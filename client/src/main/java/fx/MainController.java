package fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import message.*;
import properties.NetworkProperties;
import file.FileInfo;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import netty.Network;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class MainController implements Initializable {

    private Stage authStage;
    private AuthController authController;
    @FXML
    private TableView fileTableView;
    @FXML
    private TextField statusBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openConnection(NetworkProperties.getHost(), NetworkProperties.getPort());
        openAuthDialog();
        initFileTableView();
        AddMouseListener();
    }

    private void createAuthStage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/auth.fxml"));
            Parent parent = loader.load();
            authController = loader.getController();
            authStage = new Stage();
            authStage.setOnCloseRequest(event -> {
                closeApp();
            });
            authStage.setTitle("Connection");
            authStage.setScene(new Scene(parent));
            authStage.initModality(Modality.APPLICATION_MODAL);
            authStage.setResizable(false);
        } catch (IOException e) {
            log.error("Error creating the authorization window", e);
            closeApp();
        }
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }

    private void openAuthDialog() {
        long waitTime = System.currentTimeMillis();

        createAuthStage();

        while (!Network.network.isConnected()) {
            if (System.currentTimeMillis() - waitTime > 5000) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                alert.setHeaderText("Failed to connect to the server");
                alert.showAndWait();
                closeApp();
            }
            sleep(100);
        }

        while (true) {
            if (NetworkProperties.isAuthSuccess()) {
                authStage.close();
                break;
            } else if (!NetworkProperties.isAuthRequestSent()) {
                authStage.showAndWait();
                sleep(100);
            }
        }
    }

    private void initFileTableView() {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(25);
        fileTypeColumn.setCellFactory(column -> {
            final Image dirUpImage = new Image("icons/dir-up.png");
            final Image dirImage = new Image("icons/dir.png");
            final Image fileImage = new Image("icons/file.png");
            TableCell<FileInfo, String> cell = new TableCell<FileInfo, String>() {
                private ImageView imageView = new ImageView();
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        if (item.equals("B")) {
                            imageView.setImage(dirUpImage);
                        } else if (item.equals("D")) {
                            imageView.setImage(dirImage);
                        } else {
                            imageView.setImage(fileImage);
                        }
                        imageView.setFitHeight(16);
                        imageView.setFitWidth(16);
                        setGraphic(imageView);
                    }
                }
            };
            return cell;
        });

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Name");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        fileNameColumn.setPrefWidth(300);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Size");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setPrefWidth(150);
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item <= -1L) {
                            text = "";
                        }
                        setText(text);
                    }
                }
            };
        });

        fileTableView.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn);
        fileTableView.getSortOrder().add(fileTypeColumn);
    }

    private void AddMouseListener() {
        fileTableView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                FileInfo fileInfo = getSelectedFileInfo();
                if (fileInfo == null) {
                    return;
                }
                switch (fileInfo.getType()) {
                    case DIRECTORY:
                        sendDirDownRequest(fileInfo.getName());
                        break;
                    case BACKWARD:
                        sendDirUpRequest();
                        break;
                }
            }
        });
    }

    public void openConnection(String host, int port) {
        Network.network = new Network(message -> {
            log.debug("Received message type: {}", message.getMessageType());
            switch (message.getMessageType()) {
                case FILE_LIST_RESPONSE:
                    FileListResponse fileListResponse = (FileListResponse) message;
                    List<FileInfo> fileInfoList = fileListResponse.getFileInfoList();
                    Platform.runLater(() -> {
                        refreshFileTableView(fileInfoList);
                    });
                    break;
                case DIR_CREATE_RESPONSE:
                    DirCreateResponse dirCreateResponse = (DirCreateResponse) message;
                    if (dirCreateResponse.getResultCode() == -1 ) {
                        showErrorAlert(dirCreateResponse.getResultMessage());
                    } else {
                        sendFileListRequestMessage();
                    }
                    break;
                case FILE_UPLOAD_RESPONSE:
                    FileUploadResponse fileUploadResponse = (FileUploadResponse) message;
                    if (fileUploadResponse.getResultCode() == -1 ) {
                        showErrorAlert(fileUploadResponse.getResultMessage());
                    } else {
                        sendFileListRequestMessage();
                    }
                    break;
                case FILE_DELETE_RESPONSE:
                    FileDeleteResponse fileDeleteResponse = (FileDeleteResponse) message;
                    if (fileDeleteResponse.getResultCode() == -1 ) {
                        showErrorAlert(fileDeleteResponse.getResultMessage());
                    } else {
                        sendFileListRequestMessage();
                    }
                    break;
                case FILE_DOWNLOAD_RESPONSE:
                    FileDownloadResponse fileDownloadResponse = (FileDownloadResponse) message;
                    Platform.runLater(() -> {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Save file as..");
                        File file = fileChooser.showSaveDialog(fileTableView.getScene().getWindow());
                        if (file != null) {
                            try {
                                Files.write(file.toPath(), fileDownloadResponse.getContent());
                            } catch (IOException e) {
                                log.error("", e);
                                showErrorAlert("Error saving the file");
                            }
                        }
                    });
                    break;
                case DIR_DOWN_RESPONSE:
                case DIR_UP_RESPONSE:
                    sendFileListRequestMessage();
                    break;
                case AUTH_RESPONSE:
                    AuthResponse authResponse = (AuthResponse) message;
                    if (authResponse.getResultCode() == 0) {
                        statusBar.setText("Username: " + authResponse.getUserName());
                        NetworkProperties.setAuthSuccess(true);
                        sendFileListRequestMessage();
                    } else {
                        NetworkProperties.setAuthRequestSent(false);
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                            alert.setHeaderText(authResponse.getResultMessage());
                            alert.showAndWait();
                            authController.clearFields();
                        });
                    }
                    break;
            }
        }, host, port);
    }

    public void closeConnection() {
        if (Network.network != null) {
            Network.network.close();
            log.info("Network closed");
        }
    }

    private void showErrorAlert(String headerText) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            alert.setHeaderText(headerText);
            alert.showAndWait();
        });
    }

    private void refreshFileTableView(List<FileInfo> fileInfoList) {
        fileTableView.getItems().clear();
        fileTableView.getItems().addAll(fileInfoList);
        fileTableView.sort();
    }

    private FileInfo getSelectedFileInfo() {
        if (fileTableView.getItems().isEmpty()) {
            return null;
        } else if (fileTableView.getSelectionModel().getSelectedCells().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "File not selected", ButtonType.OK);
            alert.showAndWait();
            return null;
        } else {
            TablePosition pos = (TablePosition) fileTableView.getSelectionModel().getSelectedCells().get(0);
            int row = pos.getRow();
            FileInfo result = (FileInfo) fileTableView.getItems().get(row);
            return result;
        }
    }

    private void closeApp() {
        closeConnection();
        System.exit(0);
    }

    private void sendFileListRequestMessage() {
        Network.network.writeMessage(new FileListRequest());
    }

    private void sendCreateDirRequestMessage(String name) {
        Network.network.writeMessage(new DirCreateRequest(name));
    }

    private void fileUpload(Path path) {
        try {
            FileUploadRequest fileUploadRequest = new FileUploadRequest(path);
            Network.network.writeMessage(fileUploadRequest);
        } catch (Exception e) {
            log.error("Upload File error: ", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            alert.setContentText("File upload error");
            alert.showAndWait();
        }
    }

    private void sendFileDeleteRequest(String name) {
        Network.network.writeMessage(new FileDeleteRequest(name));
    }

    private void sendFileDownloadRequest(String name) {
        Network.network.writeMessage(new FileDownloadRequest(name));
    }

    private void sendDirDownRequest(String name) {
        Network.network.writeMessage(new DirDownRequest(name));
    }

    private void sendDirUpRequest() {
        Network.network.writeMessage(new DirUpRequest());
    }

    public void actionRefresh(ActionEvent actionEvent) {
        sendFileListRequestMessage();
    }

    public void actionCreateDir(ActionEvent actionEvent) {
        TextInputDialog inputDialog = new TextInputDialog("");
        inputDialog.setTitle("New directory");
        inputDialog.setHeaderText("Enter directory name");
        Optional<String> result = inputDialog.showAndWait();
        if (result.isPresent() && !result.get().trim().equals("")) {
            sendCreateDirRequestMessage(result.get().trim());
        }
    }

    public void actionUploadFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the file to upload..");
        File file = fileChooser.showOpenDialog(fileTableView.getScene().getWindow());
        if (file != null) {
            fileUpload(file.toPath());
        }
    }

    public void actionFileDelete(ActionEvent event) {
        FileInfo fileInfo = getSelectedFileInfo();
        if (fileInfo == null) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("File deletion..");
        alert.setContentText("Do you want to delete the file?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            sendFileDeleteRequest(fileInfo.getName());
        }
    }

    public void actionFileDownload(ActionEvent actionEvent) {
        FileInfo fileInfo = getSelectedFileInfo();
        if (fileInfo == null) {
            return;
        }
        sendFileDownloadRequest(fileInfo.getName());
    }

    public void actionExit(ActionEvent event) {
        closeApp();
    }
}

