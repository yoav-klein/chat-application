package chat.client.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import chat.client.Communication;
import chat.client.IDGenerator;
import chat.common.request.ListUsersInGroupRequest;
import chat.common.servermessage.StatusPayload;
import chat.common.servermessage.StatusMessageType;

public class ListUsersInGroupCommand extends ClientCommand {

    
    public ListUsersInGroupCommand(Communication comm, IDGenerator idGenerator, Object synchronizer, StatusPayload currentStatus) {
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

        ListUsersInGroupRequest request = new ListUsersInGroupRequest(0, group);

        StatusPayload status = super.sendRequest(request);
        if(status.status != StatusMessageType.SUCCESS) {
            System.out.println("Couldn't list users in group");
            System.out.println(status.status + ": " + status.message);
            return;
        }
        
        try {
            List<String> users = new ObjectMapper().readValue(status.message,  new TypeReference<List<String>>() {});
            users.forEach(i -> System.out.println(i));
        }  catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
}
