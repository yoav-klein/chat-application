
package chat.common.request;

public class ClientHelloRequest extends Request {
    private String userName;

    public ClientHelloRequest() {
        super(RequestType.CLIENT_HELLO);
    }
    
    public ClientHelloRequest(String userName) {
        super(RequestType.CLIENT_HELLO);
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }
}