package chat.common.request;

public class CreateGroupRequest extends Request {
    public String groupName;

    public CreateGroupRequest() {
        this(0, "deadbeef");
    }

    public CreateGroupRequest(int requestId, String groupName) {
        super(RequestType.CREATE_GROUP, requestId);
        this.groupName = groupName;
    }
    
    
}
