package chat.server;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import chat.common.request.SendMessageToUserRequest;
import chat.common.servermessage.ChatMessageType;
import chat.common.servermessage.ChatServerMessage;
import chat.common.servermessage.ServerMessageStatusType;
import chat.common.servermessage.StatusServerMessage;

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
        if(!usernameToId.containsKey(toUserName)) {
            return new StatusServerMessage(request.getRequestId(), ServerMessageStatusType.BAD_REQUEST, "No such user: " + toUserName);
        } 
        int toUserId = usernameToId.get(request.getToUser());
        ChatServerMessage message = new ChatServerMessage(ChatMessageType.TO_USER, from, request.getMessage());
        
        return sendMessageUtil(toUserId, request.getRequestId(), comm, message);
        
    }
}
