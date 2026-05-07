package com.flowtex.FormBuilder.Domain.Model.Aggregates;

import com.flowtex.FormBuilder.Domain.Model.Entities.FormField;
import com.flowtex.FormBuilder.Domain.Model.ValueObjects.FormStatus;
import com.flowtex.Shared.Domain.Model.Aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "forms")
public class Form extends AuditableAbstractAggregateRoot<Form> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String context;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FormStatus status;

    @Column(nullable = false)
    private int version;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("position ASC")
    private List<FormField> fields = new ArrayList<>();

    protected Form() {
    }

    public Form(String title, String description, String context, Long ownerId) {
        this.title = title;
        this.description = description;
        this.context = context;
        this.ownerId = ownerId;
        this.status = FormStatus.DRAFT;
        this.version = 1;
    }

    public void update(String title, String description, String context) {
        this.title = title;
        this.description = description;
        this.context = context;
        this.version += 1;
    }

    public void replaceFields(List<FormField> newFields) {
        this.fields.clear();
        for (FormField field : newFields) {
            field.attachToForm(this);
            this.fields.add(field);
        }
    }

    public void publish() {
        if (this.fields.isEmpty()) {
            throw new IllegalStateException("Cannot publish a form with no fields");
        }
        this.status = FormStatus.PUBLISHED;
    }

    public void archive() {
        this.status = FormStatus.ARCHIVED;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContext() { return context; }
    public FormStatus getStatus() { return status; }
    public int getVersion() { return version; }
    public Long getOwnerId() { return ownerId; }
    public List<FormField> getFields() { return fields; }
}
