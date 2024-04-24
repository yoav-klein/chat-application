/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package chat.server;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import chat.common.request.*;
import chat.common.exception.*;
import chat.common.*;


public class Server {

    private static Map<Integer, User> idToUserMap = new HashMap<Integer, User>();
    private static Map<String, Integer> usernameToIdMap = new HashMap<String, Integer>();
    

    /* private static ClientMessage handleRequest(ClientMessage message, Communication comm) {
        // switch case type of request
    } */

    private static void registerUser(ClientMessage message) throws BadRequestException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClientHelloRequest clientHello;
        try {
            clientHello = mapper.readValue(message.getMessage(), ClientHelloRequest.class);
        } catch(UnrecognizedPropertyException e) {
            throw new BadRequestException("Client hello message malformed");
        }
        System.out.println("Creating new user: " + clientHello.getUserName());
        User newUser = new User(clientHello.getUserName());
        idToUserMap.put(message.getUid(), newUser);
        usernameToIdMap.put(clientHello.getUserName(), message.getUid());
       
    }

    private static void start(Communication comm) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        while(true) {
            ClientMessage clientMessage;
            String serverMessageString;
            try {
                clientMessage = comm.run();
                int uid = clientMessage.getUid();
                ServerMessageStatus status;

                if(!idToUserMap.containsKey(uid)) { // new user
                    try {
                        registerUser(clientMessage);
                        status = new ServerMessageStatus(ServerMessageStatusType.SUCCESS, "client registered");
                    } catch(BadRequestException e) {
                        status = new ServerMessageStatus(ServerMessageStatusType.BAD_REQUEST, "Not a ClientHello message");
                    }
                    String serverStatusString = mapper.writeValueAsString(status);
                    serverMessageString = mapper.writeValueAsString(new ServerMessage(ServerMessageType.STATUS, serverStatusString));
                    comm.sendMessageToClient(uid, serverMessageString);
                    continue;
                }

                // handle request
                status = new ServerMessageStatus(); // TODO: replace this with handling the request

                String serverStatusString = mapper.writeValueAsString(status);
                serverMessageString = mapper.writeValueAsString(new ServerMessage(ServerMessageType.STATUS, serverStatusString));
                comm.sendMessageToClient(uid, serverMessageString);

            } catch(ClosedConnectionException e) {
                int uid = e.getUid();
                String username = idToUserMap.get(uid).getName();
                idToUserMap.remove(uid);
                usernameToIdMap.remove(username);
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