package chat.common;

public class ChatPayload {
    public ChatMessageType type;
    public String message;

    ChatPayload(ChatMessageType type, String message) {
        this.type = type;
        this.message = message;
    }
    
}
