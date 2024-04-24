package chat.common;

public class ServerMessageStatus {
    private ServerMessageStatusType status;
    private String message;

    public ServerMessageStatus() {};

    public ServerMessageStatus(ServerMessageStatusType status, String message) {
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
