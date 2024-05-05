package chat.integration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import chat.server.Server;
import chat.client.Client;
import chat.client.MockInterface;
import chat.common.servermessage.ChatPayload;
import chat.common.servermessage.StatusPayload;
import chat.common.servermessage.StatusMessageType;


public class Main {
    
    public static void main(String[] args) throws IOException {
        
        ServerThread serverThread = new ServerThread();
        serverThread.start();

        MockPerson avi = new MockPerson("Avi");
        MockPerson benny = new MockPerson("Benny");

        // login
        avi.logIn();
        benny.logIn();

        StatusPayload aviStatus = avi.getStatusPayload();
        StatusPayload bennyStatus = benny.getStatusPayload();

        assert(aviStatus.status == StatusMessageType.SUCCESS);
        assert(bennyStatus.status == StatusMessageType.SUCCESS);

        Map<Integer, Object> values = new HashMap<>();

        // send message
        values = new HashMap<>();
        values.put(0, "Avi");
        values.put(1, "Hey");

        benny.setRequest("Send a message to a user", values);

        bennyStatus = benny.getStatusPayload();
        assert(bennyStatus.status == StatusMessageType.SUCCESS);

        // check chat
        ChatPayload aviChat = avi.getChatPayload();
        assert(aviChat.from.equals("Benny"));
        assert(aviChat.to.equals("Avi"));
        assert(aviChat.message.equals("Hey"));

        // create group
        values = new HashMap<>();
        values.put(0, "Family");

        avi.setRequest("Create a group", values);

        aviStatus = avi.getStatusPayload();
        assert(aviStatus.status == StatusMessageType.SUCCESS);

        // check if Avi is in group
        values = new HashMap<>();
        values.put(0, "Family");

        avi.setRequest("List users in a group", values);

        aviStatus = avi.getStatusPayload();
        assert(aviStatus.status == StatusMessageType.SUCCESS);

        String message = avi.getStatusPayload().message;
        System.out.println(message);

        ObjectMapper mapper = new ObjectMapper();
        List<String> users = mapper.readValue(message, List.class);
        assert(users.size() == 1);
        assert(users.get(0).equals("Avai"));

        
    }
}
