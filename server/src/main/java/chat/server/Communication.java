
package chat.server;

import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.util.*;

import java.io.*;

import chat.common.util.*;
import chat.common.exception.ClosedConnectionException;
import chat.common.exception.ServerStopException;

class Communication {
    private static Selector selector;
    private TCPCommunication tcp = new TCPCommunication();
    
    private ServerSocketChannel serverSocket;
    IDGenerator idGen = new IDGenerator();

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
        
        // now, we create a Selector instance
        selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        
    }
    
    private void registerClient(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept(); // accept the connection from client
        client.configureBlocking(false);
        SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ); // register, this time with OP_READ because this socket is for reading, not receiving connections
        
        int nextId = idGen.getId();
        CommClient newClient = new CommClient(nextId, clientKey);
        clients.put(nextId, newClient);
        clientKey.attach(newClient); // in order to associate key with client
    }

    
    private String readClientRequest(SelectionKey selectionKey) throws IOException, ClosedConnectionException {
        SocketChannel client = (SocketChannel)selectionKey.channel();

        String message;
        try {
            message = tcp.readFromChannel(client);
        } catch(java.net.SocketException e) {
            throw new ClosedConnectionException();
        }
        return message;
    }
    

    void sendMessageToClient(int uid, String message) throws IOException {
        CommClient client = clients.get(uid);
        if(null == client) {
            throw new IOException("Couldn't find client socket");
        }
        SocketChannel socket = (SocketChannel)client.getKey().channel();

        tcp.writeToChannel(socket, message);
    }

    void stop() throws IOException {
        selector.close();
    }
    
    ClientMessage getClientRequest() throws ClosedConnectionException, ServerStopException, IOException  {
        while(true) {
            
            selector.select();
            
            if(!selector.isOpen()) {
                throw new ServerStopException();
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while(iter.hasNext()) {
                SelectionKey curr = iter.next();
                iter.remove();

                if(curr.isAcceptable()) {
                    registerClient(selector, serverSocket);
                }

                if(curr.isReadable()) {
                    CommClient client = (CommClient)curr.attachment();
                    String request;
                    try {
                        request = readClientRequest(curr);
                    } catch(ClosedConnectionException e) {
                        try {
                            curr.channel().close();
                        } catch(IOException e1) {
                            System.err.println("Couldn't close channel");
                            System.err.println(e1);
                        }
                        curr.cancel();
                        throw new ClosedConnectionException(client.getUid()); // will be caught in server to handle logout
                    } 


                    return new ClientMessage(client.getUid(), request);
                }
            }
        }
    }
}

