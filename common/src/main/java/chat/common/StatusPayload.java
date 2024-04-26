package chat.common;

public class StatusPayload {
    public ServerMessageStatusType status;
    public String message;

    public StatusPayload() {}

    public StatusPayload(ServerMessageStatusType status, String messsage) {
        this.status = status;
        this.message = messsage;
    }
}
