
package chat.common.request;

public enum RequestType {
    LOGIN,
    SEND_MESSAGE_TO_USER,
    SEND_MESSAGE_TO_GROUP,
    CREATE_GROUP,
    JOIN_GROUP,
    LIST_USERS_IN_GROUP,
    LIST_GROUPS_OF_USER,
    LEAVE_GROUP,
    STOP_CLIENT
}