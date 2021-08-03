package message;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class FileDownloadResponse extends AbstractMessage {

    private final String name;
    private final byte[] content;

    public FileDownloadResponse(Path path) throws IOException {
        this.name = path.getFileName().toString();
        this.content = Files.readAllBytes(path);
    }
    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_DOWNLOAD_RESPONSE;
    }
}
