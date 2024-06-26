
package chat.client;

import java.io.IOException;
import chat.common.exception.*;
import chat.common.servermessage.ChatPayload;
import chat.common.servermessage.ServerMessageType;
import chat.common.servermessage.StatusPayload;
import chat.common.util.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;


class ServerThread extends Thread {
    private boolean shouldStop = false;
    private Communication comm;
    private Object synchronizer;
    private StatusPayload currentStatus;
    private UserInterface userInterface;

    ServerThread(Communication comm, Object synchronizer, StatusPayload currentStatus, UserInterface userInterface) {
        this.comm = comm;
        this.synchronizer = synchronizer;
        this.currentStatus = currentStatus;
        this.userInterface = userInterface;
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
            
                    StatusPayload status = mapper.treeToValue(json.get("payload"), StatusPayload.class);
                    
                    Logger.debug("Status: " + status.status + ", requestId: "  + status.requestId + " : " + status.message);

                    currentStatus.requestId = status.requestId;
                    currentStatus.status = status.status;
                    currentStatus.message = status.message;
                    
                    synchronized(synchronizer) {
                        Logger.debug("notifyAll in ServerThread");
                        synchronizer.notifyAll();
                    }

                } else if(type.equals(ServerMessageType.CHAT.toString())) {
                    ChatPayload chat = mapper.treeToValue(json.get("payload"), ChatPayload.class);
                    userInterface.processChatMessage(chat);
                }
                
            }
        } catch(ClosedConnectionException e) {
            Logger.info("ClosedConnectionException");
            shouldStop = true;
            return;
        } catch(JsonParseException e) {
            Logger.error(e.getMessage());
            return;
        } catch(IOException e) {
            Logger.error("IOException in ServerThread: " + e.getMessage());
            shouldStop = true;
            return;
        } 
        
    }
}