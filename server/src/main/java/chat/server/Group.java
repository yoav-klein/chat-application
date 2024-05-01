package chat.server;

import java.util.ArrayList;
import java.util.List;

import chat.common.util.Logger;

class Group {
    private String name;
    private List<User> users;

    Group(String name, User owner) {
        this.users = new ArrayList<>();
        this.users.add(owner);
        owner.addGroup(this);
        this.name = name;
    }

    List<User> getUsers() {
        return this.users;
    }

    void addUser(User user) {
        this.users.add(user);
    }

    void removeUser(User user) {
        Logger.debug("Removing user " + user.getName() + " from group " + this.name);
        this.users.remove(user);
    }
    
}
