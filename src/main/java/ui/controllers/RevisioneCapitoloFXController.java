package ui.controllers;
import business.GestioneRevisioniController;
import business.impl.GestioneRevisioniControllerimpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.RevisioneCapitolo;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

public class RevisioneCapitoloFXController {

    @FXML private TableView<RevisioneCapitolo> tabellaRevisioni;
    @FXML private TableColumn<RevisioneCapitolo, Integer> colNumCapitolo;
    @FXML private TableColumn<RevisioneCapitolo, String> colTitolo;
    @FXML private TableColumn<RevisioneCapitolo, String> colStato;
    @FXML private TableColumn<RevisioneCapitolo, Timestamp> colData;

    @FXML private TextArea txtAreaNote;

    @FXML private TextField txtNumCapitolo;
    @FXML private TextField txtTitoloCapitolo;
    @FXML private TextField txtPercorsoPdf;

    @FXML private Button btnScegliPdf;
    @FXML private Button btnInvia;

    // Riferimento al livello di business
    private GestioneRevisioniController gestioneRevisioni;
    private int tesiIdCorrente = -1; // Verrà impostato dalla schermata precedente

    private ObservableList<RevisioneCapitolo> listaRevisioni = FXCollections.observableArrayList();

    public RevisioneCapitoloFXController() {
        this.gestioneRevisioni = new GestioneRevisioniControllerimpl();
    }

    @FXML
    public void initialize() {
        // Setup delle colonne della tabella (i nomi devono coincidere con le variabili dell'entità model)
        colNumCapitolo.setCellValueFactory(new PropertyValueFactory<>("numCapitolo"));
        colTitolo.setCellValueFactory(new PropertyValueFactory<>("titoloCapitolo"));
        colStato.setCellValueFactory(new PropertyValueFactory<>("statoRevisione"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataInvio"));

        // Listener per mostrare le note quando si clicca su una riga della tabella
        tabellaRevisioni.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && newSelection.getNoteProfessore() != null) {
                txtAreaNote.setText(newSelection.getNoteProfessore());
            } else {
                txtAreaNote.setText("Nessuna nota presente per questa revisione.");
            }
        });
    }

    /**
     * Metodo da chiamare quando si apre questa schermata dalla Dashboard/Lista Tesi
     * per passargli l'ID della Tesi di cui vogliamo vedere le revisioni.
     */
    public void initData(int idTesi) {
        this.tesiIdCorrente = idTesi;
        caricaTabella();
    }

    private void caricaTabella() {
        if (gestioneRevisioni != null && tesiIdCorrente != -1) {
            List<RevisioneCapitolo> revisioni = gestioneRevisioni.getRevisioniPerTesi(tesiIdCorrente);
            listaRevisioni.setAll(revisioni);
            tabellaRevisioni.setItems(listaRevisioni);
        }
    }

    @FXML
    void scegliFilePdf(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona il PDF del Capitolo");
        // Filtro per far vedere solo i file PDF
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File PDF", "*.pdf"));

        // Apre la finestra di selezione partendo dal bottone
        Stage stage = (Stage) btnScegliPdf.getScene().getWindow();
        File fileSelezionato = fileChooser.showOpenDialog(stage);

        if (fileSelezionato != null) {
            txtPercorsoPdf.setText(fileSelezionato.getAbsolutePath());
        }
    }

    @FXML
    void inviaRevisione(ActionEvent event) {
        // Controllo validità campi
        if (txtNumCapitolo.getText().isEmpty() || txtTitoloCapitolo.getText().isEmpty() || txtPercorsoPdf.getText().isEmpty()) {
            mostraErrore("Compila tutti i campi e seleziona un file PDF prima di inviare.");
            return;
        }

        int numCapitolo;
        try {
            numCapitolo = Integer.parseInt(txtNumCapitolo.getText());
        } catch (NumberFormatException e) {
            mostraErrore("Il numero del capitolo deve essere un numero valido.");
            return;
        }

        if (tesiIdCorrente == -1) {
            mostraErrore("Errore interno: ID Tesi non impostato.");
            return;
        }

        // Creazione dell'entità dal costruttore richiesto
        RevisioneCapitolo nuovaRevisione = new RevisioneCapitolo(
                tesiIdCorrente,
                numCapitolo,
                txtTitoloCapitolo.getText(),
                txtPercorsoPdf.getText()
        );
        // Il costruttore imposta già "IN_REVISIONE" in automatico

        // Chiamata al business
        if (gestioneRevisioni != null) {
            boolean successo = gestioneRevisioni.inviaRevisione(nuovaRevisione);
            if (successo) {
                // Pulisce i campi e ricarica la tabella
                txtNumCapitolo.clear();
                txtTitoloCapitolo.clear();
                txtPercorsoPdf.clear();
                txtAreaNote.clear();
                caricaTabella();
                mostraSuccesso("Capitolo inviato in revisione con successo!");
            } else {
                mostraErrore("Errore durante l'invio del capitolo. Riprova.");
            }
        }
    }

    // Metodi di utilità per i popup grafici
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