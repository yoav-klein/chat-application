package chat.client.option;

import java.util.HashMap;
import java.util.Map;

import chat.client.Parameter;
import chat.common.request.*;

public class SendMessageToUserOption implements Option {
    final String description;
    
    public SendMessageToUserOption() {
        description = "Send a message to a user";
    }

    // Getters and setters
    public String getDescription() {
        return description;
    }
         
    public Map<Integer, Parameter> getParameters() { 
        Map<Integer, Parameter> params = new HashMap<>();
        params.put(0, new Parameter("User name", String.class));
        params.put(1, new Parameter("message", String.class));
        return params;
    };

    public Request createRequest(Map<Integer, Object> values) {
        String toUser = (String)values.get(0);
        String message = (String)values.get(1);

        return new SendMessageToUserRequest(0, toUser, message); // 0 is a dummy value for requestId
    }

    
}
