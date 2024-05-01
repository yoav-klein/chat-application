package chat.server;

import java.util.ArrayList;
import java.util.List;

class User {
    private final String name;
    private List<Group> groups = new ArrayList<Group>();

    User(String name) {
        this.name = name;
    }

    String getName() {
        return this.name;
    }

    void addGroup(Group group) {
        groups.add(group);
    }
}