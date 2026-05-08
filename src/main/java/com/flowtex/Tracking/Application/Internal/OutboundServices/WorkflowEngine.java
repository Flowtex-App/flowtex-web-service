package com.flowtex.Tracking.Application.Internal.OutboundServices;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Domain.Model.ValueObjects.Area;
import com.flowtex.IAM.Domain.Model.ValueObjects.Position;
import com.flowtex.IAM.Domain.Repositories.UserRepository;
import com.flowtex.Tracking.Domain.Model.Aggregates.Submission;
import com.flowtex.Tracking.Domain.Model.Entities.SubmissionAuditEvent;
import com.flowtex.Tracking.Domain.Model.Entities.SubmissionStepExecution;
import com.flowtex.Tracking.Domain.Model.ValueObjects.AssignmentKind;
import com.flowtex.Tracking.Domain.Model.ValueObjects.AuditEventType;
import com.flowtex.Tracking.Domain.Model.ValueObjects.Decision;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Motor de ejecución de workflows sobre snapshots persistidos en submissions.
 *
 * Trabaja exclusivamente con el snapshot JSON guardado en la submission:
 * cualquier cambio posterior al workflow original NO afecta a las submissions
 * en curso (persistencia del flujo de aprobación, requisito del cliente).
 *
 * Soporta cuatro tipos de condición:
 *   - ALWAYS: la transición siempre se toma.
 *   - ON_APPROVE / ON_REJECT / ON_RETURN: matchean la decisión del aprobador.
 *   - CUSTOM: evalúa una expresión simple contra los datos del formulario.
 *     Config JSON: {"field":"tipo_solicitud","operator":"EQUALS","value":"compra"}
 *     Operadores: EQUALS, NOT_EQUALS, CONTAINS, GT, LT, GTE, LTE.
 *
 * Las transiciones se evalúan en orden de `position`; la primera que matchea
 * dicta el siguiente paso. Si ninguna matchea, el flujo termina con el estado
 * derivado de la decisión (APPROVE → APPROVED, REJECT → REJECTED).
 */
@Component
public class WorkflowEngine {

    private final ObjectMapper mapper = new ObjectMapper();
    private final UserRepository userRepository;

