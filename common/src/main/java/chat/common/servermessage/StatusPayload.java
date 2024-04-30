package chat.common.servermessage;

public class StatusPayload {
    public ServerMessageStatusType status;
    public String message;
    public int requestId;

    public StatusPayload() {}

    public StatusPayload(int requestId, ServerMessageStatusType status, String message) {
        this.requestId = requestId;
        this.status = status;
        this.message = message;
    }
}
