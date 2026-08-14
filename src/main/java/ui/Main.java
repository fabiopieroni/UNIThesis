package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("UNIThesis - Login");

        Scene scene = new Scene(NavigationUtil.creaWrapperConWatermark(root));
        NavigationUtil.aggiungiStile(scene);
        primaryStage.setScene(scene);

        NavigationUtil.posizionaFinestra(primaryStage);

        primaryStage.show();
        primaryStage.toFront();
        primaryStage.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}