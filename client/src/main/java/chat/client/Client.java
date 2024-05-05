package chat.client;

import java.io.IOException;

import chat.common.exception.TimeoutException;
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
    
    public Client(String serverHost, int port) throws IOException {
        this.currentStatus = new StatusPayload();
        this.synchronizer = new Object();
        this.comm = new Communication(serverHost, port);
        
        RequestManager requestManager = initRequestManager();
        
        this.userInterface = new ConsoleInterface(requestManager);
        this.serverThread = new ServerThread(comm, synchronizer, currentStatus, userInterface);

        serverThread.start();
    }

    public Client(String serverHost, int port, MockInterface userInterface) throws IOException {
        this.currentStatus = new StatusPayload();
        this.synchronizer = new Object();
        this.comm = new Communication(serverHost, port);
        
        RequestManager requestManager = initRequestManager();
        
        this.userInterface = userInterface;
        this.userInterface.setRequestManager(requestManager);
        this.serverThread = new ServerThread(comm, synchronizer, currentStatus, userInterface);

        serverThread.start();
    }


    private RequestManager initRequestManager() {
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
    
    private void sendRequestToServer(Request request) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestJson;
            
            requestJson = mapper.writeValueAsString(request);
            
            comm.writeToServer(requestJson);
        } catch(IOException e) {
            Logger.error("Couldn't send request to server: " + e);
            throw e;
        }
    }
    
    private void waitForResponse(Request request) throws TimeoutException {
        synchronized(synchronizer) {    
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0;
            long timeoutMillis = 10000;

            while (currentStatus.requestId != request.getRequestId() && elapsedTime < timeoutMillis) {
                try {
                    synchronizer.wait(timeoutMillis - elapsedTime); // Wait for the remaining time
                } catch(InterruptedException e) {
                    Logger.error("Interrupted: " + e);
                }
                elapsedTime = System.currentTimeMillis() - startTime;
            }
    
            if (currentStatus.requestId != request.getRequestId()) {
                throw new TimeoutException();
            }
            
        }
    }

    public void run() {
        try {
            
            while(true) {

                Request request = userInterface.getRequest();
                
                if(serverThread.isConnectionClosed()) {
                    System.out.println("Server closed connection");
                    break;
                }

                try {
                    sendRequestToServer(request);
                } catch(IOException e) {
                    Logger.error("Error sending request to server: " + e);
                    continue;
                }


                try {
                    waitForResponse(request);
                } catch(TimeoutException e) {
                    Logger.error("Timeout waiting for response from server");
                    continue;
                }

                userInterface.processStatusMessage(currentStatus);
                
            }
            serverThread.join();
            
        } catch(InterruptedException e) {
           Logger.error("Interrupted: " + e);
        } 

    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("127.0.0.1", 8080);
        client.run();
    }
}

