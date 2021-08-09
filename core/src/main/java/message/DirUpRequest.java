package message;

import static message.MessageType.DIR_UP_REQUEST;

public class DirUpRequest extends AbstractMessage {
    @Override
    public MessageType getMessageType() {
        return DIR_UP_REQUEST;
    }
}
