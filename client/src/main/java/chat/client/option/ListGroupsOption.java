package chat.client.option;

import java.util.HashMap;
import java.util.Map;

import chat.client.Parameter;
import chat.common.request.*;

public class ListGroupsOption implements Option {
    final String description;
    
    public ListGroupsOption() {
        description = "List groups you are in";
    }

    // Getters and setters
    public String getDescription() {
        return description;
    }
         
    public Map<Integer, Parameter> getParameters() { 
        Map<Integer, Parameter> params = new HashMap<>();
        
        return params;
    };

    public Request createRequest(Map<Integer, Object> values) {

        return new ListGroupsRequest(0); // 0 is a dummy value for requestId
    }

    
}
