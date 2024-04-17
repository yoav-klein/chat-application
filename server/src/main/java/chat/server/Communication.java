
package chat.server;

import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset; 
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import java.util.*;
import java.io.*;


class Communication {
    private static final Charset charset = Charset.forName("UTF-8");
    private static Selector selector;
    
    private ServerSocketChannel serverSocket;
    private static ByteBuffer buffer = ByteBuffer.allocate(1024);
    static int uid = 0;

    Map<Integer, CommClient> clients = new HashMap<Integer, CommClient>();

    private class CommClient {
        private int uid;
        private SelectionKey key;

        CommClient(int uid, SelectionKey key) {
            this.uid = uid;
            this.key = key;
        }

        int getUid() {
            return this.uid;
        }

        SelectionKey getKey() {
            return this.key;
        }
    }

    Communication(int port) throws IOException {
        serverSocket = ServerSocketChannel.open(); 
        serverSocket.bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false); // must be non-blocking to allow selecting

        // now, we create a Select instance
        Selector selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        this.selector = selector;
    }

    
    private String readFromClient(SocketChannel client) throws IOException {
        
        buffer.clear();
        int readBytes = client.read(buffer);
        if(-1 == readBytes) {
            System.out.println("DEBUG: client.read returned -1");
            throw new IOException("client closed connection");
        }

        buffer.flip(); // set position back to 0 before decode
        String commandString = charset.decode(buffer).toString();

        return commandString;
    }

    
    private void writeToClient(SocketChannel client, String message) throws IOException {
        
        ByteBuffer outBuffer = charset.encode(message);

        // write to socket
        client.write(outBuffer);
        
    }

    private String readClientRequest(SelectionKey selectionKey) {
        SocketChannel client = (SocketChannel)selectionKey.channel();
        /* CommClient commClient = (CommClient)selectionKey.attachment(); */

        String command;
        try {
            command = readFromClient(client);
        } catch(java.net.SocketException e) {
            System.out.println("Client closed unexpectedly");
            selectionKey.cancel();
            return "closed";
        } catch(IOException e) {
            if(e.getMessage().contains("closed")) {
                System.out.println("INFO: client closed connection");
                try {
                    client.close();
                } catch(IOException e1) {
                    System.err.println("FATAL: couldn't close connection");
                    System.err.println(e1);
                }
                selectionKey.cancel();
                return "closed";
            }
            System.err.println("ERROR: reading from client");
            System.err.println(e);
            return null;
        }
            
        return command;
        
    }

    
    private void registerClient(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept(); // accept the connection from client
        client.configureBlocking(false);
        SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ); // register, this time with OP_READ because this socket is for reading, not receiving connections
        
        CommClient newClient = new CommClient(uid++, clientKey);
        clients.put(uid, newClient);
        clientKey.attach(newClient);
    }

    ClientMessage run() throws IOException {
        while(true) {
            
            selector.select();
            
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while(iter.hasNext()) {
                SelectionKey curr = iter.next();
                iter.remove();

                if(curr.isAcceptable()) {
                    registerClient(selector, serverSocket);
                }

                if(curr.isReadable()) {
                    String request = readClientRequest(curr);
                    if(request.equals("closed")) {
                        System.out.println("closed connection");
                        continue;
                    }

                    CommClient client = (CommClient)curr.attachment();

                    if(request == null) {
                        continue;
                    }
                    return new ClientMessage(client.getUid(), request);
                }
            }


        }
    }
}