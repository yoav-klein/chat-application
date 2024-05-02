package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yoav.consolemenu.*;
import chat.client.option.*;
import chat.common.request.Request;

public class ConsoleInterface implements UserInterface {
    
    RequestManager.RequestContainer current = new RequestManager.RequestContainer();
    RequestManager reqManager;
    IDGenerator idGenerator;
    ConsoleMenu consoleMenu = new ConsoleMenu("Main Menu");
    
    
    ConsoleInterface (RequestManager reqManager, IDGenerator idGenerator) {
        class GeneralCommand implements Command {
            Option option;
            RequestManager.RequestContainer currentRequest;
    
            public GeneralCommand(Option option, RequestManager.RequestContainer currentRequest) {
                this.option = option;
                this.currentRequest = currentRequest;
            }
        
            public void execute() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                Map<Integer, Parameter> parameters = option.getParameters();
                Map<Integer, Object> values = new HashMap<>();
                
                for(Integer id : parameters.keySet()) {
                    Parameter parameter = parameters.get(id);
                    System.out.println(parameter.getDescription());
                    try {
                        String userInput = reader.readLine();
                        Object value = parameter.getType().getConstructor(String.class).newInstance(userInput);
                        values.put(id, value);
                    } catch(IOException e) {
                        e.printStackTrace();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                Request request = option.createRequest(values);
                currentRequest.currRequest = request;
            }
        }
        this.idGenerator = idGenerator;
        this.reqManager = reqManager;

        List<Option> optlist = reqManager.getOptionList();
        for(Option op : optlist) {
	        consoleMenu.addMenuItem(op.getDescription(), new GeneralCommand(op, current));
        }

    }

    public Request getRequest() {
        consoleMenu.displayMenu();
        consoleMenu.getUserChoice();
        
        return current.currRequest;
    }
}