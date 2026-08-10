package ui.controllers;
import javafx.scene.Node;
import business.Sessione;
import dao.RichiestaDAO;
import dao.TesiDAO;
import dao.impl.RichiestaDAOimpl;
import dao.impl.TesiDAOimpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Richiesta;
import model.Studente;
import model.Tesi;

import java.io.IOException;
import java.util.List;

public class CandidaturaFormController {

    @FXML private ListView<Tesi> listaTesi;
    @FXML private TextArea motivazioneField;
    @FXML private Label feedbackLabel;

    private final TesiDAO tesiDAO = new TesiDAOimpl();
    private final RichiestaDAO richiestaDAO = new RichiestaDAOimpl();

    @FXML
    private void initialize() {
        List<Tesi> disponibili = tesiDAO.trovaDisponibili();
        listaTesi.getItems().addAll(disponibili);

        listaTesi.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Tesi tesi, boolean empty) {
                super.updateItem(tesi, empty);
                setText(empty || tesi == null ? null : tesi.getTitolo() + " — " + tesi.getCorsoLaurea());
            }
        });
    }

    @FXML
    private void handleInvia(ActionEvent event) {
        Tesi selezionata = listaTesi.getSelectionModel().getSelectedItem();
        if (selezionata == null) {
            feedbackLabel.setText("Seleziona una tesi prima di candidarti.");
            return;
        }

        Studente studente = (Studente) Sessione.getInstance().getUtenteCorrente();
        Richiesta richiesta = new Richiesta(
                studente.getIdUtente(),
                selezionata.getIdTesi(),
                motivazioneField.getText()
        );

        boolean ok = richiestaDAO.salvaRichiesta(richiesta);
        if (ok) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Candidatura inviata con successo.");
            alert.showAndWait();
            tornaAlMenu(event);
        } else {
            feedbackLabel.setText("Errore durante l'invio. Riprova.");
        }
    }

    @FXML
    private void handleIndietro(ActionEvent event) {
        tornaAlMenu(event);
    }

    private void tornaAlMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SceltaRuolo.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}