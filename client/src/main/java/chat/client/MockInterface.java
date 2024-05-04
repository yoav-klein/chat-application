package chat.client;

import chat.common.request.Request;

public class MockInterface {
    RequestManager reqMan; 
    
    MockInterface(RequestManager reqMan) {
        this.reqMan = reqMan;
    }

    Request getRequest() {
        return null;
    }

}
