
package chat.client;

import java.io.IOException;
import chat.common.exception.*;
import chat.common.servermessage.ChatPayload;
import chat.common.servermessage.ServerMessageType;
import chat.common.servermessage.StatusPayload;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;


class ServerThread extends Thread {
    private boolean shouldStop = false;
    private Communication comm;
    private Object synchronizer;
    private StatusPayload currentStatus;

    ServerThread(Communication comm, Object synchronizer, StatusPayload currentStatus) {
        this.comm = comm;
        this.synchronizer = synchronizer;
        this.currentStatus = currentStatus;
    }

    void stopRunning() throws IOException {
        comm.close();
    }

    boolean isConnectionClosed() {
        return this.shouldStop;
    }

    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            while(true) {
                String message = comm.readFromServer();
                
                JsonNode json = mapper.readTree(message);
                String type = json.get("type").textValue();
                
                if(type.equals(ServerMessageType.STATUS.toString())) {
                    System.out.println("Got status back");
            
                    StatusPayload status = mapper.treeToValue(json.get("payload"), StatusPayload.class);

                    currentStatus.requestId = status.requestId;
                    currentStatus.status = status.status;
                    currentStatus.message = status.message;
                    
                    synchronized(synchronizer) {
                        synchronizer.notifyAll();
                    }

                } else if(type.equals(ServerMessageType.CHAT.toString())) {
                    ChatPayload chat = mapper.treeToValue(json.get("payload"), ChatPayload.class);
                    System.out.println(chat.from + ": " + chat.message);
                }
                
            }
        } catch(ClosedConnectionException e) {
            System.err.println("ClosedConnectionException");
            shouldStop = true;
            return;
        } catch(JsonParseException e) {
            System.err.println(e);
            return;
        } catch(IOException e) {
            System.err.println(e);
            shouldStop = true;
            return;
        } 
        
    }
}