package ui.controllers;

import business.GestioneRevisioniController;
import business.GestioneTesiController;
import business.Sessione;
import business.impl.GestioneRevisioniControllerimpl;
import business.impl.GestioneTesiControllerimpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.RevisioneCapitolo;
import model.Tesi;
import model.Utente;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestioneRevisioniProfFXController {

    @FXML private TableView<RevisioneCapitolo> tabellaRevisioni;
    @FXML private TableColumn<RevisioneCapitolo, String> colTesi;
    @FXML private TableColumn<RevisioneCapitolo, Integer> colNumCapitolo;
    @FXML private TableColumn<RevisioneCapitolo, String> colTitolo;
    @FXML private TableColumn<RevisioneCapitolo, String> colStato;
    @FXML private TableColumn<RevisioneCapitolo, Timestamp> colData;

    @FXML private TextArea txtNote;
    @FXML private ComboBox<String> comboStato;

    private final GestioneRevisioniController gestioneRevisioni = new GestioneRevisioniControllerimpl();
    private final GestioneTesiController gestioneTesi = new GestioneTesiControllerimpl();

    private final ObservableList<RevisioneCapitolo> listaRevisioni = FXCollections.observableArrayList();
    private final Map<Integer, String> titoliTesiPerId = new HashMap<>();

    @FXML
    public void initialize() {
        colTesi.setCellValueFactory(cellData ->
                new SimpleStringProperty(titoliTesiPerId.getOrDefault(cellData.getValue().getIdTesi(), "—")));
        colNumCapitolo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("numCapitolo"));
        colTitolo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("titoloCapitolo"));
        colStato.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("statoRevisione"));
        colData.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dataInvio"));

        comboStato.setItems(FXCollections.observableArrayList("APPROVATO", "RIFIUTATO", "DA_CORREGGERE"));

        tabellaRevisioni.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtNote.setText(newSel.getNoteProfessore() != null ? newSel.getNoteProfessore() : "");
                comboStato.setValue(newSel.getStatoRevisione());
            } else {
                txtNote.clear();
                comboStato.setValue(null);
            }
        });

        caricaRevisioni();
    }

    private void caricaRevisioni() {
        Utente utente = Sessione.getInstance().getUtenteCorrente();
        int idProfessore = utente.getIdUtente();

        List<Tesi> tesiDelProfessore = gestioneTesi.getTesiByProfessore(idProfessore);
        if (tesiDelProfessore == null) {
            tesiDelProfessore = List.of();
        }

        listaRevisioni.clear();
        titoliTesiPerId.clear();

        for (Tesi tesi : tesiDelProfessore) {
            titoliTesiPerId.put(tesi.getIdTesi(), tesi.getTitolo());
            List<RevisioneCapitolo> revisioni = gestioneRevisioni.getRevisioniPerTesi(tesi.getIdTesi());
            if (revisioni != null) {
                listaRevisioni.addAll(revisioni);
            }
        }

        tabellaRevisioni.setItems(listaRevisioni);
    }

    @FXML
    void salvaCorrezione(ActionEvent event) {
        RevisioneCapitolo selezionata = tabellaRevisioni.getSelectionModel().getSelectedItem();
        if (selezionata == null) {
            mostraErrore("Seleziona prima un capitolo dalla tabella.");
            return;
        }
        String nuovoStato = comboStato.getValue();
        if (nuovoStato == null) {
            mostraErrore("Seleziona uno stato (APPROVATO / RIFIUTATO / DA_CORREGGERE).");
            return;
        }

        boolean ok = gestioneRevisioni.aggiornaStatoRevisione(
                selezionata.getIdRevisione(), nuovoStato, txtNote.getText());

        if (ok) {
            mostraSuccesso("Correzione salvata correttamente.");
            caricaRevisioni();
        } else {
            mostraErrore("Salvataggio non riuscito. Riprova.");
        }
    }

    @FXML
    void apriPdf(ActionEvent event) {
        RevisioneCapitolo selezionata = tabellaRevisioni.getSelectionModel().getSelectedItem();
        if (selezionata == null || selezionata.getPercorsoPdf() == null) {
            mostraErrore("Seleziona prima un capitolo dalla tabella.");
            return;
        }
        File file = new File(selezionata.getPercorsoPdf());
        if (!file.exists()) {
            mostraErrore("File non trovato sul disco: " + selezionata.getPercorsoPdf());
            return;
        }
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            mostraErrore("Impossibile aprire il file: " + e.getMessage());
        }
    }

    @FXML
    void handleIndietro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SceltaRuolo.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tabellaRevisioni.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Scelta ruolo");
            stage.centerOnScreen();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    private void mostraSuccesso(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}