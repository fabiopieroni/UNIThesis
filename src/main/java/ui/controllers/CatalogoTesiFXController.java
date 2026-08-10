package ui.controllers;

import business.GestioneTesiController;
import business.Sessione;
import business.impl.GestioneTesiControllerimpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Studente;
import model.Tesi;
import model.Utente;

import java.util.List;
import java.util.stream.Collectors;

public class CatalogoTesiFXController {

  @FXML private TextField campoRicerca;
  @FXML private ComboBox<String> comboFiltroCorso;

  @FXML private TableView<Tesi> tabellaCatalogo;
  @FXML private TableColumn<Tesi, String> colTitolo;
  @FXML private TableColumn<Tesi, String> colDescrizione;
  @FXML private TableColumn<Tesi, String> colCorso;

  private final GestioneTesiController gestioneTesiController = new GestioneTesiControllerimpl();

  @FXML
  public void initialize() {
    comboFiltroCorso.setItems(FXCollections.observableArrayList(
      "Tutti", "Ingegneria Informatica", "Ingegneria Gestionale", "Ingegneria Elettronica"
    ));
    comboFiltroCorso.setValue("Tutti");

    colTitolo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitolo()));
    colDescrizione.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescrizione()));
    colCorso.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCorsoLaurea()));

    Utente utente = Sessione.getInstance().getUtenteCorrente();
    if (utente instanceof Studente studente && studente.getCorsoLaurea() != null) {
      comboFiltroCorso.setValue(studente.getCorsoLaurea());
    }

    eseguiRicerca();
  }

  @FXML
  public void eseguiRicerca() {
    String keyword = campoRicerca.getText() == null ? "" : campoRicerca.getText();
    String corsoFiltro = comboFiltroCorso.getValue();

    List<Tesi> risultati = gestioneTesiController.cercaTesi(keyword);

    if (corsoFiltro != null && !corsoFiltro.equals("Tutti")) {
      risultati = risultati.stream()
        .filter(t -> corsoFiltro.equals(t.getCorsoLaurea()))
        .collect(Collectors.toList());
    }

    tabellaCatalogo.setItems(FXCollections.observableArrayList(risultati));
  }
}
