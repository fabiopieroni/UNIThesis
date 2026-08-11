package ui.controllers;

import business.GestioneTesiController;
import business.Sessione;
import business.impl.GestioneTesiControllerimpl;
import dao.UtenteDAO;
import dao.impl.UtenteDAOimpl;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Studente;
import model.Tesi;
import model.Utente;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogoTesiFXController {

  @FXML private TextField campoRicerca;
  @FXML private ComboBox<String> comboFiltroCorso;

  @FXML private TableView<Tesi> tabellaCatalogo;
  @FXML private TableColumn<Tesi, String> colTitolo;
  @FXML private TableColumn<Tesi, String> colDescrizione;
  @FXML private TableColumn<Tesi, String> colCorso;
  @FXML private TableColumn<Tesi, Void> colInfo;

  private final GestioneTesiController gestioneTesiController = new GestioneTesiControllerimpl();
  private final UtenteDAO utenteDAO = new UtenteDAOimpl();

  @FXML
  public void initialize() {
    comboFiltroCorso.setItems(FXCollections.observableArrayList(
            "Tutti", "Ingegneria Informatica", "Ingegneria Gestionale", "Ingegneria Elettronica"
    ));
    comboFiltroCorso.setValue("Tutti");

    colTitolo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitolo()));
    colDescrizione.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescrizione()));
    colCorso.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCorsoLaurea()));

    aggiungiColonnaInfo();

    Utente utente = Sessione.getInstance().getUtenteCorrente();
    if (utente instanceof Studente studente && studente.getCorsoLaurea() != null) {
      comboFiltroCorso.setValue(studente.getCorsoLaurea());
    }

    tabellaCatalogo.setRowFactory(tv -> {
      TableRow<Tesi> row = new TableRow<>();
      row.setOnMouseClicked(event -> {
        if (!row.isEmpty() && event.getClickCount() == 2) {
          gestisciClickTesi(row.getItem());
        }
      });
      return row;
    });

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

  private void aggiungiColonnaInfo() {
    colInfo.setCellFactory(col -> new TableCell<>() {
      private final Button infoBtn = new Button("i");

      {
        infoBtn.setStyle(
                "-fx-background-radius: 50%; -fx-min-width: 26; -fx-min-height: 26; " +
                        "-fx-max-width: 26; -fx-max-height: 26; -fx-font-weight: bold;"
        );
        infoBtn.setOnAction(e -> {
          Tesi t = getTableView().getItems().get(getIndex());
          mostraInfoTesi(t);
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : infoBtn);
      }
    });
  }

  private void mostraInfoTesi(Tesi tesi) {
    Utente professore = utenteDAO.trovaPerId(tesi.getIdProfessore());
    String nomeProfessore = professore != null
            ? professore.getNome() + " " + professore.getCognome()
            : "Non disponibile";

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Dettagli tesi");
    alert.setHeaderText(tesi.getTitolo());
    alert.setContentText(
            "Descrizione: " + (tesi.getDescrizione() != null ? tesi.getDescrizione() : "-") +
                    "\n\nProfessore: " + nomeProfessore
    );
    alert.getDialogPane().setPrefWidth(400);
    alert.showAndWait();
  }

  private void gestisciClickTesi(Tesi tesi) {
    Utente utente = Sessione.getInstance().getUtenteCorrente();
    String fxml = (utente instanceof Studente)
            ? "/fxml/CandidaturaForm.fxml"
            : "/fxml/GestioneCandidature.fxml";

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
      Parent root = loader.load();

      if (utente instanceof Studente) {
        CandidaturaFormController controller = loader.getController();
        controller.preselezionaTesi(tesi);
      }

      Stage stage = (Stage) tabellaCatalogo.getScene().getWindow();
      stage.setScene(new Scene(root));
    } catch (IOException e) {
      e.printStackTrace();
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
}