package chat.client;

import chat.client.option.Option;
import chat.common.request.Request;
import chat.common.servermessage.ChatPayload;
import chat.common.servermessage.StatusPayload;
import java.util.Map;

public class MockInterface extends UserInterface {
    static class Synchronizer {
        boolean requestReady = false;
        boolean statusReady = false;
        boolean chatReady = false;
    }

    class CurrentRequest {
        String description;
        Map<Integer, Object> values;
    }

    class CurrentResponse {
        StatusPayload statusPayload;
        ChatPayload chatPayload;
    }

    private RequestManager reqMan;
    private Synchronizer synchronizer = new Synchronizer();
    private CurrentRequest currentRequest = new CurrentRequest();
    private CurrentResponse currentResponse = new CurrentResponse();
    
    MockInterface() {}

    public void setRequestManager(RequestManager reqMan) {
        this.reqMan = reqMan;
    }

    @Override
    public Request getRequest() {
        synchronized(synchronizer) {
            while(synchronizer.requestReady == false) {
                try {
                    synchronizer.wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        Request request = null;
        for(Option option : reqMan.getOptionList()) {
            if(option.getDescription().equals(currentRequest.description)) {
                request = option.createRequest(currentRequest.values);
            }
        };
        synchronizer.requestReady = false;
        return request;
    }

    @Override
    public void processStatusMessage(StatusPayload response) {
        currentResponse.statusPayload = response;
        synchronizer.statusReady = true;
        synchronized(synchronizer) {
            synchronizer.notifyAll();
        }
    }

    @Override
    public void processChatMessage(ChatPayload response) {
        currentResponse.chatPayload = response;
        synchronizer.chatReady = true;
        synchronized(synchronizer) {
            synchronizer.notifyAll();
        }
    }

    
    public void setRequest(String description, Map<Integer, Object> values) {
        currentRequest.description = description;
        currentRequest.values = values;
        synchronized(synchronizer) {
            synchronizer.requestReady = true;
            synchronizer.notifyAll();
        }
    }
    
    public StatusPayload getStatusPayload() {
        synchronized(synchronizer) {
            while(synchronizer.statusReady == false) {
                try {
                    synchronizer.wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        synchronizer.statusReady = false;
        return currentResponse.statusPayload;
    }

    public ChatPayload getChatPayload() {
        synchronized(synchronizer) {
            while(synchronizer.chatReady == false) {
                try {
                    synchronizer.wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        synchronizer.chatReady = false;
        return currentResponse.chatPayload;
    }

}
