package chat.client;

import java.util.ArrayList;
import java.util.List;

import chat.client.option.Option;
import chat.common.request.Request;

public class RequestManager {
        
    List<Option> options = new ArrayList<>();;
    
    void addOption(Option opt) { options.add(opt); }
    List<Option> getOptionList() { return options;  }

}