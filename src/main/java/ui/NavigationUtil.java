package ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationUtil {

    public static final double LARGHEZZA_FINESTRA = 1280;
    public static final double ALTEZZA_FINESTRA = 800;

    public static <T> T cambiaScena(Stage stage, String percorsoFxml, String titolo) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(percorsoFxml));
        Parent root = loader.load();

        boolean eraMassimizzata = stage.isMaximized();

        stage.setScene(new Scene(root));

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
}