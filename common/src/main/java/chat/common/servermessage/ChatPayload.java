package chat.common.servermessage;

public class ChatPayload {
    public ChatMessageType type;
    public String from;
    public String to;
    public String message;

    ChatPayload() {}

    ChatPayload(ChatMessageType type, String from, String to, String message) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.message = message;
    }
    
}
