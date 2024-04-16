package chat.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) {
        try (
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            SocketChannel socketChannel = SocketChannel.open();
        ){
            
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));
            
            ByteBuffer inbuffer = ByteBuffer.allocate(1024);
            Charset charset = Charset.forName("UTF-8"); 
            
            while(true) {
                
                // read message from console
                String message = stdin.readLine();
                // encode message to ByteBuffer
                ByteBuffer buffer = charset.encode(message);
                // write to socket
                socketChannel.write(buffer);
                
                inbuffer.clear();
                // read from socket to ByteBuffer
                int readBytes = socketChannel.read(inbuffer);
                if(-1 == readBytes) {
                    socketChannel.close();
                    return;
                }
                System.out.println("position: " + inbuffer.position());
                // flip buffer before decode
                inbuffer.flip();
                // decode ByteBuffer to String
                String received = charset.decode(inbuffer).toString();
                System.out.println("received: " + received);   


            }
           

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

