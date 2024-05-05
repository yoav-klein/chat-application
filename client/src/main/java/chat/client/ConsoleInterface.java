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
import chat.common.servermessage.ChatPayload;
import chat.common.servermessage.StatusMessageType;
import chat.common.servermessage.StatusPayload;
import chat.common.util.Logger;

public class ConsoleInterface implements UserInterface {
    private static class RequestContainer {
        public Request currRequest;
    }

    
    RequestContainer current = new RequestContainer();
    RequestManager reqManager;
    ConsoleMenu consoleMenu = new ConsoleMenu("Main Menu");
    
    
    ConsoleInterface (RequestManager reqManager) {
        class GeneralCommand implements Command {
            Option option;
            RequestContainer currentRequest;
    
            public GeneralCommand(Option option, RequestContainer currentRequest) {
                this.option = option;
                this.currentRequest = currentRequest;
            }
        
            public void execute() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                Map<Integer, Parameter> parameters = option.getParameters();
                Map<Integer, Object> values = new HashMap<>();
                
                for(Integer parameterId : parameters.keySet()) {
                    Parameter parameter = parameters.get(parameterId);
                    System.out.println(parameter.getDescription());
                    try {
                        String userInput = reader.readLine();
                        Object value = parameter.getType().getConstructor(String.class).newInstance(userInput);
                        values.put(parameterId, value);
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

    public void processStatusMessage(StatusPayload response) {
        switch(current.currRequest.getType()) {
            case LOGIN:
            case SEND_MESSAGE_TO_USER:
            case SEND_MESSAGE_TO_GROUP:
            case CREATE_GROUP:
            case JOIN_GROUP:
            case LEAVE_GROUP:
                if(response.status == StatusMessageType.SUCCESS) {
                    System.out.println(response.message);
                } else {
                    System.out.println("Request failed");
                    System.out.println(response.message);
                }
                break;
            case LIST_USERS_IN_GROUP:
                if(response.status == StatusMessageType.SUCCESS) {
                    System.out.println("Users in group: " + response.message);
                } else {
                    System.out.println("Request failed");
                    System.out.println(response.message);
                }
                break;
            case LIST_GROUPS_OF_USER:
                if(response.status == StatusMessageType.SUCCESS) {
                    System.out.println("Your groups: " + response.message);
                } else {
                    System.out.println("Request failed");
                    System.out.println(response.message);
                }
                System.out.println(response.message);
                break;
            }
    }

    public void processChatMessage(ChatPayload response) {
        System.out.println(response.from + "-> " + response.to + ": " + response.message);
    }
}