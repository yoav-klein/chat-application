
package chat.client.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.yoav.consolemenu.Command;
import chat.common.request.SendMessageToUserRequest;
import chat.client.Communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SendMessageToUserCommand implements Command {
    private Communication comm;

    public SendMessageToUserCommand(Communication comm) {
        this.comm = comm;
    }

    public void execute() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String to;
        String message;
        try {
            System.out.println("Enter user name");
            to = in.readLine();
            System.out.println("Enter message");
            message = in.readLine();
        
        } catch(IOException e) {
            System.out.println("couldn't read line");
            System.out.println(e);
            return;
        }

        SendMessageToUserRequest request = new SendMessageToUserRequest(to, message);

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
        // read to
        // read message
        // construct a SendMessageToUserCommandArgs
    }
}