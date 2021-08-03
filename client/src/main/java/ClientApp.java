import fx.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import properties.Config;

public class ClientApp extends Application {
    private static String HOST = "localhost";
    private static int PORT = 8189;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Config.setHost(HOST);
        Config.setPort(PORT);
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("File Storage");
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.setOnCloseRequest(event -> {
            MainController controller = loader.getController();
            controller.closeConnection();
            System.exit(0);
        });
        primaryStage.show();
    }
}
