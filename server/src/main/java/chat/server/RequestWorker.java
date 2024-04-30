package chat.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import chat.common.request.CreateGroupRequest;
import chat.common.request.SendMessageToUserRequest;
import chat.common.servermessage.ChatMessageType;
import chat.common.servermessage.ChatServerMessage;
import chat.common.servermessage.ServerMessageStatusType;
import chat.common.servermessage.StatusServerMessage;

class RequestWorker {
    private Communication comm;
    private Map<Integer, User> idToUserMap;
    private Map<String, Integer> usernameToIdMap;
    private Map<String, Group> groupnameToGroup;

    RequestWorker(Communication comm, Map<Integer, User> idToUserMap, Map<String, Integer> usernameToIdMap, Map<String, Group> groupnameToGroup) {
        this.comm = comm;
        this.idToUserMap = idToUserMap;
        this.usernameToIdMap = usernameToIdMap;
        this.groupnameToGroup = groupnameToGroup;
    }

    StatusServerMessage sendMessageUtil(int toUserId, int requestId, Object message) {
        StatusServerMessage statusMessage;
        try {
            comm.sendMessageToClient(toUserId, new ObjectMapper().writeValueAsString(message));
            statusMessage = new StatusServerMessage(requestId, ServerMessageStatusType.SUCCESS, "Sent Successfully");
        }
        catch(IOException e) {
            statusMessage = new StatusServerMessage(requestId, ServerMessageStatusType.FAILURE, e.getMessage());
        }

        return statusMessage;
    }

    StatusServerMessage sendMessageToUser(String from, SendMessageToUserRequest request) {
        String toUserName = request.getToUser();
        if(!usernameToIdMap.containsKey(toUserName)) {
            return new StatusServerMessage(request.getRequestId(), ServerMessageStatusType.BAD_REQUEST, "No such user: " + toUserName);
        } 
        int toUserId = usernameToIdMap.get(request.getToUser());
        ChatServerMessage message = new ChatServerMessage(ChatMessageType.TO_USER, from, request.getMessage());
        
        return sendMessageUtil(toUserId, request.getRequestId(), message);
        
    }

    StatusServerMessage createGroup(User user, CreateGroupRequest request) {
        Group newGroup = new Group(request.groupName, user);
        groupnameToGroup.put(request.groupName, newGroup);

        return new StatusServerMessage(request.getRequestId(), ServerMessageStatusType.SUCCESS, "Created new group successfully");

    }
}
