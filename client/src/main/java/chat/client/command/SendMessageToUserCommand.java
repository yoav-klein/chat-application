
package chat.client.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.yoav.consolemenu.Command;

import chat.common.*;
import chat.common.request.SendMessageToUserRequest;
import chat.client.Communication;
import chat.client.IDGenerator;

public class SendMessageToUserCommand implements Command {
    private Communication comm;
    private IDGenerator idGenerator;
    private Object synchronizer;
    private StatusPayload currentStatus;

    public SendMessageToUserCommand(Communication comm, IDGenerator idGenerator, Object synchronizer, StatusPayload currentStatus) {
        this.comm = comm;
        this.idGenerator = idGenerator;
        this.synchronizer = synchronizer;
        this.currentStatus = currentStatus;
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

        Integer requestId = idGenerator.getId();
        SendMessageToUserRequest request = new SendMessageToUserRequest(requestId, to, message);
        Common.serialize(comm, request);

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