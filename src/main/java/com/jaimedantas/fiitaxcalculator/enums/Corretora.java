package com.jaimedantas.fiitaxcalculator.enums;

import java.util.HashMap;
import java.util.Map;

public enum Corretora {
    CLEAR("CLEAR"),
    XP("XP"),
    RICO("RICO");

    private static final Map<String, Corretora> BY_LABEL = new HashMap<>();

    static {
        for (Corretora e : values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    public final String label;

    private Corretora(String label) {
        this.label = label;

    }

    public static Corretora valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

}
