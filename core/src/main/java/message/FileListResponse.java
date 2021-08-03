package message;

import file.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public class FileListResponse extends AbstractMessage {

    private final List<FileInfo> fileInfoList;

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_LIST_RESPONSE;
    }

    public FileListResponse(List<FileInfo> fileInfoList) {
        this.fileInfoList = fileInfoList;
    }

    public List<FileInfo> getFileInfoList() {
        return fileInfoList;
    }
}
