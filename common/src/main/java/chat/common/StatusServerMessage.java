package chat.common;

public class StatusServerMessage {
    private ServerMessageStatusType status;
    private String message;

    public StatusServerMessage() {};

    public StatusServerMessage(ServerMessageStatusType status, String message) {
        this.status = status;
        this.message = message;
    }

    public ServerMessageStatusType getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }
}
