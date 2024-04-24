package chat.common;

public class ServerMessage {
    private ServerMessageType type;
    private String message;

    public ServerMessage(ServerMessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    public ServerMessage() {}

    public String getMessage() {
        return this.message;
    }

    public ServerMessageType getType() {
        return this.type;
    }
}
