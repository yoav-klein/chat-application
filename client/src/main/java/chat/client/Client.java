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
    private IDGenerator idGenerator;
    private Object synchronizer;
    private StatusPayload currentStatus;
    private ConsoleInterface consoleInterface;
    
    public Client() throws IOException {
        this.currentStatus = new StatusPayload();
        this.synchronizer = new Object();
        this.comm = new Communication("127.0.0.1", 8080);
        this.serverThread = new ServerThread(comm, synchronizer, currentStatus);
        this.idGenerator = new IDGenerator();

        RequestManager requestManager = new RequestManager();
        requestManager.addOption(new SendMessageToUserOption());
        requestManager.addOption(new SendMessageToGroupOption());
        requestManager.addOption(new CreateGroupOption());
        requestManager.addOption(new JoinGroupOption());
        requestManager.addOption(new ListGroupsOption());
        requestManager.addOption(new ListUsersInGroupOption());
        requestManager.addOption(new LeaveGroupOption());
        
        this.consoleInterface = new ConsoleInterface(requestManager, idGenerator);

        serverThread.start();
    }

    private void initConnection() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        while(true) {
            System.out.println("Enter your username");
            String userName = new BufferedReader(new InputStreamReader(System.in)).readLine();
            int requestId = idGenerator.getId();
            ClientHelloRequest clientHello = new ClientHelloRequest(userName, requestId);
            String commandJson = mapper.writeValueAsString(clientHello);
            comm.writeToServer(commandJson);
            while(currentStatus.requestId != requestId)
            {
                try {
                    synchronized(synchronizer) {
                        synchronizer.wait();
                    }
                } catch(InterruptedException e) {}
            }

            if(currentStatus.status != StatusMessageType.SUCCESS) {
                System.err.println("Login to server failed");
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

        while(currentStatus.requestId != request.getRequestId()) {
            try {
                Logger.debug("waiting for signal from ServerThread");
                synchronized(synchronizer) {
                    synchronizer.wait();
                }
                System.out.println("wake up " + currentStatus.requestId + " " + request.getRequestId());
            } catch(InterruptedException e) {}
        }

        Logger.debug("Return from sendRequest");
    }

    void run() {
        try {
            /* ConsoleMenu menu = new ConsoleMenu("Select choice");
            menu.addMenuItem("Send message to user", new SendMessageToUserCommand(comm, idGenerator, synchronizer, currentStatus));
            menu.addMenuItem("Create a new group", new CreateGroupCommand(comm, idGenerator, synchronizer, currentStatus));
            menu.addMenuItem("Send message to group", new SendMessageToGroupCommand(comm, idGenerator, synchronizer, currentStatus));
            menu.addMenuItem("Join a group", new JoinGroupCommand(comm, idGenerator, synchronizer, currentStatus));
            menu.addMenuItem("List users in group", new ListUsersInGroupCommand(comm, idGenerator, synchronizer, currentStatus));
            menu.addMenuItem("Leave group", new LeaveGroupCommand(comm, idGenerator, synchronizer, currentStatus));
            menu.addMenuItem("List groups",  new ListGroupsCommand(comm, idGenerator, synchronizer, currentStatus));  */
            
            initConnection();

            boolean shouldRun = true;
            while(shouldRun) {
                Request request = consoleInterface.getRequest();
                System.out.println("After get request");
                sendRequest(request);
                
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

