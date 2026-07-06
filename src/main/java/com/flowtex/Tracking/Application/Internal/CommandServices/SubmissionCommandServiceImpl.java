package com.flowtex.Tracking.Application.Internal.CommandServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowtex.FormBuilder.Domain.Model.Aggregates.Form;
import com.flowtex.FormBuilder.Domain.Model.Entities.FormField;
import com.flowtex.FormBuilder.Domain.Repositories.FormRepository;
import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Domain.Repositories.UserRepository;
import com.flowtex.Tracking.Application.Internal.OutboundServices.WorkflowEngine;
import com.flowtex.Tracking.Domain.Model.Aggregates.Submission;
import com.flowtex.Tracking.Domain.Model.Commands.CreateSubmissionCommand;
import com.flowtex.Tracking.Domain.Model.Commands.DecideStepCommand;
import com.flowtex.Tracking.Domain.Model.Commands.UpdateSubmissionDataCommand;
import com.flowtex.Tracking.Domain.Model.Entities.SubmissionAuditEvent;
import com.flowtex.Tracking.Domain.Model.Entities.SubmissionStepExecution;
import com.flowtex.Tracking.Domain.Model.ValueObjects.AuditEventType;
import com.flowtex.Tracking.Domain.Model.ValueObjects.StepExecutionStatus;
import com.flowtex.Tracking.Domain.Model.ValueObjects.SubmissionStatus;
import com.flowtex.Tracking.Domain.Repositories.SubmissionRepository;
import com.flowtex.Tracking.Domain.Repositories.TicketSequenceRepository;
import com.flowtex.Tracking.Domain.Services.SubmissionCommandService;
import com.flowtex.Workflow.Domain.Model.Aggregates.Workflow;
import com.flowtex.Workflow.Domain.Model.Entities.WorkflowStep;
import com.flowtex.Workflow.Domain.Model.Entities.WorkflowStepApprover;
import com.flowtex.Workflow.Domain.Model.Entities.WorkflowStepTransition;
import com.flowtex.Workflow.Domain.Repositories.WorkflowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SubmissionCommandServiceImpl implements SubmissionCommandService {

    private final SubmissionRepository submissionRepository;
    private final FormRepository formRepository;
    private final WorkflowRepository workflowRepository;
    private final UserRepository userRepository;
    private final TicketSequenceRepository ticketSequence;
    private final WorkflowEngine engine;
    private final ObjectMapper mapper = new ObjectMapper();

    public SubmissionCommandServiceImpl(SubmissionRepository submissionRepository,
                                        FormRepository formRepository,
                                        WorkflowRepository workflowRepository,
                                        UserRepository userRepository,
                                        TicketSequenceRepository ticketSequence,
                                        WorkflowEngine engine) {
        this.submissionRepository = submissionRepository;
        this.formRepository = formRepository;
        this.workflowRepository = workflowRepository;
        this.userRepository = userRepository;
        this.ticketSequence = ticketSequence;
        this.engine = engine;
    }

    @Override
    @Transactional
    public Optional<Submission> handle(CreateSubmissionCommand command) {
        Form form = formRepository.findById(command.formId())
                .orElseThrow(() -> new IllegalArgumentException("Form no encontrado: " + command.formId()));
        User submitter = userRepository.findById(command.submitterId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Workflow workflow = form.getWorkflowId() == null
                ? null
                : workflowRepository.findById(form.getWorkflowId()).orElse(null);

        String formSnapshot = serializeForm(form);
        String workflowSnapshot = workflow == null ? null : serializeWorkflow(workflow);
        String dataJson = toJson(command.data());

        String ticket = ticketSequence.nextTicketCode();

        Submission submission = new Submission(
                ticket, form.getId(), form.getVersion(),
                workflow == null ? null : workflow.getId(),
                formSnapshot, workflowSnapshot,
                submitter.getId(), dataJson);

        submission.appendAudit(new SubmissionAuditEvent(
                AuditEventType.CREATED, submitter.getId(), submitter.getFullName(),
                null, null, null, null,
                "Solicitud creada (" + ticket + ") sobre el formulario \"" + form.getTitle() + "\".",
                null));
        submission.appendAudit(new SubmissionAuditEvent(
                AuditEventType.SUBMITTED, submitter.getId(), submitter.getFullName(),
                null, null, null, null,
                "Solicitud enviada a aprobación.", null));

        engine.initialize(submission, submitter.getFullName());
        return Optional.of(submissionRepository.save(submission));
    }

    @Override
    @Transactional
    public Optional<Submission> handle(UpdateSubmissionDataCommand command) {
        Submission submission = submissionRepository.findById(command.submissionId())
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        if (submission.getStatus() == SubmissionStatus.APPROVED ||
            submission.getStatus() == SubmissionStatus.REJECTED ||
            submission.getStatus() == SubmissionStatus.CANCELED) {
            throw new IllegalStateException("No se puede editar una solicitud cerrada");
        }
        // Solo el solicitante puede editar
        if (!submission.getSubmitterId().equals(command.actorUserId())) {
            throw new IllegalStateException("Sólo el solicitante puede editar los datos");
        }

        Map<String, Object> oldData = parseData(submission.getDataJson());
        Map<String, Object> newData = command.data();
        User actor = userRepository.findById(command.actorUserId()).orElse(null);
        String actorLabel = actor != null ? actor.getFullName() : null;

        // Audit por campo: una entrada por cada cambio detectado
        Map<String, String> labelByKey = formFieldLabels(submission);
        for (Map.Entry<String, Object> e : newData.entrySet()) {
            Object old = oldData.get(e.getKey());
            if (!java.util.Objects.equals(old, e.getValue())) {
                submission.appendAudit(new SubmissionAuditEvent(
                        AuditEventType.FIELD_CHANGED, command.actorUserId(), actorLabel,
                        e.getKey(), labelByKey.getOrDefault(e.getKey(), e.getKey()),
                        old == null ? null : old.toString(),
                        e.getValue() == null ? null : e.getValue().toString(),
                        "Campo \"" + labelByKey.getOrDefault(e.getKey(), e.getKey()) + "\" actualizado.",
                        null));
            }
        }
        submission.updateData(toJson(newData));
        return Optional.of(submissionRepository.save(submission));
    }

    @Override
    @Transactional
    public Optional<Submission> handle(DecideStepCommand command) {
        Submission submission = submissionRepository.findById(command.submissionId())
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        if (submission.getStatus() == SubmissionStatus.APPROVED ||
            submission.getStatus() == SubmissionStatus.REJECTED ||
            submission.getStatus() == SubmissionStatus.CANCELED) {
            throw new IllegalStateException("La solicitud ya está cerrada");
        }

        SubmissionStepExecution exec = submission.getOrderedExecutions().stream()
                .filter(s -> s.getId() != null && s.getId().equals(command.stepExecutionId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Paso no encontrado en la solicitud"));
        if (exec.getStatus() != StepExecutionStatus.PENDING && exec.getStatus() != StepExecutionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Este paso ya fue decidido");
        }

        User actor = userRepository.findById(command.actorUserId())
                .orElseThrow(() -> new IllegalArgumentException("Aprobador no encontrado"));

        exec.recordDecision(command.decision(), command.comments(), actor.getId());
        submission.appendAudit(new SubmissionAuditEvent(
                auditTypeFor(command.decision()), actor.getId(), actor.getFullName(),
                null, null, null, null,
                "Paso \"" + exec.getStepLabel() + "\": "
                        + describe(command.decision())
                        + (command.comments() != null && !command.comments().isBlank()
                            ? " — " + command.comments() : ""),
                null));

        engine.onDecision(submission, exec, command.decision(), actor.getFullName());
        return Optional.of(submissionRepository.save(submission));
    }

    @Override
    @Transactional
    public void cancel(Long submissionId, Long actorUserId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        User actor = userRepository.findById(actorUserId).orElse(null);
        submission.markCanceled();
        submission.appendAudit(new SubmissionAuditEvent(
                AuditEventType.CANCELED, actorUserId, actor != null ? actor.getFullName() : null,
                null, null, null, null,
                "Solicitud cancelada.", null));
        submissionRepository.save(submission);
    }

    @Override
    @Transactional
    public Optional<Submission> resubmit(Long submissionId, Long actorUserId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        if (submission.getStatus() != SubmissionStatus.RETURNED) {
            throw new IllegalStateException("Solo se pueden reenviar solicitudes en estado RETURNED");
        }
        User actor = userRepository.findById(actorUserId).orElse(null);
        submission.markInProgress();
        submission.appendAudit(new SubmissionAuditEvent(
                AuditEventType.RESUBMITTED, actorUserId, actor != null ? actor.getFullName() : null,
                null, null, null, null,
                "Solicitud reenviada tras corrección.", null));

        // Vuelve a poner en cola el último step decidido (o reinicia desde el primero)
        SubmissionStepExecution last = submission.getOrderedExecutions().stream()
                .reduce((a, b) -> b).orElse(null);
        if (last != null && last.getStatus() == StepExecutionStatus.RETURNED) {
            engine.advanceAfter(submission, last,
                    com.flowtex.Tracking.Domain.Model.ValueObjects.Decision.APPROVE,
                    actor != null ? actor.getFullName() : null);
            // No cuenta como "approve" en audit, pero re-encola el step siguiente
        } else {
            engine.initialize(submission, actor != null ? actor.getFullName() : null);
        }
        return Optional.of(submissionRepository.save(submission));
    }

    // ─── helpers ────────────────────────────────────────────────────────

    private AuditEventType auditTypeFor(com.flowtex.Tracking.Domain.Model.ValueObjects.Decision d) {
        return switch (d) {
            case APPROVE -> AuditEventType.STEP_APPROVED;
            case REJECT  -> AuditEventType.STEP_REJECTED;
            case RETURN  -> AuditEventType.STEP_RETURNED;
        };
    }

    private String describe(com.flowtex.Tracking.Domain.Model.ValueObjects.Decision d) {
        return switch (d) {
            case APPROVE -> "aprobado";
            case REJECT  -> "rechazado";
            case RETURN  -> "devuelto al solicitante";
        };
    }

    private String serializeForm(Form form) {
        Map<String, Object> snap = new LinkedHashMap<>();
        snap.put("id", form.getId());
        snap.put("title", form.getTitle());
        snap.put("description", form.getDescription());
        snap.put("version", form.getVersion());
        List<Map<String, Object>> fields = form.getFields().stream().map(this::serializeField).toList();
        snap.put("fields", fields);
        return toJson(snap);
    }

    private Map<String, Object> serializeField(FormField f) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", f.getId());
        m.put("label", f.getLabel());
        m.put("fieldKey", f.getFieldKey());
        m.put("fieldType", f.getFieldType().name());
        m.put("required", f.isRequired());
        m.put("placeholder", f.getPlaceholder());
        m.put("helpText", f.getHelpText());
        m.put("position", f.getPosition());
        m.put("width", f.getWidth());
        m.put("colStart", f.getColStart());
        m.put("rowStart", f.getRowStart());
        m.put("rowSpan", f.getRowSpan());
        m.put("options", f.getOptions());
        return m;
    }

    private String serializeWorkflow(Workflow w) {
        Map<String, Object> snap = new LinkedHashMap<>();
        snap.put("id", w.getId());
        snap.put("name", w.getName());
        List<Map<String, Object>> steps = w.getOrderedSteps().stream().map(this::serializeStep).toList();
        snap.put("steps", steps);
        return toJson(snap);
    }

    private Map<String, Object> serializeStep(WorkflowStep s) {
        Map<String, Object> m = new LinkedHashMap<>();
        // ref usa el id si existe, sino una marca temporal
        m.put("ref", s.getId() != null ? "s-" + s.getId() : "s-tmp-" + s.hashCode());
        m.put("label", s.getLabel());
        m.put("position", s.getPosition());
        m.put("role", s.getRole());
        m.put("mode", s.getMode().name());

        List<Map<String, Object>> approvers = s.getOrderedApprovers().stream()
                .map(this::serializeApprover).toList();
        m.put("approvers", approvers);

        List<Map<String, Object>> transitions = s.getOrderedTransitions().stream()
                .map(this::serializeTransition).toList();
        m.put("transitions", transitions);
        return m;
    }

    private Map<String, Object> serializeApprover(WorkflowStepApprover a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("kind", a.getKind().name());
        m.put("userId", a.getUserId());
        if (a.getUserId() != null) {
            userRepository.findById(a.getUserId()).ifPresent(u -> {
                m.put("userLabel", u.getFullName());
                m.put("userEmployeeCode", u.getEmployeeCode());
            });
        }
        m.put("area", a.getArea());
        m.put("userPosition", a.getUserPosition());
        m.put("role", a.getRole());
        return m;
    }

    private Map<String, Object> serializeTransition(WorkflowStepTransition t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("toStepRef", t.getToStep() != null ? "s-" + t.getToStep().getId() : null);
        m.put("conditionKind", t.getConditionKind().name());
        m.put("label", t.getLabel());
        m.put("position", t.getPosition());
        m.put("config", t.getConfig());
        return m;
    }

    private Map<String, String> formFieldLabels(Submission submission) {
        try {
            Map<String, Object> snap = mapper.readValue(submission.getFormSnapshot(),
                    new com.fasterxml.jackson.core.type.TypeReference<>() {});
            Map<String, String> out = new LinkedHashMap<>();
            Object fields = snap.get("fields");
            if (fields instanceof List<?> list) {
                for (Object f : list) {
                    if (f instanceof Map<?, ?> fm) {
                        Object key = fm.get("fieldKey");
                        Object label = fm.get("label");
                        if (key != null) out.put(key.toString(), label == null ? key.toString() : label.toString());
                    }
                }
            }
            return out;
        } catch (Exception e) {
            return Map.of();
        }
    }

    private Map<String, Object> parseData(String json) {
        try {
            if (json == null || json.isBlank()) return new LinkedHashMap<>();
            return mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {});
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    private String toJson(Object o) {
        try { return mapper.writeValueAsString(o == null ? Map.of() : o); }
        catch (Exception e) { return "{}"; }
    }
}
