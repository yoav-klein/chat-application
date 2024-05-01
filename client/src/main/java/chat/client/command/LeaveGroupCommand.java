package chat.client.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import chat.client.Communication;
import chat.client.IDGenerator;
import chat.common.request.LeaveGroupRequest;
import chat.common.servermessage.StatusPayload;

public class LeaveGroupCommand extends ClientCommand {
    
    public LeaveGroupCommand(Communication comm, IDGenerator idGenerator, Object synchronizer, StatusPayload currentStatus) {
        super(comm, idGenerator, synchronizer, currentStatus); // Call to the super constructor
    }


    public void execute() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String group;
        try {
            System.out.println("Enter group name");
            group = in.readLine();
        
        } catch(IOException e) {
            System.out.println("couldn't read line");
            System.out.println(e);
            return;
        }

        LeaveGroupRequest request = new LeaveGroupRequest(0, group);

        super.sendRequest(request);
        
    }
    
}
