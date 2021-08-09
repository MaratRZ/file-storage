package message;

import lombok.AllArgsConstructor;
import lombok.Data;

import static message.MessageType.AUTH_REQUEST;

@AllArgsConstructor
@Data
public class AuthRequest extends AbstractMessage {
    private final String login;
    private final String password;

    @Override
    public MessageType getMessageType() {
        return AUTH_REQUEST;
    }
}
