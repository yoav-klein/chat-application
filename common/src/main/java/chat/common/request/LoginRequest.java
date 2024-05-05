
package chat.common.request;

public class LoginRequest extends Request {
    private String userName;

    public LoginRequest() {
        super(RequestType.LOGIN);
    }
    
    public LoginRequest(Integer requestId, String userName) {
        super(RequestType.LOGIN, requestId);
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }
}