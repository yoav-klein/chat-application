package chat.integration;

import java.io.IOException;

import chat.server.Server;
import chat.client.Client;

public class Main {
    static class ServerThread extends Thread {
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

    static class ClientThread extends Thread {
        Client client;

        ClientThread() throws IOException {
            client = new Client();
        }

        @Override
        public void run() {
                System.out.println("Starting client");
                client.run();
            /* } catch(IOException e) {
                System.err.println("Couldn't create client");
                System.err.println(e);
            } */
        }
    }

    public static void main(String[] args) {
        try {
            ServerThread server = new ServerThread();
            server.start();

            ClientThread client = new ClientThread();
            client.start();

        } catch(IOException e) {
            System.err.println("Couldn't create server");
            System.err.println(e);
        }
    }
}
