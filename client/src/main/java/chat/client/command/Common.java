package chat.client.command;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import chat.client.Communication;
import chat.common.request.Request;

public class Common {
    static void serialize(Communication comm, Request request) {
        ObjectMapper mapper = new ObjectMapper();
        String requestJson;
        try {
            requestJson = mapper.writeValueAsString(request);
        } catch(JsonProcessingException e) {
            System.out.println("Couldn't encode to JSON");
            return;
        }

        try {
            comm.writeToServer(requestJson);
        } catch(IOException e) {
            System.out.println("Error sending to server");
            System.out.println(e);
        }
    }
}
