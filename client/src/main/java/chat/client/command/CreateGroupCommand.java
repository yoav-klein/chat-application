package chat.client.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.yoav.consolemenu.Command;

import chat.common.request.CreateGroupRequest;
import chat.common.servermessage.StatusPayload;
import chat.client.Communication;
import chat.client.IDGenerator;


public class CreateGroupCommand implements Command {
    private Communication comm;
    private IDGenerator idGenerator;
    private Object synchronizer;
    private StatusPayload currentStatus;


    public CreateGroupCommand(Communication comm, IDGenerator idGenerator, Object synchronizer, StatusPayload currentStatus) {
        this.comm = comm;
        this.idGenerator = idGenerator;
        this.synchronizer = synchronizer;
        this.currentStatus = currentStatus;
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

        Integer requestId = idGenerator.getId();
        CreateGroupRequest request = new CreateGroupRequest(requestId, groupName);

        try {
            Common.serialize(comm, request);
        } catch(IOException e) {
            System.err.println("couldn't send to server");
            System.err.println(e);
            return;
        }

        while(currentStatus.requestId != requestId) {
           
            try {
                synchronized(synchronizer) {
                    synchronizer.wait();
                }
            } catch(InterruptedException e) {}
        }

        System.out.println("Got response from server");
        System.out.println(currentStatus.requestId + ": " + currentStatus.message);
    }
}
