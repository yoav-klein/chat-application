
package chat.common.request;

public abstract class Request {
    private final RequestType type;

    public Request(RequestType type) {
        this.type = type;
    }

    public RequestType getType() {
        return this.type;
    }

}