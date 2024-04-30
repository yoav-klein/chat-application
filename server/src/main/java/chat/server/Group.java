package chat.server;

import java.util.ArrayList;
import java.util.List;

class Group {
    private String name;
    private List<User> users;

    Group(String name, User owner) {
        this.users = new ArrayList<>();
        this.users.add(owner);
        this.name = name;
    }
    
}
