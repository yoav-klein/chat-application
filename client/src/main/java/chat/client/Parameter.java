package chat.client;

public class Parameter {
    private final String description;
    private final Class<?> type;

    public Parameter  (String description, Class<?> type) {
        this.description = description;
        this.type = type;
    }

    // Getters and setters
    public String getDescription() {
        return description;
    }

    public Class<?> getType() {
        return type;
    }

}