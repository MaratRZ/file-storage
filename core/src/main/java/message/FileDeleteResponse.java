package message;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FileDeleteResponse extends AbstractMessage {

    private final int resultCode;
    private final String resultMessage;

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_DELETE_RESPONSE;
    }
}
