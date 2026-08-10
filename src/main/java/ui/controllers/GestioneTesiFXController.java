package ui.controllers;

import business.GestioneTesiController;
import business.Sessione;
import business.impl.GestioneTesiControllerimpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Professore;
import model.Tesi;

import java.util.List;

public class GestioneTesiFXController {

  @FXML private TextField campoTitolo;
  @FXML private TextArea campoDescrizione;
  @FXML private ComboBox<String> comboCorsoLaurea;
  @FXML private Label labelErrore;

  @FXML private TableView<Tesi> tabellaTesi;
  @FXML private TableColumn<Tesi, String> colTitolo;
  @FXML private TableColumn<Tesi, String> colCorso;
  @FXML private TableColumn<Tesi, String> colStato;

  private final GestioneTesiController gestioneTesiController = new GestioneTesiControllerimpl();
  private Professore professore;

  @FXML
  public void initialize() {
    professore = (Professore) Sessione.getInstance().getUtenteCorrente();

    comboCorsoLaurea.setItems(FXCollections.observableArrayList(
      "Ingegneria Informatica", "Ingegneria Gestionale", "Ingegneria Elettronica"
    ));

    colTitolo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitolo()));
    colCorso.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCorsoLaurea()));
    colStato.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStato()));

    caricaTabellaTesi();
  }

  private void caricaTabellaTesi() {
    List<Tesi> tesiProf = gestioneTesiController.getTesiByProfessore(professore.getIdUtente());
    tabellaTesi.setItems(FXCollections.observableArrayList(tesiProf));
  }

  @FXML
  public void creaTesi() {
    labelErrore.setText("");
    String titolo = campoTitolo.getText();
    String descrizione = campoDescrizione.getText();
    String corso = comboCorsoLaurea.getValue();

    if (titolo == null || titolo.isBlank() || corso == null) {
      labelErrore.setText("Titolo e corso di laurea sono obbligatori.");
      return;
    }

    Tesi nuovaTesi = new Tesi(titolo, descrizione, corso, "BOZZA", professore.getIdUtente());
    boolean successo = gestioneTesiController.creaTesi(nuovaTesi);

    if (successo) {
      campoTitolo.clear();
      campoDescrizione.clear();
      comboCorsoLaurea.setValue(null);
      caricaTabellaTesi();
    } else {
      labelErrore.setText("Errore durante il salvataggio della tesi.");
    }
  }

  @FXML
  public void pubblicaTesiSelezionata() {
    Tesi selezionata = tabellaTesi.getSelectionModel().getSelectedItem();
    if (selezionata == null) {
      labelErrore.setText("Seleziona una tesi dalla tabella.");
      return;
    }
    boolean successo = gestioneTesiController.pubblicaTesi(selezionata);
    if (successo) {
      caricaTabellaTesi();
    } else {
      labelErrore.setText("Impossibile pubblicare questa tesi.");
    }
  }
}
