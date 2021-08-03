package message;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DirDownRequest extends AbstractMessage {

    private final String name;

    @Override
    public MessageType getMessageType() {
        return MessageType.DIR_DOWN_REQUEST;
    }
}
