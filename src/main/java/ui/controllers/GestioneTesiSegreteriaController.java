package ui.controllers;

import business.GestioneTesiController;
import business.impl.GestioneTesiControllerimpl;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.TesiConDettagli;

import java.io.IOException;
import java.util.List;

public class GestioneTesiSegreteriaController {

    @FXML private TableView<TesiConDettagli> tabellaAccettate;
    @FXML private TableColumn<TesiConDettagli, String> colTitoloAccettate;
    @FXML private TableColumn<TesiConDettagli, String> colProfessoreAccettate;
    @FXML private TableColumn<TesiConDettagli, String> colStudenteAccettate;

    @FXML private ComboBox<String> comboFiltroStato;
    @FXML private TableView<TesiConDettagli> tabellaTutte;
    @FXML private TableColumn<TesiConDettagli, String> colTitoloTutte;
    @FXML private TableColumn<TesiConDettagli, String> colProfessoreTutte;
    @FXML private TableColumn<TesiConDettagli, String> colStudenteTutte;
    @FXML private TableColumn<TesiConDettagli, String> colStatoTutte;

    private final GestioneTesiController gestioneTesi = new GestioneTesiControllerimpl();

    @FXML
    private void initialize() {
        colTitoloAccettate.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        colProfessoreAccettate.setCellValueFactory(new PropertyValueFactory<>("nomeProfessore"));
        colStudenteAccettate.setCellValueFactory(new PropertyValueFactory<>("nomeStudente"));

        colTitoloTutte.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        colProfessoreTutte.setCellValueFactory(new PropertyValueFactory<>("nomeProfessore"));
        colStudenteTutte.setCellValueFactory(new PropertyValueFactory<>("nomeStudente"));
        colStatoTutte.setCellValueFactory(new PropertyValueFactory<>("stato"));

        comboFiltroStato.setItems(FXCollections.observableArrayList(
                "Tutti", "PUBBLICATA", "IN_CORSO", "CONSEGNATA", "ACCETTATA", "ARCHIVIATA"
        ));
        comboFiltroStato.setValue("Tutti");

        caricaAccettate();
        eseguiFiltro();
    }

    private void caricaAccettate() {
        List<TesiConDettagli> lista = gestioneTesi.getTesiAccettate();
        tabellaAccettate.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    private void eseguiFiltro() {
        String filtro = comboFiltroStato.getValue();
        String filtroQuery = "Tutti".equals(filtro) ? null : filtro;
        List<TesiConDettagli> lista = gestioneTesi.getTesiPerVistaSegreteria(filtroQuery);
        tabellaTutte.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    private void archiviaSelezionata(ActionEvent event) {
        TesiConDettagli selezionata = tabellaAccettate.getSelectionModel().getSelectedItem();
        if (selezionata == null) {
            mostraErrore("Seleziona prima una tesi dalla tabella.");
            return;
        }

        boolean ok = gestioneTesi.archiviaTesi(selezionata.getIdTesi());
        if (ok) {
            mostraSuccesso("Tesi archiviata correttamente.");
            caricaAccettate();
            eseguiFiltro();
        } else {
            mostraErrore("Impossibile archiviare la tesi. Riprova.");
        }
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

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    private void mostraSuccesso(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}