package chat.client.command;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import chat.client.Communication;
import chat.client.IDGenerator;
import chat.common.request.ListGroupsRequest;
import chat.common.servermessage.StatusPayload;
import chat.common.servermessage.StatusMessageType;

public class ListGroupsCommand extends ClientCommand {

    
    public ListGroupsCommand(Communication comm, IDGenerator idGenerator, Object synchronizer, StatusPayload currentStatus) {
        super(comm, idGenerator, synchronizer, currentStatus); // Call to the super constructor
    }


    public void execute() {

        ListGroupsRequest request = new ListGroupsRequest(0);

        StatusPayload status = super.sendRequest(request);
        if(status.status != StatusMessageType.SUCCESS) {
            System.out.println("Couldn't list groups");
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
