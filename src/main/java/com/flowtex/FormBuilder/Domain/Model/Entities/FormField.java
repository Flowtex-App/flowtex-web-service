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

    @Column(name = "width", nullable = false)
    private int width = 12;

    /** When non-null, absolute column start (1..12) in the grid. */
    @Column(name = "col_start")
    private Integer colStart;

    /** When non-null, absolute row start (1..) in the grid. */
    @Column(name = "row_start")
    private Integer rowStart;

    @Column(name = "row_span", nullable = false)
    private int rowSpan = 1;

    @Column(columnDefinition = "TEXT")
    private String options;

    public FormField() {
    }

    public FormField(String label, String fieldKey, FieldType fieldType, boolean required,
                     String placeholder, String helpText, int position, int width,
                     Integer colStart, Integer rowStart, int rowSpan, String options) {
        this.label = label;
        this.fieldKey = fieldKey;
        this.fieldType = fieldType;
        this.required = required;
        this.placeholder = placeholder;
        this.helpText = helpText;
        this.position = position;
        this.width = clampWidth(width);
        this.colStart = clampCol(colStart);
        this.rowStart = clampRow(rowStart);
        this.rowSpan = Math.max(1, rowSpan);
        this.options = options;
    }

    public void attachToForm(Form form) {
        this.form = form;
    }

    public void update(String label, String fieldKey, FieldType fieldType, boolean required,
                       String placeholder, String helpText, int position, int width,
                       Integer colStart, Integer rowStart, int rowSpan, String options) {
        this.label = label;
        this.fieldKey = fieldKey;
        this.fieldType = fieldType;
        this.required = required;
        this.placeholder = placeholder;
        this.helpText = helpText;
        this.position = position;
        this.width = clampWidth(width);
        this.colStart = clampCol(colStart);
        this.rowStart = clampRow(rowStart);
        this.rowSpan = Math.max(1, rowSpan);
        this.options = options;
    }

    private static int clampWidth(int w) {
        if (w < 1) return 1;
        if (w > 12) return 12;
        return w;
    }

    private static Integer clampCol(Integer c) {
        if (c == null) return null;
        if (c < 1) return 1;
        if (c > 12) return 12;
        return c;
    }

    private static Integer clampRow(Integer r) {
        if (r == null) return null;
        if (r < 1) return 1;
        return r;
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
    public int getWidth() { return width; }
    public Integer getColStart() { return colStart; }
    public Integer getRowStart() { return rowStart; }
    public int getRowSpan() { return rowSpan; }
    public String getOptions() { return options; }
}
