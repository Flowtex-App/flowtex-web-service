package com.flowtex.Workflow.Domain.Model.Entities;

import com.flowtex.Workflow.Domain.Model.ValueObjects.TransitionCondition;
import jakarta.persistence.*;

@Entity
@Table(name = "workflow_step_transitions")
public class WorkflowStepTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_step_id", nullable = false)
    private WorkflowStep fromStep;

    /** Null means the transition terminates the workflow (end node). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_step_id")
    private WorkflowStep toStep;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_kind", nullable = false, length = 40)
    private TransitionCondition conditionKind;

    @Column(length = 160)
    private String label;

    @Column(nullable = false)
    private int position;

    @Column(columnDefinition = "TEXT")
    private String config;

    /** Which side of the source node this edge starts from: top|right|bottom|left. */
    @Column(name = "source_handle", length = 8)
    private String sourceHandle;

    /** Which side of the target node this edge ends on: top|right|bottom|left. */
    @Column(name = "target_handle", length = 8)
    private String targetHandle;

    public WorkflowStepTransition() {}

    public WorkflowStepTransition(TransitionCondition conditionKind, String label, int position, String config,
                                  String sourceHandle, String targetHandle) {
        this.conditionKind = conditionKind;
        this.label = label;
        this.position = position;
        this.config = config;
        this.sourceHandle = sourceHandle;
        this.targetHandle = targetHandle;
    }

    public void attachFrom(WorkflowStep fromStep) { this.fromStep = fromStep; }
    public void attachTo(WorkflowStep toStep) { this.toStep = toStep; }

    public Long getId() { return id; }
    public WorkflowStep getFromStep() { return fromStep; }
    public WorkflowStep getToStep() { return toStep; }
    public TransitionCondition getConditionKind() { return conditionKind; }
    public String getLabel() { return label; }
    public int getPosition() { return position; }
    public String getConfig() { return config; }
    public String getSourceHandle() { return sourceHandle; }
    public String getTargetHandle() { return targetHandle; }
}
