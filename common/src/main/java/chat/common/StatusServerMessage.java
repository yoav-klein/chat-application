package chat.common;

public class StatusServerMessage extends ServerMessage {
    public StatusPayload payload;

    public StatusServerMessage(ServerMessageStatusType status, String message) {
        super(ServerMessageType.STATUS);
        this.payload = new StatusPayload(status, message);
    }

    
    public StatusServerMessage() {
        this(ServerMessageStatusType.SUCCESS, "");
    }

}
