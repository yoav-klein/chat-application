package chat.client;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import chat.common.command.*;
import chat.common.exception.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.yoav.consolemenu.ConsoleMenu;


public class Client {
    private static Communication comm;

    private static void sendCommand(Command command) throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String commandJson = mapper.writeValueAsString(command);
        
        comm.writeToServer(commandJson);

    }

    private static void initConnection() throws IOException {
        ClientHelloCommand clientHello = new ClientHelloCommand("Avi");
        sendCommand(clientHello);
        try {
            System.out.println(comm.readFromServer());
        } catch(ClosedConnectionException e) {
            System.err.println("server closed connection");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            comm = new Communication("127.0.0.1", 8080);
            ConsoleMenu menu = new ConsoleMenu("Select choice");
            
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

                String command = console.readLine();
                if(command.equals("exit")) {
                    serverThread.stopRunning();
                    break;
                }
                comm.writeToServer(command);
            }
            serverThread.join();
            
        } catch(IOException e) {
            System.err.println("Error");
            System.err.println(e);
        }
    }
}

