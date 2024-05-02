package chat.client.option;

import java.util.Map;
import chat.common.request.Request;
import chat.client.Parameter;

import chat.client.IDGenerator;

public abstract class Option {
    abstract public Request createRequest(Map<Integer, Object> values);

    abstract public String getDescription();

    abstract public Map<Integer, Parameter> getParameters();

    protected int getRequestId() {
        return IDGenerator.getInstance().getId();
    }

}
