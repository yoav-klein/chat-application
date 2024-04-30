/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package chat.server;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import chat.common.request.*;
import chat.common.servermessage.ServerMessageStatusType;
import chat.common.servermessage.StatusServerMessage;
import chat.common.exception.*;


public class Server {

    private Map<Integer, User> idToUserMap = new HashMap<Integer, User>();
    private Map<String, Integer> usernameToIdMap = new HashMap<String, Integer>();
    private Map<String, Group> groupnameToGroup = new HashMap<String, Group>();
    private Communication comm;
    private RequestWorker worker;
    
    Server(int port) throws IOException {
        comm = new Communication(port);
        worker = new RequestWorker(comm, idToUserMap, usernameToIdMap, groupnameToGroup);
    }
    
    private void handleRequest(ClientMessage clientMessage) {
        String requestString = clientMessage.getMessage();
        int uid = clientMessage.getUid();

        ObjectMapper mapper = new ObjectMapper();
        StatusServerMessage status;
        try {
            RequestType type = RequestType.valueOf(mapper.readTree(requestString).get("type").textValue());
            switch(type) {
                case SEND_MESSAGE_TO_USER:
                    status = worker.sendMessageToUser(idToUserMap.get(uid).getName(), mapper.readValue(clientMessage.getMessage(), SendMessageToUserRequest.class));
                    break;
                case CREATE_GROUP:
                    status = worker.createGroup(idToUserMap.get(uid), mapper.readValue(clientMessage.getMessage(), CreateGroupRequest.class));
                    break;
                default:
                    int requestId = mapper.readTree(requestString).get("requestId").intValue();
                    status = new StatusServerMessage(requestId, ServerMessageStatusType.BAD_REQUEST, "Unknown request");
            }
        } catch(IOException e){
            status = new StatusServerMessage(-1, ServerMessageStatusType.FAILURE, e.getMessage());
        }

        try {
            String serverStatusString = mapper.writeValueAsString(status);
            comm.sendMessageToClient(uid, serverStatusString);
        } catch(JsonProcessingException e) {
            System.err.println(e);
        }
        catch(IOException e) {
            System.err.println(e);
        }
        
    }

    private void registerUser(ClientMessage message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClientHelloRequest clientHello;
        StatusServerMessage status;
        int uid = message.getUid();

        try {
            clientHello = mapper.readValue(message.getMessage(), ClientHelloRequest.class);

            if(usernameToIdMap.containsKey(clientHello.getUserName())) {
                status = new StatusServerMessage(clientHello.getRequestId(), ServerMessageStatusType.FAILURE, "Username already exists");
            } else {
                User newUser = new User(clientHello.getUserName());
                idToUserMap.put(uid, newUser);
                usernameToIdMap.put(clientHello.getUserName(), uid);
        
                // return response to client
                status = new StatusServerMessage(clientHello.getRequestId(), ServerMessageStatusType.SUCCESS, "client registered");
            }

        }

        catch(UnrecognizedPropertyException e) {
            status = new StatusServerMessage(-1, ServerMessageStatusType.BAD_REQUEST, "Not a ClientHello message");
        }

        String serverStatusString = mapper.writeValueAsString(status);
        comm.sendMessageToClient(uid, serverStatusString);
    }

    private void run() throws IOException {
        while(true) {
            ClientMessage clientMessage;

            try {
                clientMessage = comm.run();
                int uid = clientMessage.getUid();

                if(!idToUserMap.containsKey(uid)) { // new user
                    registerUser(clientMessage);
                    
                    continue;
                }

                // handle request
                handleRequest(clientMessage);

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
            Server server = new Server(8080);
            server.run();
        } catch(IOException e) {
            System.err.println(e);
        }
    }
}