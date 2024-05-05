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

    public void run() {
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

        class MockPerson {
            ClientThread client;
            MockInterface mockInterface;
            String name;

            MockPerson(String name) {
                this.name = name;
                this.mockInterface = new MockInterface();
                this.client = new ClientThread(mockInterface);

                this.client.start();
            }

            void logIn() {
                Map<Integer, Object> values = new HashMap<>();
                values.put(0, this.name);
                mockInterface.setRequest("Log in", values);
                
            }

            StatusPayload getStatusPayload() {
                return mockInterface.getStatusPayload();
            }

            ChatPayload getChatPayload() {
                return mockInterface.getChatPayload();
            }

            void setRequest(String description, Map<Integer, Object> values) {
                mockInterface.setRequest(description, values);
            }

            void displayStatus() {
                StatusPayload status = mockInterface.getStatusPayload();
                System.out.println(status.status + " " + status.message);
            
            }

            void displayChat() {
                Logger.debug(name + " is displaying chat");
                ChatPayload chat = mockInterface.getChatPayload();
                System.out.println(chat.from + "->" + chat.to + ": " + chat.message);
            }

        }

        MockPerson avi = new MockPerson("Avi");
        MockPerson benny = new MockPerson("Benny");

        avi.logIn();
        avi.displayStatus();
        benny.logIn();
        benny.displayStatus();

        Map<Integer, Object> values = new HashMap<>();
        values.put(0, "Family");

        avi.setRequest("Join a group", values);

        avi.displayStatus();
        
        values = new HashMap<>();
        values.put(0, "Avi");
        values.put(1, "Hey");

        benny.setRequest("Send a message to a user", values);
        benny.displayStatus();

        avi.displayChat();

    }
}

