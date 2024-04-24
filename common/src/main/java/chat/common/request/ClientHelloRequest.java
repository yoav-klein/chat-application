
package chat.common.request;

public class ClientHelloRequest extends Request {
    private String userName;

    public ClientHelloRequest() {
        super(RequestType.CLIENT_HELLO);
    }
    
    public ClientHelloRequest(String userName, Integer requestId) {
        super(RequestType.CLIENT_HELLO, requestId);
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }
}