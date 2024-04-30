package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import chat.common.request.*;
import chat.common.servermessage.ServerMessageStatusType;
import chat.common.servermessage.StatusPayload;
import chat.client.command.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoav.consolemenu.ConsoleMenu;


public class Client {
    
    private Communication comm;
    private ServerThread serverThread;
    private IDGenerator idGenerator;
    private Object synchronizer;
    private StatusPayload currentStatus;
    
    public Client() throws IOException {
        this.currentStatus = new StatusPayload();
        this.synchronizer = new Object();
        this.comm = new Communication("127.0.0.1", 8080);
        this.serverThread = new ServerThread(comm, synchronizer, currentStatus);
        this.idGenerator = new IDGenerator();
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

            if(currentStatus.status != ServerMessageStatusType.SUCCESS) {
                System.err.println("Login to server failed");
                System.err.println(currentStatus.message);
            } else {
                break;
            }
        }

    }

    void run() {
        try {
            ConsoleMenu menu = new ConsoleMenu("Select choice");
            menu.addMenuItem("Send message to user", new SendMessageToUserCommand(comm, idGenerator, synchronizer, currentStatus));
            // menu.addMenuItem("Send message to group", new SendMessageToGroupCommand(comm, idGenerator));
            
            initConnection();

            boolean shouldRun = true;
            while(shouldRun) {
                menu.getUserChoice();
                System.out.println("After get user choice");
               
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

