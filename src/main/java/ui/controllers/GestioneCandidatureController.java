package ui.controllers;

import business.Sessione;
import dao.RichiestaDAO;
import dao.impl.RichiestaDAOimpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Professore;
import model.RichiestaConDettagli;

import java.io.IOException;

public class GestioneCandidatureController {

    @FXML private Label postiLabel;
    @FXML private TableView<RichiestaConDettagli> tabellaCandidature;
    @FXML private TableColumn<RichiestaConDettagli, String> colStudente;
    @FXML private TableColumn<RichiestaConDettagli, String> colTesi;
    @FXML private TableColumn<RichiestaConDettagli, String> colData;
    @FXML private TableColumn<RichiestaConDettagli, String> colStato;
    @FXML private TableColumn<RichiestaConDettagli, Void> colAzioni;

    private final RichiestaDAO richiestaDAO = new RichiestaDAOimpl();
    private Professore professore;

    @FXML
    private void initialize() {
        professore = (Professore) Sessione.getInstance().getUtenteCorrente();

        colStudente.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getNomeStudente() + " " + data.getValue().getCognomeStudente()));
        colTesi.setCellValueFactory(new PropertyValueFactory<>("titoloTesi"));
        colData.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(data.getValue().getDataRichiesta())));
        colStato.setCellValueFactory(new PropertyValueFactory<>("stato"));

        aggiungiColonnaAzioni();
        aggiornaVista();
    }

    private void aggiornaVista() {
        tabellaCandidature.getItems().setAll(richiestaDAO.trovaPerProfessore(professore.getIdUtente()));
        postiLabel.setText("Tesisti attivi: " + professore.getNumTesistiAttivi() + " / 5");
    }

    private void aggiungiColonnaAzioni() {
        colAzioni.setCellFactory(col -> new TableCell<>() {
            private final Button accettaBtn = new Button("Accetta");
            private final Button rifiutaBtn = new Button("Rifiuta");
            private final HBox box = new HBox(6, accettaBtn, rifiutaBtn);

            {
                accettaBtn.setOnAction(e -> {
                    RichiestaConDettagli r = getTableView().getItems().get(getIndex());
                    boolean ok = richiestaDAO.accettaRichiesta(r.getId(), professore.getIdUtente());
                    if (ok) {
                        professore.setNumTesistiAttivi(professore.getNumTesistiAttivi() + 1);
                        aggiornaVista();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Hai già raggiunto il limite di 5 tesisti attivi.");
                        alert.showAndWait();
                    }
                });
                rifiutaBtn.setOnAction(e -> {
                    RichiestaConDettagli r = getTableView().getItems().get(getIndex());
                    richiestaDAO.rifiutaRichiesta(r.getId());
                    aggiornaVista();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                RichiestaConDettagli r = getTableView().getItems().get(getIndex());
                boolean pendente = "IN_ATTESA".equals(r.getStato());
                accettaBtn.setDisable(!pendente || professore.getNumTesistiAttivi() >= 5);
                rifiutaBtn.setDisable(!pendente);
                setGraphic(box);
            }
        });
    }

    @FXML
    private void handleIndietro(ActionEvent event) {
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