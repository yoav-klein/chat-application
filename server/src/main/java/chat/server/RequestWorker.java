package chat.server;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import chat.common.request.CreateGroupRequest;
import chat.common.request.JoinGroupRequest;
import chat.common.request.LeaveGroupRequest;
import chat.common.request.ListGroupsRequest;
import chat.common.request.ListUsersInGroupRequest;
import chat.common.request.LoginRequest;
import chat.common.request.SendMessageToGroupRequest;
import chat.common.request.SendMessageToUserRequest;
import chat.common.servermessage.ChatMessageType;
import chat.common.servermessage.ChatServerMessage;
import chat.common.servermessage.ServerMessage;
import chat.common.servermessage.StatusMessageType;
import chat.common.servermessage.StatusServerMessage;
import chat.common.util.Logger;

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
            statusMessage = new StatusServerMessage(requestId, StatusMessageType.SUCCESS, "Sent Successfully");
        }
        catch(IOException e) {
            statusMessage = new StatusServerMessage(requestId, StatusMessageType.FAILURE, e.getMessage());
        }

        return statusMessage;
    }


    StatusServerMessage login(int clientUid, LoginRequest request) {
        
        if(idToUserMap.containsKey(clientUid)) { // new user
            return new StatusServerMessage(request.getRequestId(), StatusMessageType.BAD_REQUEST, "User already logged in");
        }
        
        User newUser = new User(request.getUserName());
        idToUserMap.put(clientUid, newUser);
        usernameToIdMap.put(request.getUserName(), clientUid);

        // return response to client
        return new StatusServerMessage(request.getRequestId(), StatusMessageType.SUCCESS, "client registered");
    }

    StatusServerMessage sendMessageToUser(String from, SendMessageToUserRequest request) {
        String toUserName = request.getToUser();

        Logger.debug("sendMessageToUser: " + from + " -> " + toUserName);
        if(!usernameToIdMap.containsKey(toUserName)) {
            return new StatusServerMessage(request.getRequestId(), StatusMessageType.BAD_REQUEST, "No such user: " + toUserName);
        } 
        int toUserId = usernameToIdMap.get(request.getToUser());
        ChatServerMessage message = new ChatServerMessage(ChatMessageType.TO_USER, from, toUserName, request.getMessage());
        
        return sendMessageUtil(toUserId, request.getRequestId(), message);
        
    }

    StatusServerMessage createGroup(User user, CreateGroupRequest request) {
        String groupName = request.getGroupName();
        if(groupnameToGroup.containsKey(groupName)) {
            return new StatusServerMessage(request.getRequestId(), StatusMessageType.BAD_REQUEST, "Group already exists: " + groupName);
        }
        
        Group newGroup = new Group(groupName, user);
        
        groupnameToGroup.put(groupName, newGroup);

        return new StatusServerMessage(request.getRequestId(), StatusMessageType.SUCCESS, "Created new group successfully");

    }

    StatusServerMessage sendMessageToGroup(User from, SendMessageToGroupRequest request) {
        Logger.debug("sendMessageToGroup: " + from.getName() + " -> " + request.getToGroup());
        String toGroup = request.getToGroup();
        if(!groupnameToGroup.containsKey(toGroup)) {
            return new StatusServerMessage(request.getRequestId(), StatusMessageType.BAD_REQUEST, "No such group: " + toGroup);
        }
        
        Group group = groupnameToGroup.get(toGroup);

        if(!group.getUsers().contains(from)) {
            return new StatusServerMessage(request.getRequestId(), StatusMessageType.BAD_REQUEST, "Not part of group: " + toGroup);
        }

        ChatServerMessage message = new ChatServerMessage(ChatMessageType.TO_GROUP, from.getName(), toGroup, request.getMessage());
        
        for(User user: group.getUsers()) {
            if(user.equals(from)) continue;
            sendMessageUtil(usernameToIdMap.get(user.getName()), request.getRequestId(), message);
        }

        return new StatusServerMessage(request.getRequestId(), StatusMessageType.SUCCESS, "Sent message to group successfully");
    }

    StatusServerMessage joinGroup(User user, JoinGroupRequest request) {
        String groupName = request.getGroupName();
        if(!groupnameToGroup.containsKey(groupName)) {
            return new StatusServerMessage(request.getRequestId(), StatusMessageType.BAD_REQUEST, "No such group: " + groupName);
        }
        Group group = groupnameToGroup.get(groupName);

        if(group.getUsers().contains(user)) {
            return new StatusServerMessage(request.getRequestId(), StatusMessageType.BAD_REQUEST, "Already part of group: " + groupName);
        }

        group.addUser(user);
        user.addGroup(group);
        
        return new StatusServerMessage(request.getRequestId(), StatusMessageType.SUCCESS, "Joined group successfully");
    }

    ServerMessage listUsersInGroup(User user, ListUsersInGroupRequest request) {

        String groupName = request.getGroupName();
        if(!groupnameToGroup.containsKey(groupName)) {
            return new StatusServerMessage(request.getRequestId(), StatusMessageType.BAD_REQUEST, "No such group: " + groupName);
        }
        
        Group group = groupnameToGroup.get(groupName);

        if(!group.getUsers().contains(user)) {
            return new StatusServerMessage(request.getRequestId(), StatusMessageType.UNAUTHORIZED, "Not part of group: " + groupName);
        }

        String response;
        try {
            response = new ObjectMapper().writeValueAsString(group.getUsers().stream().map(User::getName).collect(Collectors.toList()));
        } catch(JsonProcessingException e) {
            return new StatusServerMessage(request.getRequestId(), StatusMessageType.FAILURE, e.toString());
        }
                
        return new StatusServerMessage(request.getRequestId(), StatusMessageType.SUCCESS, response);
    }

    ServerMessage leaveGroup(User user, LeaveGroupRequest value) {
        String groupName = value.getGroupName();
        if(!groupnameToGroup.containsKey(groupName)) {
            return new StatusServerMessage(value.getRequestId(), StatusMessageType.BAD_REQUEST, "No such group: " + groupName);
        }
        
        Group group = groupnameToGroup.get(groupName);

        if(!group.getUsers().contains(user)) {
            return new StatusServerMessage(value.getRequestId(), StatusMessageType.BAD_REQUEST, "Not part of group: " + groupName);
        }

        group.removeUser(user);
        user.removeGroup(group);
        
        return new StatusServerMessage(value.getRequestId(), StatusMessageType.SUCCESS, "Left group successfully");
    }

    ServerMessage listGroupsOfUser(User user, ListGroupsRequest value) {
        String response;
        try {
            response = new ObjectMapper().writeValueAsString(user.getGroups().stream().map(Group::getName).collect(Collectors.toList()));
        } catch(JsonProcessingException e) {
            return new StatusServerMessage(value.getRequestId(), StatusMessageType.FAILURE, e.toString());
        }
                
        return new StatusServerMessage(value.getRequestId(), StatusMessageType.SUCCESS, response);
    }

    

}
