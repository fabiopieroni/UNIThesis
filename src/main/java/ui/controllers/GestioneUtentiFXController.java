package ui.controllers;

import business.GestioneUtentiController;
import business.impl.GestioneUtentiControllerimpl;
import dao.CorsoDiLaureaDAO;
import dao.impl.CorsoDiLaureaDAOimpl;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CorsoDiLaurea;
import model.Professore;
import model.Ruolo;
import model.Studente;
import model.Utente;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GestioneUtentiFXController {

    @FXML private ComboBox<String> comboFiltroRuolo;
    @FXML private TableView<Utente> tabellaUtenti;
    @FXML private TableColumn<Utente, String> colNome;
    @FXML private TableColumn<Utente, String> colCognome;
    @FXML private TableColumn<Utente, String> colEmail;
    @FXML private TableColumn<Utente, String> colRuolo;

    @FXML private ComboBox<String> comboTipoUtente;
    @FXML private TextField txtNome;
    @FXML private TextField txtCognome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPassword;

    @FXML private VBox boxCampiStudente;
    @FXML private TextField txtMatricola;
    @FXML private TextField txtCfuTotali;

    @FXML private VBox boxCampiProfessore;
    @FXML private TextField txtMatricolaDocente;

    @FXML private ComboBox<String> comboCorsoLaurea;

    @FXML private Label lblFormTitolo;
    @FXML private Button btnSalva;

    private final GestioneUtentiController gestioneUtenti = new GestioneUtentiControllerimpl();
    private final CorsoDiLaureaDAO corsoDiLaureaDAO = new CorsoDiLaureaDAOimpl();

    private Integer idUtenteInModifica = null;

    @FXML
    private void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCognome.setCellValueFactory(new PropertyValueFactory<>("cognome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRuolo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getRuolo() != null ? data.getValue().getRuolo().name() : "—"));

        comboFiltroRuolo.setItems(FXCollections.observableArrayList("Tutti", "STUDENTE", "PROFESSORE"));
        comboFiltroRuolo.setValue("Tutti");

        comboTipoUtente.setItems(FXCollections.observableArrayList("Studente", "Professore"));
        comboTipoUtente.setValue("Studente");
        comboTipoUtente.valueProperty().addListener((obs, oldV, newV) -> aggiornaCampiSpecifici());

        List<CorsoDiLaurea> corsi = corsoDiLaureaDAO.trovaTutti();
        comboCorsoLaurea.setItems(FXCollections.observableArrayList(
                corsi.stream().map(CorsoDiLaurea::getNome).collect(Collectors.toList())
        ));

        tabellaUtenti.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                caricaUtenteNelForm(newSel);
            }
        });

        aggiornaCampiSpecifici();
        eseguiFiltro();
    }

    private void aggiornaCampiSpecifici() {
        boolean isStudente = "Studente".equals(comboTipoUtente.getValue());
        boxCampiStudente.setVisible(isStudente);
        boxCampiStudente.setManaged(isStudente);
        boxCampiProfessore.setVisible(!isStudente);
        boxCampiProfessore.setManaged(!isStudente);
    }

    @FXML
    private void eseguiFiltro() {
        String filtro = comboFiltroRuolo.getValue();
        List<Utente> lista = "Tutti".equals(filtro)
                ? gestioneUtenti.getTuttiUtenti()
                : gestioneUtenti.getUtentiPerRuolo(filtro);
        tabellaUtenti.setItems(FXCollections.observableArrayList(lista));
    }

    private void caricaUtenteNelForm(Utente utenteBase) {
        if (utenteBase.getRuolo() == Ruolo.SEGRETERIA) {
            mostraErrore("Gli account di Segreteria non sono modificabili da questa schermata.");
            tabellaUtenti.getSelectionModel().clearSelection();
            return;
        }

        Utente completo = gestioneUtenti.getDettagliUtente(utenteBase.getIdUtente());
        if (completo == null) return;

        idUtenteInModifica = completo.getIdUtente();
        lblFormTitolo.setText("Modifica utente");
        btnSalva.setText("Salva Modifiche");

        txtNome.setText(completo.getNome());
        txtCognome.setText(completo.getCognome());
        txtEmail.setText(completo.getEmail());
        txtPassword.setText(completo.getPassword());

        comboTipoUtente.setDisable(true);

        if (completo instanceof Studente s) {
            comboTipoUtente.setValue("Studente");
            txtMatricola.setText(s.getMatricola());
            txtCfuTotali.setText(String.valueOf(s.getCfuTotali()));
            comboCorsoLaurea.setValue(s.getCorsoLaurea());
        } else if (completo instanceof Professore p) {
            comboTipoUtente.setValue("Professore");
            txtMatricolaDocente.setText(p.getMatricolaDocente());
            comboCorsoLaurea.setValue(p.getCorsoLaurea());
        }
        aggiornaCampiSpecifici();
    }

    @FXML
    private void nuovoUtente(ActionEvent event) {
        idUtenteInModifica = null;
        lblFormTitolo.setText("Crea nuovo utente");
        btnSalva.setText("Crea Utente");
        comboTipoUtente.setDisable(false);
        comboTipoUtente.setValue("Studente");

        txtNome.clear();
        txtCognome.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtMatricola.clear();
        txtCfuTotali.clear();
        txtMatricolaDocente.clear();
        comboCorsoLaurea.setValue(null);

        aggiornaCampiSpecifici();
        tabellaUtenti.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvaUtente(ActionEvent event) {
        String nome = txtNome.getText();
        String cognome = txtCognome.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String corsoLaurea = comboCorsoLaurea.getValue();

        if (nome == null || nome.isBlank() || cognome == null || cognome.isBlank()
                || email == null || email.isBlank() || corsoLaurea == null
                || password == null || password.isBlank()) {
            mostraErrore("Compila tutti i campi obbligatori (Nome, Cognome, Email, Password, Corso di Laurea).");
            return;
        }

        boolean isCreazione = (idUtenteInModifica == null);
        boolean isStudente = "Studente".equals(comboTipoUtente.getValue());
        boolean ok;

        if (isStudente) {
            Studente s = new Studente();
            s.setNome(nome);
            s.setCognome(cognome);
            s.setEmail(email);
            s.setPassword(password);
            s.setCorsoLaurea(corsoLaurea);
            s.setMatricola(txtMatricola.getText());
            try {
                s.setCfuTotali(txtCfuTotali.getText().isBlank() ? 0 : Integer.parseInt(txtCfuTotali.getText()));
            } catch (NumberFormatException e) {
                mostraErrore("CFU Totali deve essere un numero.");
                return;
            }

            if (isCreazione) {
                ok = gestioneUtenti.creaStudente(s);
            } else {
                s.setIdUtente(idUtenteInModifica);
                ok = gestioneUtenti.aggiornaStudente(s);
            }
        } else {
            Professore p = new Professore();
            p.setNome(nome);
            p.setCognome(cognome);
            p.setEmail(email);
            p.setPassword(password);
            p.setCorsoLaurea(corsoLaurea);
            p.setMatricolaDocente(txtMatricolaDocente.getText());

            if (isCreazione) {
                p.setNumTesistiAttivi(0);
                ok = gestioneUtenti.creaProfessore(p);
            } else {
                p.setIdUtente(idUtenteInModifica);
                Utente attuale = gestioneUtenti.getDettagliUtente(idUtenteInModifica);
                p.setNumTesistiAttivi(attuale instanceof Professore ap ? ap.getNumTesistiAttivi() : 0);
                ok = gestioneUtenti.aggiornaProfessore(p);
            }
        }

        if (ok) {
            mostraSuccesso(isCreazione ? "Utente creato correttamente." : "Utente modificato correttamente.");
            eseguiFiltro();
            nuovoUtente(null);
        } else {
            mostraErrore(isCreazione
                    ? "Creazione non riuscita. Controlla che l'email non sia già in uso."
                    : "Modifica non riuscita. Riprova.");
        }
    }

    @FXML
    private void handleIndietro(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            ui.NavigationUtil.cambiaScena(stage, "/fxml/SceltaRuolo.fxml", null);
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