package chat.client.option;

import java.util.HashMap;
import java.util.Map;

import chat.client.Parameter;
import chat.common.request.*;

public class CreateGroupOption extends Option {
    final String description;
    
    public CreateGroupOption() {
        description = "Create a group";
    }

    // Getters and setters
    public String getDescription() {
        return description;
    }
         
    public Map<Integer, Parameter> getParameters() { 
        Map<Integer, Parameter> params = new HashMap<>();
        params.put(0, new Parameter("Group name", String.class));
        return params;
    };

    public Request createRequest(Map<Integer, Object> values) {
        String group = (String)values.get(0);

        return new CreateGroupRequest(super.getRequestId(), group); // 0 is a dummy value for requestId
    }

    
}
