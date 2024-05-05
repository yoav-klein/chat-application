package chat.client;

import java.io.IOException;
import chat.common.request.*;
import chat.common.servermessage.ChatPayload;
import chat.common.servermessage.StatusPayload;
import chat.common.util.Logger;
import chat.client.option.*;

import java.util.Map;
import java.util.HashMap;

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

    void run() {
        try {
            
            boolean shouldRun = true;
            while(shouldRun) {
                Logger.debug("calling getRequest");

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
        class ClientThread extends Thread {
            MockInterface mockInterface;
            ClientThread(MockInterface mockInterface) { this.mockInterface = mockInterface; }

            public void run() {
                try {
                    Client client = new Client(mockInterface);
                    client.run();
                } catch(IOException e) {
                    System.err.println("Couldn't create client");
                    System.err.println(e);
                }
            }
        }
        StatusPayload status;
        ChatPayload chat;

        MockInterface.Synchronizer synObject = new MockInterface.Synchronizer();
        MockInterface mockInterface = new MockInterface(synObject);

        ClientThread clientThread = new ClientThread(mockInterface);
        clientThread.start();

        Map<Integer, Object> values = new HashMap<>();
        values.put(0, "Yoav");
        
        mockInterface.setRequest("Log in", values);
        
        status = mockInterface.getStatusPayload();
        
        System.out.println(status.status + " " + status.message);

        values = new HashMap<>();
        values.put(0, "Family");

        Logger.debug("Setting request to create group");
        mockInterface.setRequest("Create a group", values);

        status = mockInterface.getStatusPayload();
        System.out.println(status.status + " " + status.message);

        values = new HashMap<>();
        values.put(0, "Yoav");
        values.put(1, "Hey");

        mockInterface.setRequest("Send a message to a user", values);
        status = mockInterface.getStatusPayload();
        System.out.println(status.status + " " + status.message);
        
        chat = mockInterface.getChatPayload();
        System.out.println(chat.from + ": " + chat.message);
    }
}

