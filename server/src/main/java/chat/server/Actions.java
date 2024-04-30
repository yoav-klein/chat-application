package chat.server;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import chat.common.ChatMessageType;
import chat.common.ChatServerMessage;
import chat.common.ServerMessageStatusType;
import chat.common.StatusServerMessage;
import chat.common.request.SendMessageToUserRequest;

class Actions {

    static StatusServerMessage sendMessageUtil(int toUserId, int requestId, Communication comm, Object message) {
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

    static StatusServerMessage sendMessageToUser(String from, Communication comm, Map<String, Integer> usernameToId, SendMessageToUserRequest request) {
        String toUserName = request.getToUser();
        System.out.println("Send message to " + toUserName);
        System.out.println(usernameToId.containsKey(toUserName));
        if(!usernameToId.containsKey(toUserName)) {
            return new StatusServerMessage(request.getRequestId(), ServerMessageStatusType.BAD_REQUEST, "No such user: " + toUserName);
        } 
        int toUserId = usernameToId.get(request.getToUser());
        ChatServerMessage message = new ChatServerMessage(ChatMessageType.TO_USER, from, request.getMessage());
        
        return sendMessageUtil(toUserId, request.getRequestId(), comm, message);
        
    }
}
