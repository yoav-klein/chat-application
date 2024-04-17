
package chat.common.exception;

public class BadRequestException extends ChatException {
    public BadRequestException() {
        super();
    }

    public BadRequestException(String message) {
        super(message);
    }
}