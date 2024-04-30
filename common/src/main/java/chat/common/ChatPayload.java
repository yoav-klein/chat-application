package chat.common;

public class ChatPayload {
    public ChatMessageType type;
    public String from;
    public String message;

    ChatPayload() {}

    ChatPayload(ChatMessageType type, String from, String message) {
        this.type = type;
        this.from = from;
        this.message = message;
    }
    
}
