package ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationUtil {

    public static final double LARGHEZZA_FINESTRA = 1280;
    public static final double ALTEZZA_FINESTRA = 800;

    public static <T> T cambiaScena(Stage stage, String percorsoFxml, String titolo) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(percorsoFxml));
        Parent root = loader.load();

        boolean eraMassimizzata = stage.isMaximized();

        Scene scene = new Scene(creaWrapperConWatermark(root));
        aggiungiStile(scene);
        stage.setScene(scene);

        if (!eraMassimizzata) {
            posizionaFinestra(stage);
        }

        if (titolo != null) {
            stage.setTitle(titolo);
        }

        return loader.getController();
    }

    public static void posizionaFinestra(Stage stage) {
        stage.setWidth(LARGHEZZA_FINESTRA);
        stage.setHeight(ALTEZZA_FINESTRA);
        Platform.runLater(stage::centerOnScreen);
    }

    public static StackPane creaWrapperConWatermark(Parent contenuto) {
        Label watermark = new Label("UNITHESIS");
        watermark.getStyleClass().add("watermark-label");
        watermark.setMouseTransparent(true);

        StackPane wrapper = new StackPane();
        wrapper.getChildren().addAll(watermark, contenuto);
        StackPane.setAlignment(watermark, Pos.TOP_CENTER);
        StackPane.setMargin(watermark, new javafx.geometry.Insets(80, 0, 0, 0));

        return wrapper;
    }

    public static void aggiungiStile(Scene scene) {
        scene.getStylesheets().add(NavigationUtil.class.getResource("/css/style.css").toExternalForm());
    }
}