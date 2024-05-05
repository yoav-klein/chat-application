package chat.client;

import chat.common.request.Request;
import chat.common.servermessage.ChatPayload;
import chat.common.servermessage.StatusPayload;

public abstract class UserInterface {
    protected RequestManager requestManager;

    abstract Request getRequest();

    abstract void processStatusMessage(StatusPayload response);

    abstract void processChatMessage(ChatPayload response);

    void setRequestManager(RequestManager reqMan) { this.requestManager = requestManager; }    
}
