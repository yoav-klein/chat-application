package chat.server;

class User {
    private final String name;

    User(String name) {
        this.name = name;
    }

    String getName() {
        return this.name;
    }
}