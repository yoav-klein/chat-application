package chat.common.request;

public class StopClientRequest extends Request {
    public StopClientRequest(int requestId) {
        super(RequestType.STOP_CLIENT, requestId);
    }
    
}
