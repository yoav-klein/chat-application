package chat.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import chat.common.command.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;


public class Client {
    private static ByteBuffer inbuffer = ByteBuffer.allocate(1024);
    private static final Charset charset = Charset.forName("UTF-8"); 
    
    private static String readFromServer(SocketChannel server) throws IOException {
        
        inbuffer.clear();
        int readBytes = server.read(inbuffer);
        if(-1 == readBytes) {
            server.close();
            return null;
        }

        inbuffer.flip(); // set position back to 0 before decode
        String message = charset.decode(inbuffer).toString();

        return message;
    }

    
    private static void writeToServer(SocketChannel server, String message) throws IOException {
        ByteBuffer outBuffer = charset.encode(message);
        // write to socket
        server.write(outBuffer);
    }


    private static SocketChannel initTCPConnection() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));

        return socketChannel;
    }


    private static void sendCommand(SocketChannel sock, Command command) throws JsonProcessingException, IOException {
        ByteBuffer outbuffer;
        
        ObjectMapper mapper = new ObjectMapper();
        String commandJson = mapper.writeValueAsString(command);

        writeToServer(sock, commandJson);

    }

    private static void initConnection(SocketChannel socket) throws IOException {
        ClientHelloCommand clientHello = new ClientHelloCommand("Avi");
        sendCommand(socket, clientHello);
        System.out.println(readFromServer(socket));
    }

    public static void main(String[] args) throws Exception {
        try {
            SocketChannel socket = initTCPConnection();
            initConnection(socket);
            //socket.close();

        } catch(IOException e) {
            System.err.println("Error");
            System.err.println(e);
        }
    }
}

