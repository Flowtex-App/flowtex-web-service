package com.flowtex.Workflow.Domain.Model.Entities;

import com.flowtex.Workflow.Domain.Model.ValueObjects.SectionKind;
import jakarta.persistence.*;

@Entity
@Table(name = "workflow_step_sections")
public class WorkflowStepSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "step_id", nullable = false)
    private WorkflowStep step;

    @Column(name = "position", nullable = false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(name = "section_kind", nullable = false, length = 40)
    private SectionKind sectionKind;

    @Column(nullable = false, length = 160)
    private String label;

    @Column(nullable = false)
    private boolean required;

    @Column(columnDefinition = "TEXT")
    private String config;

    public WorkflowStepSection() {}

    public WorkflowStepSection(SectionKind sectionKind, String label, boolean required, int position, String config) {
        this.sectionKind = sectionKind;
        this.label = label;
        this.required = required;
        this.position = position;
        this.config = config;
    }

    public void attachToStep(WorkflowStep step) { this.step = step; }

    public void update(SectionKind sectionKind, String label, boolean required, int position, String config) {
        this.sectionKind = sectionKind;
        this.label = label;
        this.required = required;
        this.position = position;
        this.config = config;
    }

    public Long getId() { return id; }
    public WorkflowStep getStep() { return step; }
    public int getPosition() { return position; }
    public SectionKind getSectionKind() { return sectionKind; }
    public String getLabel() { return label; }
    public boolean isRequired() { return required; }
    public String getConfig() { return config; }
}
