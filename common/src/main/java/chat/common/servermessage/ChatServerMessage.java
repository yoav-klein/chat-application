package chat.common.servermessage;

public class ChatServerMessage extends ServerMessage {
    public ChatPayload payload;

    public ChatServerMessage(ChatMessageType type, String from, String message) {
        super(ServerMessageType.CHAT);
        payload = new ChatPayload(type, from, message);
    }
    
}
