
package chat.server;

public class ClosedConnectionException extends Exception {
    private int uid;

    public ClosedConnectionException() {
        super();
    }
    public ClosedConnectionException(int uid) {
        this.uid = uid;
    }

    public ClosedConnectionException(String message) {
        super(message);
    }

    public int getUid() {
        return this.uid;
    }
}