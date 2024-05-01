package chat.common.request;

public class JoinGroupRequest extends Request {
    private String groupName;

    public JoinGroupRequest() {
        super(RequestType.JOIN_GROUP);
    }
    public JoinGroupRequest(Integer requestId, String groupName) {
        super(RequestType.JOIN_GROUP, requestId);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
}
