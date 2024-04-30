package chat.client.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import chat.common.request.CreateGroupRequest;
import chat.common.servermessage.StatusPayload;
import chat.client.Communication;
import chat.client.IDGenerator;


public class CreateGroupCommand extends ClientCommand {

    public CreateGroupCommand(Communication comm, IDGenerator idGenerator, Object synchronizer, StatusPayload currentStatus) {
        super(comm, idGenerator, synchronizer, currentStatus);
    }

    public void execute() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String groupName;
        
        try {
            System.out.println("Enter group name");
            groupName = in.readLine();
            
        } catch(IOException e) {
            System.out.println("couldn't read line");
            System.out.println(e);
            return;
        }

        CreateGroupRequest request = new CreateGroupRequest(0, groupName);
        sendRequest(request);
    }
}
