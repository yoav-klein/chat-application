package chat.client.option;

import java.util.HashMap;
import java.util.Map;

import chat.client.Parameter;
import chat.common.request.*;

public class LoginOption extends Option {
    final String description;
    
    public LoginOption() {
        description = "Log in";
    }

    // Getters and setters
    public String getDescription() {
        return description;
    }
         
    public Map<Integer, Parameter> getParameters() { 
        Map<Integer, Parameter> params = new HashMap<>();
        params.put(0, new Parameter("User name", String.class));
        return params;
    };

    public Request createRequest(Map<Integer, Object> values) {
        String userName = (String)values.get(0);

        return new LoginRequest(super.getRequestId(), userName); // 0 is a dummy value for requestId
    }

    
}
