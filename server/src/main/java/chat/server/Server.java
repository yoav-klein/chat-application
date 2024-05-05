/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package chat.server;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import chat.common.request.*;
import chat.common.servermessage.StatusMessageType;
import chat.common.servermessage.StatusServerMessage;
import chat.common.servermessage.ServerMessage;
import chat.common.exception.*;
import chat.common.util.Logger;


public class Server {

    private Map<Integer, User> idToUserMap = new HashMap<Integer, User>();
    private Map<String, Integer> usernameToIdMap = new HashMap<String, Integer>();
    private Map<String, Group> groupnameToGroup = new HashMap<String, Group>();
    private Communication comm;
    private RequestWorker worker;
    
    public Server(int port) throws IOException {
        comm = new Communication(port);
        worker = new RequestWorker(comm, idToUserMap, usernameToIdMap, groupnameToGroup);
    }
    
    private void handleRequest(ClientMessage clientMessage) {
        String requestString = clientMessage.getMessage();
        int uid = clientMessage.getUid();


        ObjectMapper mapper = new ObjectMapper();
        ServerMessage status;
        try {
            RequestType type = RequestType.valueOf(mapper.readTree(requestString).get("type").textValue());
            

            // check if user is logged in
            if(!idToUserMap.containsKey(uid) && type != RequestType.LOGIN) {
                int requestId = mapper.readTree(requestString).get("requestId").intValue();
                status = new StatusServerMessage(requestId, StatusMessageType.BAD_REQUEST, "User not logged in");
                String serverStatusString = mapper.writeValueAsString(status);
                comm.sendMessageToClient(uid, serverStatusString);
                return;
            }

            switch(type) {
                case LOGIN:
                    Logger.debug("LOGIN");
                    status = worker.login(uid, mapper.readValue(requestString, LoginRequest.class));
                    break;

                case SEND_MESSAGE_TO_USER:
                    Logger.debug("SEND_MESSAGE_TO_USER");
                    status = worker.sendMessageToUser(idToUserMap.get(uid).getName(), mapper.readValue(requestString, SendMessageToUserRequest.class));
                    break;
                case CREATE_GROUP:
                    Logger.debug("CREATE_GROUP");
                    status = worker.createGroup(idToUserMap.get(uid), mapper.readValue(requestString, CreateGroupRequest.class));
                    break;
                case SEND_MESSAGE_TO_GROUP:
                    Logger.debug("SEND_MESSAGE_TO_GROUP");
                    status = worker.sendMessageToGroup(idToUserMap.get(uid), mapper.readValue(requestString, SendMessageToGroupRequest.class));
                    break;
                case JOIN_GROUP:
                    Logger.debug("JOIN_GROUP");
                    status = worker.joinGroup(idToUserMap.get(uid), mapper.readValue(requestString, JoinGroupRequest.class));
                    break;
                case LIST_USERS_IN_GROUP:
                    Logger.debug("LIST_USERS_IN_GROUP");
                    status = worker.listUsersInGroup(idToUserMap.get(uid), mapper.readValue(requestString, ListUsersInGroupRequest.class));
                    break;
                case LEAVE_GROUP:
                    Logger.debug("LEAVE_GROUP");
                    status = worker.leaveGroup(idToUserMap.get(uid), mapper.readValue(requestString, LeaveGroupRequest.class));
                    break;
                case LIST_GROUPS_OF_USER:
                    Logger.debug("LIST_GROUPS_OF_USER");
                    status = worker.listGroupsOfUser(idToUserMap.get(uid), mapper.readValue(requestString, ListGroupsRequest.class));
                    break;

                default:
                    int requestId = mapper.readTree(requestString).get("requestId").intValue();
                    status = new StatusServerMessage(requestId, StatusMessageType.BAD_REQUEST, "Unknown request");
            }
        } catch(IOException e){
            Logger.debug("FAILED in handleRequest");
            status = new StatusServerMessage(-1, StatusMessageType.FAILURE, e.getMessage());
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


    public void run() throws IOException {
        while(true) {
            ClientMessage clientMessage;

            try {
                clientMessage = comm.run();
                
                // handle request
                handleRequest(clientMessage);

            } catch(ClosedConnectionException e) {
                int uid = e.getUid();
                handleUserLogout(uid);
            } 
        }
    }

    private void handleUserLogout(int uid) {
        if(!idToUserMap.containsKey(uid)) {
            return;
        }
        Logger.debug("User " + idToUserMap.get(uid).getName() + " logged out");
        User user = idToUserMap.get(uid);
        String username = user.getName();

        for(Group group : user.getGroups()) {
            group.removeUser(user);
        }
        
        idToUserMap.remove(uid);
        usernameToIdMap.remove(username);
        
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