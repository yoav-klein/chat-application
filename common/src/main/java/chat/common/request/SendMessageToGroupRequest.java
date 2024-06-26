
package chat.common.request;

public class SendMessageToGroupRequest extends Request {
    private String toGroup;
    private String message;

    public SendMessageToGroupRequest() {
        super(RequestType.SEND_MESSAGE_TO_GROUP);
    }

    public SendMessageToGroupRequest(Integer requestId, String toGroup, String message) {
        super(RequestType.SEND_MESSAGE_TO_GROUP, requestId);
        this.toGroup = toGroup;
        this.message = message;
    }

    public String getToGroup() {
        return this.toGroup;
    }

    public String getMessage() {
        return this.message;
    }
}