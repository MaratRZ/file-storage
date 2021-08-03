package message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static message.MessageType.FILE_UPLOAD_RESPONSE;

@AllArgsConstructor
@Data
public class FileUploadResponse extends AbstractMessage {

    private final int resultCode;
    private final String resultMessage;

    @Override
    public MessageType getMessageType() {
        return FILE_UPLOAD_RESPONSE;
    }
}
