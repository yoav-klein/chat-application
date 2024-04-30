
package chat.common.request;

public abstract class Request {
    private final RequestType type;
    private Integer requestId;

    public Request(RequestType type, Integer requestId) {
        this.requestId = requestId;
        this.type = type;
    }

    public Request(RequestType type) {
        this(type, 0);
    }

    public RequestType getType() {
        return this.type;
    }

    public Integer getRequestId() {
        return this.requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

}