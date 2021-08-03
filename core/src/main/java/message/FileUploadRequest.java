package message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static message.MessageType.FILE_UPLOAD_REQUEST;

@Data
public class FileUploadRequest extends AbstractMessage {

    private final String name;
    private final byte[] content;

    public FileUploadRequest(Path path) throws IOException {
        this.name = path.getFileName().toString();
        this.content = Files.readAllBytes(path);
    }

    @Override
    public MessageType getMessageType() {
        return FILE_UPLOAD_REQUEST;
    }
}
