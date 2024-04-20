
package chat.client;

import java.io.IOException;
import chat.common.exception.*;

class ServerThread extends Thread {
    private boolean shouldStop = false;
    private Communication comm;

    ServerThread(Communication comm) {
        this.comm = comm;
    }

    void stopRunning() throws IOException {
        comm.close();
    }

    boolean isConnectionClosed() {
        return this.shouldStop;
    }

    public void run() {
        try {
            while(true) {
                String message = comm.readFromServer();
                System.out.println(message);
            }
        } catch(ClosedConnectionException e) {
            shouldStop = true;
            return;
        } catch(IOException e) {
            shouldStop = true;
            return;
        } catch(InterruptedException e) {}
        
    }
}