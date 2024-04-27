package chat.common;

public class StatusPayload {
    public ServerMessageStatusType status;
    public String message;
    public int requestId;

    public StatusPayload() {}

    public StatusPayload(int requestId, ServerMessageStatusType status, String messsage) {
        this.requestId = requestId;
        this.status = status;
        this.message = messsage;
    }
}
