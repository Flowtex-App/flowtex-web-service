package com.flowtex.FormBuilder.Domain.Model.Entities;

import com.flowtex.FormBuilder.Domain.Model.Aggregates.Form;
import com.flowtex.FormBuilder.Domain.Model.ValueObjects.FieldType;
import jakarta.persistence.*;

@Entity
@Table(name = "form_fields")
public class FormField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @Column(nullable = false, length = 160)
    private String label;

    @Column(name = "field_key", nullable = false, length = 80)
    private String fieldKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false, length = 30)
    private FieldType fieldType;

    @Column(nullable = false)
    private boolean required;

    @Column(length = 160)
    private String placeholder;

    @Column(name = "help_text", length = 255)
    private String helpText;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(columnDefinition = "TEXT")
    private String options;

    public FormField() {
    }

    public FormField(String label, String fieldKey, FieldType fieldType, boolean required,
                     String placeholder, String helpText, int position, String options) {
        this.label = label;
        this.fieldKey = fieldKey;
        this.fieldType = fieldType;
        this.required = required;
        this.placeholder = placeholder;
        this.helpText = helpText;
        this.position = position;
        this.options = options;
    }

    public void attachToForm(Form form) {
        this.form = form;
    }

    public void update(String label, String fieldKey, FieldType fieldType, boolean required,
                       String placeholder, String helpText, int position, String options) {
        this.label = label;
        this.fieldKey = fieldKey;
        this.fieldType = fieldType;
        this.required = required;
        this.placeholder = placeholder;
        this.helpText = helpText;
        this.position = position;
        this.options = options;
    }

    public Long getId() { return id; }
    public Form getForm() { return form; }
    public String getLabel() { return label; }
    public String getFieldKey() { return fieldKey; }
    public FieldType getFieldType() { return fieldType; }
    public boolean isRequired() { return required; }
    public String getPlaceholder() { return placeholder; }
    public String getHelpText() { return helpText; }
    public int getPosition() { return position; }
    public String getOptions() { return options; }
}
