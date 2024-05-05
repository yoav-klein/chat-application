package chat.client;

import java.util.ArrayList;
import java.util.List;

import chat.client.option.Option;

public class RequestManager {
        
    List<Option> options = new ArrayList<>();;
    
    void addOption(Option opt) { options.add(opt); }
    List<Option> getOptionList() { return options;  }

}