
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
        server.run();
    }
}