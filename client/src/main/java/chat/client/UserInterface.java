package chat.client;

import chat.common.request.Request;
import chat.common.servermessage.ChatPayload;
import chat.common.servermessage.StatusPayload;

public abstract class UserInterface {
    protected RequestManager requestManager;

    abstract public Request getRequest();

    abstract public void processStatusMessage(StatusPayload response);

    abstract public void processChatMessage(ChatPayload response);

    public void setRequestManager(RequestManager reqMan) { this.requestManager = requestManager; }    
}
