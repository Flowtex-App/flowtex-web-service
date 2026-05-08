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
    SECTION,
    SPACER,

    // Auto-filled from current user metadata at render/submit time
    AUTO_USER_NAME,
    AUTO_EMPLOYEE_CODE,
    AUTO_POSITION,
    AUTO_AREA
}
