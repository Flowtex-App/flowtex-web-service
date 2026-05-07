package com.flowtex.Workflow.Domain.Model.Aggregates;

import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.Shared.Domain.Model.Aggregates.AuditableAbstractAggregateRoot;
import com.flowtex.Workflow.Domain.Model.Entities.WorkflowStep;
import com.flowtex.Workflow.Domain.Model.ValueObjects.WorkflowStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "workflows")
public class Workflow extends AuditableAbstractAggregateRoot<Workflow> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkflowStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<WorkflowStep> steps = new ArrayList<>();

    public Workflow() {}

    public Workflow(String name, String description, User owner) {
        this.name = name;
        this.description = description;
        this.status = WorkflowStatus.DRAFT;
        this.owner = owner;
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void replaceSteps(List<WorkflowStep> next) {
        this.steps.clear();
        for (WorkflowStep s : next) {
            s.attachToWorkflow(this);
            this.steps.add(s);
        }
    }

    public void publish() {
        this.status = WorkflowStatus.PUBLISHED;
    }

    public void archive() {
        this.status = WorkflowStatus.ARCHIVED;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public WorkflowStatus getStatus() { return status; }
    public User getOwner() { return owner; }

    public List<WorkflowStep> getOrderedSteps() {
        List<WorkflowStep> copy = new ArrayList<>(steps);
        copy.sort(Comparator.comparingInt(WorkflowStep::getPosition));
        return copy;
    }
}
