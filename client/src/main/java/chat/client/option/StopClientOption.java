package chat.client.option;

import java.util.HashMap;
import java.util.Map;

import chat.client.Parameter;
import chat.common.request.*;

public class StopClientOption extends Option {
    final String description;
    
    public StopClientOption() {
        description = "Stop client";
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
        
        return new StopClientRequest(super.getRequestId()); // 0 is a dummy value for requestId
    }

    
}
