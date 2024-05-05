package chat.integration;

import chat.client.MockInterface;
import chat.common.servermessage.ChatPayload;
import chat.common.servermessage.StatusPayload;
import chat.common.util.Logger;

import java.util.Map;
import java.util.HashMap;  

class MockPerson {
    ClientThread client;
    MockInterface mockInterface;
    String name;

    MockPerson(String name) {
        this.name = name;
        this.mockInterface = new MockInterface();
        this.client = new ClientThread(mockInterface);

        this.client.start();
    }

    void logIn() {
        Map<Integer, Object> values = new HashMap<>();
        values.put(0, this.name);
        mockInterface.setRequest("Log in", values);
        
    }

    StatusPayload getStatusPayload() {
        return mockInterface.getStatusPayload();
    }

    ChatPayload getChatPayload() {
        return mockInterface.getChatPayload();
    }

    void setRequest(String description, Map<Integer, Object> values) {
        mockInterface.setRequest(description, values);
    }

    void displayStatus() {
        StatusPayload status = mockInterface.getStatusPayload();
        System.out.println(status.status + " " + status.message);
    
    }

    void displayChat() {
        Logger.debug(name + " is displaying chat");
        ChatPayload chat = mockInterface.getChatPayload();
        System.out.println(chat.from + "->" + chat.to + ": " + chat.message);
    }

}
