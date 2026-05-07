package com.flowtex.FormBuilder.Domain.Model.ValueObjects;

public enum FieldType {
    // Inputs
    TEXT,
    TEXTAREA,
    NUMBER,
    EMAIL,
    DATE,
    DATETIME,
    SELECT,
    MULTI_SELECT,
    RADIO,
    CHECKBOX,
    FILE,
    URL,
    PHONE,
    SIGNATURE,

    // Layout / display (no input data captured)
    HEADING,
    PARAGRAPH,
    DIVIDER,
    SECTION
}
