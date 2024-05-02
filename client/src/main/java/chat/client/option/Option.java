package chat.client.option;

import java.util.Map;
import chat.common.request.Request;
import chat.client.Parameter;

public interface Option {
    Request createRequest(Map<Integer, Object> values);

    String getDescription();

    Map<Integer, Parameter> getParameters();

}
