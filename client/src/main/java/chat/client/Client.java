package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import chat.common.request.*;
import chat.common.servermessage.StatusMessageType;
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

    private void initConnection() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        while(true) {
            System.out.println("Enter your username");
            String userName = new BufferedReader(new InputStreamReader(System.in)).readLine();
            
            ClientHelloRequest clientHello = new ClientHelloRequest(userName, IDGenerator.getInstance().getId());
            String commandJson = mapper.writeValueAsString(clientHello);
            comm.writeToServer(commandJson);


            while(currentStatus.requestId != IDGenerator.getInstance().getCurrent())
            {
                try {
                    synchronized(synchronizer) {
                        synchronizer.wait();
                    }
                } catch(InterruptedException e) {}
            }

            if(currentStatus.status != StatusMessageType.SUCCESS) {
                System.err.println("Login to server failed");
                System.err.println(currentStatus.status);
                System.err.println(currentStatus.message);
            } else {
                break;
            }
        }

    }

    void sendRequest(Request request) {
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
                Logger.debug("waitForResponse woke up " );
            } catch(InterruptedException e) {}
        }
    }

    void run() {
        try {
            initConnection();

            boolean shouldRun = true;
            while(shouldRun) {
                Logger.debug("calling getRequest");


                Request request = userInterface.getRequest();
                sendRequest(request);
                waitForResponse(request);

                userInterface.processStatusMessage(currentStatus);
                // handle response
                // check type of request
                // and handle response accordingly
                
                if(serverThread.isConnectionClosed()) {
                    System.out.println("Server closed connection");
                    shouldRun = false;
                }
                
            }
            serverThread.join();
            
        } catch(IOException e) {
            System.err.println("Error");
            System.err.println(e);
        } catch(InterruptedException e) {
            System.out.println("Interrupted: " + e);
        }

    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.run();
        
    }
}

