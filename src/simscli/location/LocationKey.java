package simscli.location;

/**
 * Typed keys for all travel locations.
 */
public enum LocationKey {
    STREET("street"),
    HOME("home"),
    PARK("park"),
    BANK("bank"),
    RESTAURANT("restaurant"),
    HOSPITAL("hospital"),
    PETSTORE("petstore"),
    CASINO("casino");

    private final String key;

    LocationKey(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public static LocationKey fromKey(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException("Location key required");
        }

        String normalized = raw.trim().toLowerCase();
        for (LocationKey value : values()) {
            if (value.key.equals(normalized)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown location key: " + raw);
    }
}
