package chat.common.request;

public class ListGroupsRequest extends Request {

    public ListGroupsRequest() {
        super(RequestType.LIST_GROUPS_OF_USER);
    }
    
    public ListGroupsRequest(Integer requestId) {
        super(RequestType.LIST_GROUPS_OF_USER, requestId);
    }

    
}

