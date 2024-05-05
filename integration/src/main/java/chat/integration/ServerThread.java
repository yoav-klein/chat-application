
package chat.integration;

import java.io.IOException;

import chat.server.Server;

class ServerThread extends Thread {
    Server server;

    ServerThread() throws IOException {
        server = new Server(8080);
    }

    @Override
    public void run()  {
        try {
            server.run();
        } catch(IOException e) {
            System.err.println("Couldn't run server");
            System.err.println(e);
        }
    }
}