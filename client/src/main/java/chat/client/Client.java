package chat.client;

import java.io.IOException;
import chat.common.request.*;
import chat.common.servermessage.StatusPayload;
import chat.common.util.Logger;
import chat.client.option.*;

import com.fasterxml.jackson.databind.ObjectMapper;


public class Client {
    
    private Communication comm;
    private ServerThread serverThread;
    private Object synchronizer;
    private StatusPayload currentStatus;
    private UserInterface userInterface;

    RequestManager initRequestManager() {
        RequestManager requestManager = new RequestManager();
        requestManager.addOption(new LoginOption());
        requestManager.addOption(new SendMessageToUserOption());
        requestManager.addOption(new SendMessageToGroupOption());
        requestManager.addOption(new CreateGroupOption());
        requestManager.addOption(new JoinGroupOption());
        requestManager.addOption(new ListGroupsOption());
        requestManager.addOption(new ListUsersInGroupOption());
        requestManager.addOption(new LeaveGroupOption());
        return requestManager;
    }
    
    public Client() throws IOException {
        this.currentStatus = new StatusPayload();
        this.synchronizer = new Object();
        this.comm = new Communication("127.0.0.1", 8080);
        
        RequestManager requestManager = initRequestManager();
        
        this.userInterface = new ConsoleInterface(requestManager);
        this.serverThread = new ServerThread(comm, synchronizer, currentStatus, userInterface);

        serverThread.start();
    }

    public Client(MockInterface userInterface) throws IOException {
        this.currentStatus = new StatusPayload();
        this.synchronizer = new Object();
        this.comm = new Communication("127.0.0.1", 8080);
        
        RequestManager requestManager = initRequestManager();
        
        this.userInterface = userInterface;
        this.userInterface.setRequestManager(requestManager);
        this.serverThread = new ServerThread(comm, synchronizer, currentStatus, userInterface);

        serverThread.start();
    }

    void sendRequestToServer(Request request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestJson;
            
            requestJson = mapper.writeValueAsString(request);
            
            comm.writeToServer(requestJson);
        } catch(IOException e) {
            System.err.println("couldn't send to server");
            System.err.println(e);
        }
    }
    
    void waitForResponse(Request request) {
        
        while(currentStatus.requestId != request.getRequestId()) {
            try {
                synchronized(synchronizer) {
                    synchronizer.wait();
                }
                
            } catch(InterruptedException e) {}
        }
    }

    public void run() {
        try {
            
            boolean shouldRun = true;
            while(shouldRun) {

                Request request = userInterface.getRequest();
                sendRequestToServer(request);
                waitForResponse(request);

                userInterface.processStatusMessage(currentStatus);
                
                if(serverThread.isConnectionClosed()) {
                    System.out.println("Server closed connection");
                    shouldRun = false;
                }
                
            }
            serverThread.join();
            
        } catch(InterruptedException e) {
            System.out.println("Interrupted: " + e);
        }

    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.run();
    }
}

