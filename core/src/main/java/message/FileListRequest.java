package message;

public class FileListRequest extends AbstractMessage {

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_LIST_REQUEST;
    }
}
