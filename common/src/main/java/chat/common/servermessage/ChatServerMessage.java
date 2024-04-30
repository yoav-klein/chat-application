package chat.common.servermessage;

public class ChatServerMessage extends ServerMessage {
    public ChatPayload payload;

    public ChatServerMessage(ChatMessageType type, String from, String to, String message) {
        super(ServerMessageType.CHAT);
        payload = new ChatPayload(type, from, to, message);
    }
    
}
