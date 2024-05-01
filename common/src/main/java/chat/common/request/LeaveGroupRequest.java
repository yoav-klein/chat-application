package chat.common.request;

public class LeaveGroupRequest extends Request {
    private String groupName;

    public LeaveGroupRequest() {
        super(RequestType.LEAVE_GROUP);
    }
    
    public LeaveGroupRequest(Integer requestId, String groupName) {
        super(RequestType.LEAVE_GROUP, requestId);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
}
