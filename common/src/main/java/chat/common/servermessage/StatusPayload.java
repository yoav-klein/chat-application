package chat.common.servermessage;

public class StatusPayload {
    public StatusMessageType status;
    public String message;
    public int requestId;

    public StatusPayload() {}

    public StatusPayload(int requestId, StatusMessageType status, String message) {
        this.requestId = requestId;
        this.status = status;
        this.message = message;
    }
}
