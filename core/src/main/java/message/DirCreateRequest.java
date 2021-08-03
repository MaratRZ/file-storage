package message;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DirCreateRequest extends AbstractMessage {

    private final String name;

    @Override
    public MessageType getMessageType() {
        return MessageType.DIR_CREATE_REQUEST;
    }
}