    public WorkflowEngine(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Crea la primera step execution para una submission recién enviada.
     * Si el snapshot no tiene workflow o no tiene pasos, marca la submission
     * como APROBADA al instante (no hay nada que aprobar).
     */
    public void initialize(Submission submission, String actorLabel) {
        if (submission.getWorkflowSnapshot() == null) {
            submission.markApproved();
            submission.appendAudit(new SubmissionAuditEvent(
                    AuditEventType.WORKFLOW_COMPLETED, submission.getSubmitterId(), actorLabel,
                    null, null, null, null,
                    "Solicitud sin flujo de aprobación: completada al envío.", null));
            return;
        }
        WorkflowSnapshot snap = parseWorkflow(submission.getWorkflowSnapshot());
        Optional<StepSnapshot> entry = snap.steps.stream()
                .min((a, b) -> Integer.compare(a.position, b.position));
        if (entry.isEmpty()) {
            submission.markApproved();
            submission.appendAudit(new SubmissionAuditEvent(
                    AuditEventType.WORKFLOW_COMPLETED, submission.getSubmitterId(), actorLabel,
                    null, null, null, null,
                    "Workflow vacío: completado al envío.", null));
            return;
        }
        enqueueStep(submission, entry.get(), actorLabel);
    }

    /**
     * Tras la decisión sobre el step actual, evalúa transitions y crea el
     * siguiente step execution, o cierra la submission si no hay continuación.
     */
    public void advanceAfter(Submission submission, SubmissionStepExecution current,
                             Decision decision, String actorLabel) {
        if (submission.getWorkflowSnapshot() == null) return;
        WorkflowSnapshot snap = parseWorkflow(submission.getWorkflowSnapshot());
        Map<String, Object> data = parseDataMap(submission.getDataJson());

        StepSnapshot currentSnap = snap.steps.stream()
                .filter(s -> Objects.equals(s.ref, current.getStepRef()))
                .findFirst().orElse(null);
        if (currentSnap == null) {
            // step no encontrado en snapshot: comportamiento defensivo
            finalizeBy(submission, decision, actorLabel);
            return;
        }

        // RETURN no avanza el workflow: simplemente devuelve al solicitante.
        if (decision == Decision.RETURN) {
            submission.markReturned();
            submission.appendAudit(new SubmissionAuditEvent(
                    AuditEventType.STEP_RETURNED, current.getDecidedByUserId(), actorLabel,
                    null, null, null, null,
                    "Paso \"" + current.getStepLabel() + "\" devuelto al solicitante.", null));
            return;
        }

        TransitionSnapshot taken = pickTransition(currentSnap.transitions, decision, data);
        if (taken == null) {
            // sin transición matcheada: el resultado del paso decide el cierre
            finalizeBy(submission, decision, actorLabel);
            return;
        }

        if (taken.toStepRef == null) {
            // transition explícita al fin
            finalizeBy(submission, decision, actorLabel);
            return;
        }

        StepSnapshot next = snap.steps.stream()
                .filter(s -> Objects.equals(s.ref, taken.toStepRef))
                .findFirst().orElse(null);
        if (next == null) {
            finalizeBy(submission, decision, actorLabel);
            return;
        }

        enqueueStep(submission, next, actorLabel);
    }

    // ─── pasos privados ─────────────────────────────────────────────────

    private void enqueueStep(Submission submission, StepSnapshot step, String actorLabel) {
        ApproverResolution res = resolveApprover(step);
        SubmissionStepExecution exec = new SubmissionStepExecution(
                step.ref, step.label, step.position,
                res.kind,
                res.userId, res.userLabel,
                res.area, res.userPosition, res.role
        );
        submission.appendStepExecution(exec);
        submission.setCurrentStepRef(step.ref);
        submission.markInProgress();
        submission.appendAudit(new SubmissionAuditEvent(
                AuditEventType.STEP_ASSIGNED, null, actorLabel,
                null, null, null, null,
                "Paso \"" + step.label + "\" en cola — asignado a " + res.describe() + ".", null));
    }

    private void finalizeBy(Submission submission, Decision decision, String actorLabel) {
        switch (decision) {
            case APPROVE -> {
                submission.markApproved();
                submission.appendAudit(new SubmissionAuditEvent(
                        AuditEventType.WORKFLOW_COMPLETED, null, actorLabel,
                        null, null, null, null,
                        "Solicitud aprobada — fin del flujo.", null));
            }
            case REJECT -> {
                submission.markRejected();
                submission.appendAudit(new SubmissionAuditEvent(
                        AuditEventType.WORKFLOW_COMPLETED, null, actorLabel,
                        null, null, null, null,
                        "Solicitud rechazada — fin del flujo.", null));
            }
            case RETURN -> submission.markReturned();
        }
    }

    private TransitionSnapshot pickTransition(List<TransitionSnapshot> transitions,
                                              Decision decision, Map<String, Object> data) {
        if (transitions == null) return null;
        return transitions.stream()
                .sorted((a, b) -> Integer.compare(a.position, b.position))
                .filter(t -> matches(t, decision, data))
                .findFirst().orElse(null);
    }

    private boolean matches(TransitionSnapshot t, Decision decision, Map<String, Object> data) {
        String kind = t.conditionKind == null ? "ALWAYS" : t.conditionKind;
        switch (kind) {
            case "ALWAYS":     return true;
            case "ON_APPROVE": return decision == Decision.APPROVE;
            case "ON_REJECT":  return decision == Decision.REJECT;
            case "ON_RETURN":  return decision == Decision.RETURN;
            case "CUSTOM":     return evaluateCustom(t.config, data);
            default:           return false;
        }
    }

    private boolean evaluateCustom(String configJson, Map<String, Object> data) {
        if (configJson == null || configJson.isBlank()) return false;
        try {
            CustomCondition cond = mapper.readValue(configJson, CustomCondition.class);
            Object actual = data.get(cond.field);
            return compare(actual, cond.operator, cond.value);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean compare(Object actual, String op, Object expected) {
        if (op == null) return false;
        switch (op.toUpperCase()) {
            case "EQUALS":     return Objects.equals(asString(actual), asString(expected));
            case "NOT_EQUALS": return !Objects.equals(asString(actual), asString(expected));
            case "CONTAINS":   return asString(actual).contains(asString(expected));
            case "GT":         return asNumber(actual) >  asNumber(expected);
            case "LT":         return asNumber(actual) <  asNumber(expected);
            case "GTE":        return asNumber(actual) >= asNumber(expected);
            case "LTE":        return asNumber(actual) <= asNumber(expected);
            default:           return false;
        }
    }

    private static String asString(Object o) {
        return o == null ? "" : o.toString();
    }

    private static double asNumber(Object o) {
        if (o == null) return 0;
        if (o instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(o.toString()); } catch (Exception e) { return 0; }
    }

    private ApproverResolution resolveApprover(StepSnapshot step) {
        if (step.approvers != null && !step.approvers.isEmpty()) {
            ApproverSnapshot first = step.approvers.get(0);
            switch (first.kind) {
                case "USER":
                    if (first.userId != null) {
                        Optional<User> u = userRepository.findById(first.userId);
                        return new ApproverResolution(
                                AssignmentKind.USER,
                                first.userId,
                                u.map(User::getFullName).orElse(first.userLabel),
                                null, null, null);
                    }
                    break;
                case "AREA_POSITION":
                    return new ApproverResolution(
                            AssignmentKind.AREA_POSITION, null, null,
                            first.area, first.userPosition, null);
                case "ROLE":
                    return new ApproverResolution(
                            AssignmentKind.ROLE, null, null, null, null, first.role);
            }
        }
        // Fallback: rol legacy del step
        return new ApproverResolution(AssignmentKind.ROLE, null, null, null, null, step.role);
    }

    // ─── parsing helpers ────────────────────────────────────────────────

    private WorkflowSnapshot parseWorkflow(String json) {
        try {
            return mapper.readValue(json, WorkflowSnapshot.class);
        } catch (Exception e) {
            throw new IllegalStateException("Snapshot de workflow inválido", e);
        }
    }

    private Map<String, Object> parseDataMap(String json) {
        try {
            if (json == null || json.isBlank()) return Map.of();
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }

    // ─── DTOs internos del snapshot ─────────────────────────────────────

    public static class WorkflowSnapshot {
        public Long id;
        public String name;
        public List<StepSnapshot> steps;
    }

    public static class StepSnapshot {
        public String ref;
        public String label;
        public int position;
        public String role;
        public String mode;
        public List<ApproverSnapshot> approvers;
        public List<TransitionSnapshot> transitions;
    }

    public static class ApproverSnapshot {
        public String kind;
        public Long userId;
        public String userLabel;
        public String userEmployeeCode;
        public String area;
        public String userPosition;
        public String role;
    }

    public static class TransitionSnapshot {
        public String toStepRef;
        public String conditionKind;
        public String label;
        public int position;
        public String config;
    }

    public static class CustomCondition {
        public String field;
        public String operator;
        public Object value;
    }

    private record ApproverResolution(
            AssignmentKind kind, Long userId, String userLabel,
            String area, String userPosition, String role
    ) {
        String describe() {
            return switch (kind) {
                case USER -> userLabel != null ? userLabel : "usuario #" + userId;
                case AREA_POSITION -> userPosition + " de " + area;
                case ROLE -> role;
            };
        }
    }
}
