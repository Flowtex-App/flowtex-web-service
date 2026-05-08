package com.flowtex.IAM.Domain.Model.ValueObjects;

public enum Area {
    TECNOLOGIA("Tecnología"),
    FINANZAS("Finanzas"),
    MERCADO_CORPORATIVO("Mercado Corporativo"),
    RECURSOS_HUMANOS("Recursos Humanos"),
    LEGAL("Legal"),
    OPERACIONES("Operaciones"),
    COMERCIAL_MASIVO("Comercial Masivo"),
    MARKETING("Marketing"),
    ATENCION_CLIENTE("Atención al Cliente"),
    COMPRAS("Compras"),
    AUDITORIA("Auditoría");

    private final String label;

    Area(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
