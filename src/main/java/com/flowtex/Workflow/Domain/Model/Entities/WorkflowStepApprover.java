package com.flowtex.Workflow.Domain.Model.Entities;

import com.flowtex.Workflow.Domain.Model.ValueObjects.ApproverKind;
import jakarta.persistence.*;

@Entity
@Table(name = "workflow_step_approvers")
public class WorkflowStepApprover {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "step_id", nullable = false)
    private WorkflowStep step;

    @Column(name = "position", nullable = false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApproverKind kind;

    /** Sólo si kind = USER. */
    @Column(name = "user_id")
    private Long userId;

    /** Sólo si kind = AREA_POSITION. Guardamos como string para evitar acoplar enums entre contextos. */
    @Column(length = 40)
    private String area;

    /** Sólo si kind = AREA_POSITION. */
    @Column(name = "user_position", length = 20)
    private String userPosition;

    /** Sólo si kind = ROLE. */
    @Column(length = 80)
    private String role;

    public WorkflowStepApprover() {}

    public WorkflowStepApprover(int position, ApproverKind kind, Long userId,
                                String area, String userPosition, String role) {
        this.position = position;
        this.kind = kind;
        this.userId = userId;
        this.area = area;
        this.userPosition = userPosition;
        this.role = role;
    }

    public void attachToStep(WorkflowStep step) { this.step = step; }

    public Long getId() { return id; }
    public WorkflowStep getStep() { return step; }
    public int getPosition() { return position; }
    public ApproverKind getKind() { return kind; }
    public Long getUserId() { return userId; }
    public String getArea() { return area; }
    public String getUserPosition() { return userPosition; }
    public String getRole() { return role; }
}
