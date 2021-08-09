package message;

import lombok.AllArgsConstructor;
import lombok.Data;

import static message.MessageType.DIR_CREATE_RESPONSE;

@AllArgsConstructor
@Data
public class DirCreateResponse extends AbstractMessage {

    private final int resultCode;
    private final String resultMessage;

    @Override
    public MessageType getMessageType() {
        return DIR_CREATE_RESPONSE;
    }
}
