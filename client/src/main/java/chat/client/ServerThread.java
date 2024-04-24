
package chat.client;

import java.io.IOException;
import chat.common.exception.*;
import chat.common.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

class ServerThread extends Thread {
    private boolean shouldStop = false;
    private Communication comm;

    ServerThread(Communication comm) {
        this.comm = comm;
    }

    void stopRunning() throws IOException {
        comm.close();
    }

    boolean isConnectionClosed() {
        return this.shouldStop;
    }

    public void run() {
        try {
            while(true) {
                String message = comm.readFromServer();
                ObjectMapper mapper = new ObjectMapper();
                ServerMessage serverMessage = mapper.readValue(message, ServerMessage.class);
                System.out.println(serverMessage.getType() + ": " + serverMessage.getMessage());
                
            }
        } catch(ClosedConnectionException e) {
            System.err.println("ClosedConnectionException");
            shouldStop = true;
            return;
        } catch(JsonParseException e) {
            System.err.println(e);
            return;
        }
        catch(IOException e) {
            System.err.println(e);
            shouldStop = true;
            return;
        } 
        
    }
}