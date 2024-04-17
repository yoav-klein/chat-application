
package chat.common.util;

import java.nio.channels.SocketChannel;
import java.nio.charset.Charset; 
import java.nio.ByteBuffer;

import java.io.IOException;

import chat.common.exception.*;


public class TCPCommunication {
    private static final Charset charset = Charset.forName("UTF-8");
    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    public TCPCommunication() {

    }

    public String readFromChannel(SocketChannel channel) throws ClosedConnectionException, IOException {    
        buffer.clear();
        int readBytes = channel.read(buffer);
        if(-1 == readBytes) {
            throw new ClosedConnectionException();
        }
        buffer.flip(); // set position back to 0 before decode
        String commandString = charset.decode(buffer).toString();

        return commandString;
    }

    public void writeToChannel(SocketChannel channel, String message) throws IOException {
        channel.write(charset.encode(message));
    }
}