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
                    creaBottoneCandidature(),
                    creaBottoneRevisioni()
            );
        } else if (Sessione.getInstance().isProfessore()) {
            menuBox.getChildren().addAll(
                    creaBottoneNavigazione("Gestione Tesi", "/fxml/GestioneTesi.fxml", "Gestione Tesi"),
                    creaBottoneCandidature(),
                    creaBottoneRevisioniProf(),
                    creaBottoneTesisti()
            );
        } else if (Sessione.getInstance().isSegreteria()) {
            menuBox.getChildren().addAll(
                    creaBottoneNavigazione("Gestione Tesi", "/fxml/GestioneTesiSegreteria.fxml", "Gestione Tesi"),
                    creaBottoneNavigazione("Gestione Utenti", "/fxml/GestioneUtenti.fxml", "Gestione Utenti")
            );
        }
    }

    private Button creaBottoneCandidature() {
        Button b = new Button("Gestione Candidature");
        b.setMaxWidth(250);
        b.setOnAction(e -> {
            String fxml = Sessione.getInstance().isStudente()
                    ? "/fxml/CandidaturaForm.fxml"
                    : "/fxml/GestioneCandidature.fxml";
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                Parent root = loader.load();
                Stage stage = (Stage) menuBox.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
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

    private Button creaBottoneRevisioni() {
        Button b = new Button("Gestione Revisioni");
        b.setMaxWidth(250);
        b.setOnAction(e -> {
            Utente utente = Sessione.getInstance().getUtenteCorrente();
            Integer idTesi = new dao.impl.RichiestaDAOimpl()
                    .trovaIdTesiAccettataPerStudente(utente.getIdUtente());

            if (idTesi == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Nessuna tesi assegnata");
                alert.setHeaderText(null);
                alert.setContentText("Non hai ancora una tesi assegnata. Attendi che un professore accetti la tua candidatura.");
                alert.showAndWait();
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RevisioneCapitolo.fxml"));
                Parent root = loader.load();

                RevisioneCapitoloFXController controller = loader.getController();
                controller.initData(idTesi);

                Stage stage = (Stage) menuBox.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Gestione Revisioni");
                stage.centerOnScreen();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        return b;
    }

    private Button creaBottoneRevisioniProf() {
        Button b = new Button("Gestione Revisioni");
        b.setMaxWidth(250);
        b.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestioneRevisioniProf.fxml"));
                Parent root = loader.load();

                GestioneRevisioniProfFXController controller = loader.getController();
                controller.mostraTutte();

                Stage stage = (Stage) menuBox.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Gestione Revisioni");
                stage.centerOnScreen();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        return b;
    }

    private Button creaBottoneTesisti() {
        Button b = new Button("I miei Tesisti");
        b.setMaxWidth(250);
        b.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TesistiAttivi.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) menuBox.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("I miei tesisti");
                stage.centerOnScreen();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        return b;
    }

}