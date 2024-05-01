package chat.common.request;

public class ListUsersInGroupRequest extends Request {
    private String groupName;

    public ListUsersInGroupRequest() {
        super(RequestType.LIST_USERS_IN_GROUP);
    }
    
    public ListUsersInGroupRequest(Integer requestId, String groupName) {
        super(RequestType.LIST_USERS_IN_GROUP, requestId);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
