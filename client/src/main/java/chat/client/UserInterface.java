package chat.client;

import chat.common.request.Request;
import chat.common.servermessage.ChatPayload;
import chat.common.servermessage.StatusPayload;

public interface UserInterface {
    public Request getRequest();

    public void processStatusMessage(StatusPayload response);

    public void processChatMessage(ChatPayload response);
}
