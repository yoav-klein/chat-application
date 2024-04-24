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
    private static Communication comm;

    private static void sendCommand(Request request) throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String commandJson = mapper.writeValueAsString(request);
        
        comm.writeToServer(commandJson);

    }

    private static void initConnection() throws IOException {
        ClientHelloRequest clientHello = new ClientHelloRequest("Avi");
        sendCommand(clientHello);
        /* try {
            System.out.println(comm.readFromServer());
        } catch(ClosedConnectionException e) {
            System.err.println("server closed connection");
        } */
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            comm = new Communication("127.0.0.1", 8080);
            ConsoleMenu menu = new ConsoleMenu("Select choice");
            menu.addMenuItem("Send message to user", new SendMessageToUserCommand(comm));
            menu.addMenuItem("Send message to group", new SendMessageToGroupCommand(comm));
            
            initConnection();

            ServerThread serverThread = new ServerThread(comm);
            serverThread.start();

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
        }
    }
}

