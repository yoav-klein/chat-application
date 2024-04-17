
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

import chat.common.util.*;
import chat.common.exception.ClosedConnectionException;

class Communication {
    private static final Charset charset = Charset.forName("UTF-8");
    private static Selector selector;
    private TCPCommunication tcp = new TCPCommunication();
    
    private ServerSocketChannel serverSocket;
    /* private static ByteBuffer buffer = ByteBuffer.allocate(1024); */
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
    
    private String readClientRequest(SelectionKey selectionKey) throws IOException, ClosedConnectionException {
        SocketChannel client = (SocketChannel)selectionKey.channel();
        /* CommClient commClient = (CommClient)selectionKey.attachment(); */

        String command;
        try {
            command = tcp.readFromChannel(client);
        } catch(java.net.SocketException e) {
            System.out.println("Client closed unexpectedly");
            throw new ClosedConnectionException();
        }   
        return command;
    }
    
    private void registerClient(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept(); // accept the connection from client
        client.configureBlocking(false);
        SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ); // register, this time with OP_READ because this socket is for reading, not receiving connections
        
        CommClient newClient = new CommClient(uid, clientKey);
        clients.put(uid, newClient);
        uid++;
        clientKey.attach(newClient);
    }

    ClientMessage run() throws ClosedConnectionException, IOException  {
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
                    String request;
                    try {
                        request = readClientRequest(curr);
                    } catch(ClosedConnectionException e) {
                        CommClient closedClient = (CommClient)curr.attachment();
                        try {
                            curr.channel().close();
                        } catch(IOException e1) {
                            System.err.println("Couldn't close channel");
                            System.err.println(e1);
                        }
                        curr.cancel();
                        throw new ClosedConnectionException(closedClient.getUid());
                    } catch(IOException e) {
                        System.err.println(e);
                        continue;
                    }

                    CommClient client = (CommClient)curr.attachment();

                    if(request == null) { // TODO: what to do in this case?
                        continue;
                    }
                    return new ClientMessage(client.getUid(), request);
                }
            }
        }
    }

    void sendMessageToClient(ClientMessage message) throws IOException {
        CommClient client = clients.get(message.getUid());
        if(null == client) {
            throw new IOException("Couldn't find client socket");
        }
        SocketChannel socket = (SocketChannel)client.getKey().channel();

        tcp.writeToChannel(socket, message.getMessage());
    }
}