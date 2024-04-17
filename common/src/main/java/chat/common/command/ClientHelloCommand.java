
package chat.common.command;

public class ClientHelloCommand extends Command {
    private String userName;

    public ClientHelloCommand() {
        super(CommandType.CLIENT_HELLO);
    }
    
    public ClientHelloCommand(String userName) {
        super(CommandType.CLIENT_HELLO);
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }
}