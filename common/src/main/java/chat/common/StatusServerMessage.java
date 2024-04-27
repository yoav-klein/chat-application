package chat.common;

public class StatusServerMessage extends ServerMessage {
    public StatusPayload payload;

    public StatusServerMessage(int requestId, ServerMessageStatusType status, String message) {
        super(ServerMessageType.STATUS);
        this.payload = new StatusPayload(requestId, status, message);
    }

    
    public StatusServerMessage() {
        this(0, ServerMessageStatusType.SUCCESS, "");
    }

}
