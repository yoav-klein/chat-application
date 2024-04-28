
package chat.client.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.yoav.consolemenu.Command;
import chat.common.request.SendMessageToGroupRequest;
import chat.client.Communication;
import chat.client.IDGenerator;

public class SendMessageToGroupCommand implements Command {
    private Communication comm;
    private IDGenerator idGenerator;

    public SendMessageToGroupCommand(Communication comm, IDGenerator idGenerator) {
        this.comm = comm;
        this.idGenerator = idGenerator;
    }

    public void execute() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String to;
        String message;
        try {
            System.out.println("Enter group name");
            to = in.readLine();
            System.out.println("Enter message");
            message = in.readLine();
        
        } catch(IOException e) {
            System.out.println("couldn't read line");
            System.out.println(e);
            return;
        }

        Integer requestId = idGenerator.getId();
        SendMessageToGroupRequest request = new SendMessageToGroupRequest(requestId, to, message);
        try {
            Common.serialize(comm, request);
        } catch(IOException e) {
            System.err.println("couldn't send to server");
            System.err.println(e);
            return;
        }
    }
}