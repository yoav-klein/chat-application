
package chat.common.request;

public class SendMessageToUserRequest extends Request {
    private String toUser;
    private String message;

    public SendMessageToUserRequest() {
        super(RequestType.SEND_MESSAGE_TO_USER);
    }

    public SendMessageToUserRequest(Integer requestId, String toUser, String message) {
        super(RequestType.SEND_MESSAGE_TO_USER, requestId);
        this.toUser = toUser;
        this.message = message;
    }

    public String getToUser() {
        return this.toUser;
    }

    public String getMessage() {
        return this.message;
    }
}