
package chat.server;

class ClientMessage {
    private String message;
    private int uid;

    ClientMessage(int uid, String message) {
        this.uid = uid;
        this.message = message;
    }

    String getMessage() {
        return this.message;
    }

    int getUid() {
        return this.uid;
    }
}