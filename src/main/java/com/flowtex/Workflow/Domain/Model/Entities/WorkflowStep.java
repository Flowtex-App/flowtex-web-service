package com.flowtex.Workflow.Domain.Model.Entities;

import com.flowtex.Workflow.Domain.Model.Aggregates.Workflow;
import com.flowtex.Workflow.Domain.Model.ValueObjects.StepMode;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "workflow_steps")
public class WorkflowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(nullable = false, length = 160)
    private String label;

    @Column(nullable = false, length = 80)
    private String role;

    @Column(name = "sla_hours", nullable = false)
    private int slaHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StepMode mode;

    @Column(length = 500)
    private String description;

    @Column(name = "canvas_x", nullable = false)
    private int canvasX;

    @Column(name = "canvas_y", nullable = false)
    private int canvasY;

    /** Curated color name for visual differentiation (slate/emerald/amber/rose/sky/violet). Null = default. */
    @Column(length = 20)
    private String color;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<WorkflowStepSection> sections = new ArrayList<>();

    @OneToMany(mappedBy = "fromStep", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<WorkflowStepTransition> outgoingTransitions = new ArrayList<>();

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<WorkflowStepApprover> approvers = new ArrayList<>();

    public WorkflowStep() {}

    public WorkflowStep(int position, String label, String role, int slaHours, StepMode mode, String description,
                        int canvasX, int canvasY, String color) {
        this.position = position;
        this.label = label;
        this.role = role;
        this.slaHours = slaHours;
        this.mode = mode;
        this.description = description;
        this.canvasX = canvasX;
        this.canvasY = canvasY;
        this.color = color;
    }

    public void attachToWorkflow(Workflow workflow) { this.workflow = workflow; }

    public void update(int position, String label, String role, int slaHours, StepMode mode, String description,
                       int canvasX, int canvasY, String color) {
        this.position = position;
        this.label = label;
        this.role = role;
        this.slaHours = slaHours;
        this.mode = mode;
        this.description = description;
        this.canvasX = canvasX;
        this.canvasY = canvasY;
        this.color = color;
    }

    public void replaceSections(List<WorkflowStepSection> next) {
        this.sections.clear();
        for (WorkflowStepSection s : next) {
            s.attachToStep(this);
            this.sections.add(s);
        }
    }

    public void replaceTransitions(List<WorkflowStepTransition> next) {
        this.outgoingTransitions.clear();
        for (WorkflowStepTransition t : next) {
            t.attachFrom(this);
            this.outgoingTransitions.add(t);
        }
    }

    public void replaceApprovers(List<WorkflowStepApprover> next) {
        this.approvers.clear();
        for (WorkflowStepApprover a : next) {
            a.attachToStep(this);
            this.approvers.add(a);
        }
    }

    public List<WorkflowStepApprover> getOrderedApprovers() {
        List<WorkflowStepApprover> copy = new ArrayList<>(approvers);
        copy.sort(Comparator.comparingInt(WorkflowStepApprover::getPosition));
        return copy;
    }

    public Long getId() { return id; }
    public Workflow getWorkflow() { return workflow; }
    public int getPosition() { return position; }
    public String getLabel() { return label; }
    public String getRole() { return role; }
    public int getSlaHours() { return slaHours; }
    public StepMode getMode() { return mode; }
    public String getDescription() { return description; }
    public int getCanvasX() { return canvasX; }
    public int getCanvasY() { return canvasY; }
    public String getColor() { return color; }

    public List<WorkflowStepSection> getOrderedSections() {
        List<WorkflowStepSection> copy = new ArrayList<>(sections);
        copy.sort(Comparator.comparingInt(WorkflowStepSection::getPosition));
        return copy;
    }

    public List<WorkflowStepTransition> getOrderedTransitions() {
        List<WorkflowStepTransition> copy = new ArrayList<>(outgoingTransitions);
        copy.sort(Comparator.comparingInt(WorkflowStepTransition::getPosition));
        return copy;
    }
}
