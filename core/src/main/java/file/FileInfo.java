package file;

import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class FileInfo implements Serializable {

    public enum FileType {
        FILE("F"),
        DIRECTORY("D"),
        BACKWARD("B");

        private String name;

        public String getName() {
            return name;
        }

        FileType(String name) {
            this.name = name;
        }
    }

    private String name;
    private FileType type;
    private long size;

    public FileInfo(Path path) {
        try {
            this.name = path.getFileName().toString();
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            this.size = (this.type == FileType.DIRECTORY) ? -1L : Files.size(path);
        } catch (IOException e) {
            throw new RuntimeException("Unable to get file info");
        }
    }

    public FileInfo(String name, FileType type) {
        this.name = name;
        this.type = type;
        this.size = -2L;
    }
}


