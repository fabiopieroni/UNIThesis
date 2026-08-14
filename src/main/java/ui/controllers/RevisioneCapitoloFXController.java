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
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.List;
import business.GestioneTesiController;
import business.impl.GestioneTesiControllerimpl;
import model.Tesi;
import javafx.scene.control.Label;

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

    @FXML private Button btnCorreggiRinvia;

    @FXML private Label lblStatoTesi;
    @FXML private Button btnConsegnaTesi;

    private final GestioneTesiController gestioneTesi = new GestioneTesiControllerimpl();
    private Tesi tesiCorrente;
    private GestioneRevisioniController gestioneRevisioni;
    private int tesiIdCorrente = -1;

    private File fileSelezionato;

    private ObservableList<RevisioneCapitolo> listaRevisioni = FXCollections.observableArrayList();

    public RevisioneCapitoloFXController() {
        this.gestioneRevisioni = new GestioneRevisioniControllerimpl();
    }

    @FXML
    public void initialize() {
        colNumCapitolo.setCellValueFactory(new PropertyValueFactory<>("numCapitolo"));
        colTitolo.setCellValueFactory(new PropertyValueFactory<>("titoloCapitolo"));
        colStato.setCellValueFactory(new PropertyValueFactory<>("statoRevisione"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataInvio"));

        tabellaRevisioni.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && newSelection.getNoteProfessore() != null) {
                txtAreaNote.setText(newSelection.getNoteProfessore());
            } else {
                txtAreaNote.setText("Nessuna nota presente per questa revisione.");
            }
            btnCorreggiRinvia.setDisable(newSelection == null ||
                    !"DA_CORREGGERE".equals(newSelection.getStatoRevisione()));
        });
    }

    public void initData(int idTesi) {
        this.tesiIdCorrente = idTesi;
        this.tesiCorrente = gestioneTesi.getTesiById(idTesi);
        aggiornaStatoUI();
        caricaTabella();
    }

    private void aggiornaStatoUI() {
        if (tesiCorrente == null) return;

        lblStatoTesi.setText("Stato tesi: " + tesiCorrente.getStato());

        boolean modificabile = "IN_CORSO".equals(tesiCorrente.getStato());

        txtNumCapitolo.setDisable(!modificabile);
        txtTitoloCapitolo.setDisable(!modificabile);
        btnScegliPdf.setDisable(!modificabile);
        btnInvia.setDisable(!modificabile);
        btnConsegnaTesi.setDisable(!modificabile);
    }

    private void caricaTabella() {
        if (gestioneRevisioni != null && tesiIdCorrente != -1) {
            List<RevisioneCapitolo> revisioni = gestioneRevisioni.getRevisioniPerTesi(tesiIdCorrente);
            listaRevisioni.setAll(revisioni != null ? revisioni : List.of());
            tabellaRevisioni.setItems(listaRevisioni);
        }
    }

    @FXML
    void consegnaTesiDefinitiva(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma consegna");
        confirm.setHeaderText(null);
        confirm.setContentText("Una volta consegnata, la tesi non sarà più modificabile finché il professore non la valuta. Continuare?");

        if (confirm.showAndWait().filter(r -> r == javafx.scene.control.ButtonType.OK).isPresent()) {
            boolean ok = gestioneTesi.consegnaTesi(tesiIdCorrente);
            if (ok) {
                tesiCorrente = gestioneTesi.getTesiById(tesiIdCorrente);
                aggiornaStatoUI();
                mostraSuccesso("Tesi consegnata! In attesa di valutazione del professore.");
            } else {
                mostraErrore("Impossibile consegnare la tesi in questo momento.");
            }
        }
    }

    @FXML
    void scegliFilePdf(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona il PDF del Capitolo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File PDF", "*.pdf"));

        Stage stage = (Stage) btnScegliPdf.getScene().getWindow();
        File scelto = fileChooser.showOpenDialog(stage);

        if (scelto != null) {
            this.fileSelezionato = scelto;
            txtPercorsoPdf.setText(scelto.getName());
        }
    }

    @FXML
    void inviaRevisione(ActionEvent event) {
        if (txtNumCapitolo.getText().isEmpty() || txtTitoloCapitolo.getText().isEmpty() || fileSelezionato == null) {
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

        byte[] contenutoPdf;
        try {
            contenutoPdf = Files.readAllBytes(fileSelezionato.toPath());
        } catch (IOException e) {
            mostraErrore("Impossibile leggere il file selezionato: " + e.getMessage());
            return;
        }

        RevisioneCapitolo nuovaRevisione = new RevisioneCapitolo(
                tesiIdCorrente,
                numCapitolo,
                txtTitoloCapitolo.getText(),
                fileSelezionato.getName(),
                contenutoPdf
        );

        if (gestioneRevisioni != null) {
            boolean successo = gestioneRevisioni.inviaRevisione(nuovaRevisione);
            if (successo) {
                txtNumCapitolo.clear();
                txtTitoloCapitolo.clear();
                txtPercorsoPdf.clear();
                txtAreaNote.clear();
                fileSelezionato = null;
                caricaTabella();
                mostraSuccesso("Capitolo inviato in revisione con successo!");
            } else {
                mostraErrore("Errore durante l'invio del capitolo. Riprova.");
            }
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

    @FXML
    void handleIndietro(ActionEvent event) {
        try {
            Stage stage = (Stage) tabellaRevisioni.getScene().getWindow();
            ui.NavigationUtil.cambiaScena(stage, "/fxml/SceltaRuolo.fxml", "Scelta ruolo");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void correggiERinvia(ActionEvent event) {
        RevisioneCapitolo selezionata = tabellaRevisioni.getSelectionModel().getSelectedItem();
        if (selezionata == null) {
            mostraErrore("Seleziona prima un capitolo dalla tabella.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona il PDF corretto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File PDF", "*.pdf"));

        Stage stage = (Stage) btnCorreggiRinvia.getScene().getWindow();
        File fileCorretto = fileChooser.showOpenDialog(stage);

        if (fileCorretto != null) {
            byte[] contenutoPdf;
            try {
                contenutoPdf = Files.readAllBytes(fileCorretto.toPath());
            } catch (IOException e) {
                mostraErrore("Impossibile leggere il file selezionato: " + e.getMessage());
                return;
            }

            boolean successo = gestioneRevisioni.rinviaCorrezione(
                    selezionata.getIdRevisione(), fileCorretto.getName(), contenutoPdf);
            if (successo) {
                mostraSuccesso("Capitolo corretto e rinviato con successo!");
                caricaTabella();
            } else {
                mostraErrore("Errore durante il rinvio del capitolo. Riprova.");
            }
        }
    }
}