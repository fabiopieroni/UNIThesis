package ui.controllers;

import business.Sessione;
import business.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Utente;

import java.io.IOException;

public class SceltaRuoloController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private VBox menuBox;

    private final AuthService authService = new AuthService();

    @FXML
    private void initialize() {
        Utente utente = Sessione.getInstance().getUtenteCorrente();

        welcomeLabel.setText("Benvenuto, " + utente.getNome() + " " + utente.getCognome());
        roleLabel.setText("Ruolo: " + utente.getRuolo());

        costruisciMenu();
    }

    private void costruisciMenu() {
        menuBox.getChildren().clear();

        if (Sessione.getInstance().isStudente()) {
            menuBox.getChildren().addAll(
                    creaBottoneNavigazione("Gestione Tesi", "/fxml/CatalogoTesi.fxml", "Catalogo Tesi"),
                    creaBottone("Gestione Candidature"),
                    creaBottone("Gestione Revisioni")
            );
        } else if (Sessione.getInstance().isProfessore()) {
            menuBox.getChildren().addAll(
              creaBottoneNavigazione("Gestione Tesi", "/fxml/GestioneTesi.fxml", "Gestione Tesi"),
                    creaBottone("Gestione Candidature"),
                    creaBottone("Gestione Revisioni")
            );
        } else if (Sessione.getInstance().isSegreteria()) {
            menuBox.getChildren().addAll(
                    creaBottone("Gestione Tesi"),
                    creaBottone("Gestione Utenti")
            );
        }
    }

    private Button creaBottone(String testo) {
        Button b = new Button(testo);
        b.setMaxWidth(250);
        b.setOnAction(e -> mostraNonImplementato(testo));
        return b;
    }

  private Button creaBottoneNavigazione(String testo, String percorsoFxml, String titoloFinestra) {
    Button b = new Button(testo);
    b.setMaxWidth(250);
    b.setOnAction(e -> {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(percorsoFxml));
        Parent root = loader.load();
        Stage stage = (Stage) b.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(titoloFinestra);
        stage.centerOnScreen();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    });
    return b;
  }

    // Placeholder: qui in futuro collegherai le vere schermate
    // (es. caricare GestioneTesi.fxml con il relativo controller
    // che userà GestioneTesiController/impl del package business)
    private void mostraNonImplementato(String funzione) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("In sviluppo");
        alert.setHeaderText(null);
        alert.setContentText("La funzione \"" + funzione + "\" non è ancora implementata.");
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        authService.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
