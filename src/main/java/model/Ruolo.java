package model;

public enum Ruolo {
    STUDENTE,
    PROFESSORE,
    SEGRETERIA;

    public static Ruolo fromString(String ruoloStr) {
        if (ruoloStr == null) return null;
        String normalized = ruoloStr.toUpperCase().trim();
        if (normalized.contains("SEGRETERIA")) return SEGRETERIA;
        try {
            return Ruolo.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}