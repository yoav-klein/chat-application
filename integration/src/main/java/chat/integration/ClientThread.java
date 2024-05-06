
package chat.integration;

import java.io.IOException;

import chat.client.Client;
import chat.client.MockInterface;


class ClientThread extends Thread {
    MockInterface mockInterface;
    ClientThread(MockInterface mockInterface) { this.mockInterface = mockInterface; }

    public void run() {
        try {
            Client client = new Client("127.0.0.1", 8080, mockInterface);
            client.run();
        } catch(IOException e) {
            System.err.println("Couldn't create client");
            System.err.println(e);
        }
    }

    public void stopClient() {
        mockInterface.setRequest("Stop client", null);
    }
}