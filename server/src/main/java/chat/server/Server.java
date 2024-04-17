/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package chat.server;

import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import chat.common.command.*;
import chat.common.exception.*;


public class Server {

    private static Map<Integer, User> users = new HashMap<Integer, User>();

    private static void registerUser(ClientMessage message) throws BadRequestException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClientHelloCommand clientHello;
        try {
            clientHello = mapper.readValue(message.getMessage(), ClientHelloCommand.class);
        } catch(UnrecognizedPropertyException e) {
            throw new BadRequestException("Client hello message malformed");
        }
        System.out.println("Creating new user: " + clientHello.getUserName());
        User newUser = new User(clientHello.getUserName());
        users.put(message.getUid(), newUser);
       
    }

    private static void start(Communication comm) throws IOException {
        while(true) {
            ClientMessage message;
            try {
                message = comm.run();
                int uid = message.getUid();
                if(!users.containsKey(uid)) { // new user
                try {
                    registerUser(message);
                    comm.sendMessageToClient(new ClientMessage(uid, "OK: Got you"));

                } catch(BadRequestException e) {

                }
                // return OK to user
            }
            } catch(ClosedConnectionException e) {
                int uid = e.getUid();
                users.remove(uid);
            } 

        }

    }

    public static void main(String[] args) {
        try {
            Communication tcp = new Communication(8080);
            start(tcp);
        } catch(IOException e) {
            System.err.println(e);
        }
    }
}