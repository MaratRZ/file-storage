package message;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FileDeleteRequest extends AbstractMessage {

    private final String name;

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_DELETE_REQUEST;
    }
}
