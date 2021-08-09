package handler;

import db.Database;
import file.FileInfo;
import message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private static String rootDir = "./server/files";
    private Path rootPath = Paths.get(rootDir);
    private Path currentPath = Paths.get(rootDir);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("Client connected..");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) throws Exception {
        log.debug("Received message type: " + message.getMessageType());
        Path path;
        switch (message.getMessageType()) {
            case FILE_LIST_REQUEST:
                FileListResponse fileListResponse =
                        new FileListResponse(Files.list(currentPath)
                                                         .map(FileInfo::new)
                                                         .collect(Collectors.toList()));
                if (!rootPath.equals(currentPath)) {
                    FileInfo parent = new FileInfo("..", FileInfo.FileType.BACKWARD);
                    fileListResponse.getFileInfoList().add(parent);
                }
                ctx.writeAndFlush(fileListResponse);
                break;
            case DIR_CREATE_REQUEST:
                DirCreateRequest dirCreateRequest = (DirCreateRequest) message;
                path = Paths.get(currentPath.toFile().getPath(), dirCreateRequest.getName());
                if (Files.exists(path)) {
                    ctx.writeAndFlush(new DirCreateResponse(-1, String.format("Directory %s already exists", dirCreateRequest.getName())));
                } else {
                    Files.createDirectory(path);
                    ctx.writeAndFlush(new DirCreateResponse(0, "Successful"));
                }
                break;
            case FILE_UPLOAD_REQUEST:
                FileUploadRequest fileUploadRequest = (FileUploadRequest) message;
                path = Paths.get(currentPath.toFile().getPath(), ((FileUploadRequest) message).getName());
                try (FileOutputStream os = new FileOutputStream(path.toFile())) {
                    os.write(fileUploadRequest.getContent());
                } catch (IOException e) {
                    log.error("File write exception: {}", e.getMessage());
                }
                ctx.writeAndFlush(new FileUploadResponse(0, "Successful"));
                break;
            case FILE_DELETE_REQUEST:
                FileDeleteRequest fileDeleteRequest = (FileDeleteRequest) message;
                path = Paths.get(currentPath.toFile().getPath(), fileDeleteRequest.getName());
                Files.deleteIfExists(path);
                ctx.writeAndFlush(new FileDeleteResponse(0, "Successful"));
                break;
            case FILE_DOWNLOAD_REQUEST:
                FileDownloadRequest fileDownloadRequest = (FileDownloadRequest) message;
                path = Paths.get(currentPath.toFile().getPath(), fileDownloadRequest.getName());
                try {
                    FileDownloadResponse fileDownloadResponse = new FileDownloadResponse(path);
                    ctx.writeAndFlush(fileDownloadResponse);
                } catch (Exception e) {
                    log.error("Upload File error: ", e);
                }
                break;
            case DIR_DOWN_REQUEST:
                DirDownRequest dirDownRequest = (DirDownRequest) message;
                currentPath = Paths.get(currentPath.toFile().getPath(), dirDownRequest.getName());
                ctx.writeAndFlush(new DirDownResponse(0, "Successful"));
                break;
            case DIR_UP_REQUEST:
                if (!currentPath.equals(rootPath)){
                    currentPath = currentPath.getParent();
                    ctx.writeAndFlush(new DirUpResponse(0, "Successful"));
                }
                break;
            case AUTH_REQUEST:
                AuthRequest authRequest = (AuthRequest) message;
                String userName = Database.getUsername(authRequest.getLogin(), authRequest.getPassword());
                AuthResponse authResponse = null;
                if (userName == null) {
                    authResponse = new AuthResponse(null, -1, "Incorrect login or password");
                } else {
                    authResponse = new AuthResponse(userName, 0, "Successful");
                    rootPath = Paths.get(rootDir, authRequest.getLogin());
                    currentPath = Paths.get(rootDir, authRequest.getLogin());
                    if (!Files.exists(currentPath)) {
                        Files.createDirectory(currentPath);
                    }
                }
                ctx.writeAndFlush(authResponse);
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Network exception ", cause);
        ctx.close();
    }
}
