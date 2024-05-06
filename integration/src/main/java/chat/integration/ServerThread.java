
package chat.integration;

import java.io.IOException;

import chat.server.Server;

class ServerThread extends Thread {
    Server server;

    ServerThread(int port) throws IOException {
        server = new Server(port);
    }

    @Override
    public void run()  {
        server.run();
    }

    void stopServer() {
        server.stop();
    }
}