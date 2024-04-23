
package chat.client;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.io.IOException;

import chat.common.util.*;
import chat.common.exception.*;

public class Communication {
    
    private SocketChannel channel = SocketChannel.open();
    private TCPCommunication tcp = new TCPCommunication();
    
    Communication(String serverHost, int serverPort) throws IOException {
        channel.connect(new InetSocketAddress(serverHost, 8080));
        channel.configureBlocking(true);
        
    }

    public void writeToServer(String message) throws IOException {
        tcp.writeToChannel(this.channel, message);
    }

    public String readFromServer() throws ClosedConnectionException, IOException {
        return tcp.readFromChannel(this.channel);
    }

    void close() throws IOException {
        System.out.println("closing socket");
        channel.close();
    }
}