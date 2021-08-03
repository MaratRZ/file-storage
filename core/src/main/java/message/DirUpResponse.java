package message;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DirUpResponse extends AbstractMessage {

    private final int resultCode;
    private final String resultMessage;

    @Override
    public MessageType getMessageType() {
        return MessageType.DIR_UP_RESPONSE;
    }
}
