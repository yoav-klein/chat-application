package chat.client.command;


import java.io.IOException;

import com.yoav.consolemenu.Command;

import chat.client.Communication;
import chat.client.IDGenerator;
import chat.common.request.Request;
import chat.common.servermessage.StatusPayload;
import chat.common.util.Logger;

public abstract class ClientCommand implements Command {
    protected Communication comm;
    protected IDGenerator idGenerator;
    private Object synchronizer;
    private StatusPayload currentStatus;

    public ClientCommand(Communication comm, IDGenerator idGenerator, Object synchronizer, StatusPayload currentStatus) {
        this.comm = comm;
        this.idGenerator = idGenerator;
        this.synchronizer = synchronizer;
        this.currentStatus = currentStatus;
    }

    protected void sendRequest(Request request) {
        int requestId = idGenerator.getId();
        request.setRequestId(requestId);
        
        try {
            Common.serialize(comm, request);
        } catch(IOException e) {
            System.err.println("couldn't send to server");
            System.err.println(e);
            return;
        }

        while(currentStatus.requestId != requestId) {
            try {
                Logger.debug("waiting for signal from ServerThread");
                synchronized(synchronizer) {
                    synchronizer.wait();
                }
                System.out.println("wake up " + currentStatus.requestId + " " + requestId);
            } catch(InterruptedException e) {}
        }

        System.out.println("Got response from server");
        System.out.println(currentStatus.requestId + ": " + currentStatus.message);
    }
    
}
