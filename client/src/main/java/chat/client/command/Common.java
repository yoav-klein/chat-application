package chat.client.command;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import chat.client.Communication;
import chat.common.request.Request;

public class Common {
    static void serialize(Communication comm, Request request) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String requestJson;
        
        requestJson = mapper.writeValueAsString(request);
        
        comm.writeToServer(requestJson);
        
    }
}
