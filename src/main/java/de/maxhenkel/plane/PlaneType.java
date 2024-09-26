package de.maxhenkel.plane;

public enum PlaneType {
    OAK("oak"),
    SPRUCE("spruce"),
    BIRCH("birch"),
    JUNGLE("jungle"),
    ACACIA("acacia"),
    DARK_OAK("dark_oak"),
    WARPED("warped"),
    CRIMSON("crimson");

    private final String name;

    PlaneType(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return name;
    }

    public static PlaneType fromTypeName(String name) {
        for (PlaneType type : values()) {
            if (type.getTypeName().equals(name)) {
                return type;
            }
        }
        return OAK;
    }
}
