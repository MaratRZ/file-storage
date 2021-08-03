package message;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FileDownloadRequest extends AbstractMessage {

    String name;

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_DOWNLOAD_REQUEST;
    }
}
