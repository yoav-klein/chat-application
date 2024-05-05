
package chat.integration;

import chat.client.*;

import java.io.IOException;


class ClientThread extends Thread {
    MockInterface mockInterface;
    ClientThread(MockInterface mockInterface) { this.mockInterface = mockInterface; }

    public void run() {
        try {
            Client client = new Client(mockInterface);
            client.run();
        } catch(IOException e) {
            System.err.println("Couldn't create client");
            System.err.println(e);
        }
    }
}