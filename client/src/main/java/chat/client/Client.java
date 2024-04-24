package chat.client;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import chat.common.request.*;
import chat.common.exception.*;
import chat.client.command.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.yoav.consolemenu.ConsoleMenu;


public class Client {
    
    
    private Communication comm;
    private ServerThread serverThread;
    private IDGenerator idGenerator;
    
    public Client() throws IOException {
        this.comm = new Communication("127.0.0.1", 8080);
        this.serverThread = new ServerThread(comm);
        this.idGenerator = new IDGenerator();
        serverThread.start();
    }

    private void sendCommand(Request request) throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String commandJson = mapper.writeValueAsString(request);
        
        comm.writeToServer(commandJson);
    }

    private void initConnection() throws IOException {
        ClientHelloRequest clientHello = new ClientHelloRequest("Avi", idGenerator.getId());
        sendCommand(clientHello);
    }

    void run() {
        try {
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            
            ConsoleMenu menu = new ConsoleMenu("Select choice");
            menu.addMenuItem("Send message to user", new SendMessageToUserCommand(comm, idGenerator));
            menu.addMenuItem("Send message to group", new SendMessageToGroupCommand(comm, idGenerator));
            
            initConnection();

            while(true) {
                boolean shouldRun = true;
                while(!console.ready()) {
                    Thread.sleep(1000);
                    if(serverThread.isConnectionClosed()) {
                        System.out.println("Server closed connection");
                        shouldRun = false;
                        break;
                    }
                }
                if(!shouldRun) {
                    break;
                }
                
                menu.getUserChoice();
                
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

