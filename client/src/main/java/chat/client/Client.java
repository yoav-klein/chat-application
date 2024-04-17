package chat.client;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import chat.common.command.*;
import chat.common.exception.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;


public class Client {
    private static Communication comm;

    private static void sendCommand(Command command) throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String commandJson = mapper.writeValueAsString(command);

        comm = new Communication("127.0.0.1", 8080);
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

    public static void main(String[] args) throws IOException {
        try {
            
            initConnection();
            comm.close();

        } catch(IOException e) {
            System.err.println("Error");
            System.err.println(e);
        }
    }
}

