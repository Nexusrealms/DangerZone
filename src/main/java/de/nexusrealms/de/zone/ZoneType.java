package de.nexusrealms.de.zone;

/**
 * Defines different types of danger zones with unique effects.
 */
public enum ZoneType {
    DAMAGE("damage", "Takes damage over time"),
    FOGGY("foggy", "Reduces visibility with fog"),
    SLIPPERY("slippery", "Makes blocks slippery like ice"),
    CUSTOM("custom", "Custom zone with configurable effects");
    
    private final String id;
    private final String description;
    
    ZoneType(String id, String description) {
        this.id = id;
        this.description = description;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
}