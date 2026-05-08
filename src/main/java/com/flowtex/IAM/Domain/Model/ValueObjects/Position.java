package com.flowtex.IAM.Domain.Model.ValueObjects;

public enum Position {
    PRACTICANTE("Practicante"),
    ANALISTA("Analista"),
    JEFE("Jefe"),
    GERENTE("Gerente"),
    SUBDIRECTOR("Subdirector"),
    DIRECTOR("Director");

    private final String label;

    Position(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
