package message;

import lombok.AllArgsConstructor;
import lombok.Data;

import static message.MessageType.AUTH_RESPONSE;

@AllArgsConstructor
@Data
public class AuthResponse extends AbstractMessage {

    private final String userName;
    private final int resultCode;
    private final String resultMessage;

    @Override
    public MessageType getMessageType() {
        return AUTH_RESPONSE;
    }
}
