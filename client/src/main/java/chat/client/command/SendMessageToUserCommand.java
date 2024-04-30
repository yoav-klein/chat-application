
package chat.client.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import chat.common.request.SendMessageToUserRequest;
import chat.common.servermessage.StatusPayload;
import chat.client.Communication;
import chat.client.IDGenerator;

public class SendMessageToUserCommand extends ClientCommand {

    public SendMessageToUserCommand(Communication comm, IDGenerator idGenerator, Object synchronizer, StatusPayload currentStatus) {
        super(comm, idGenerator, synchronizer, currentStatus);
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

        SendMessageToUserRequest request = new SendMessageToUserRequest(0, to, message);

        super.sendRequest(request);
        
    }
}