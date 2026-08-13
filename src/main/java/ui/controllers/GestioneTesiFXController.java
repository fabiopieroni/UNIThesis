package ui.controllers;

import business.GestioneTesiController;
import business.Sessione;
import business.impl.GestioneTesiControllerimpl;
import dao.CorsoDiLaureaDAO;
import dao.impl.CorsoDiLaureaDAOimpl;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CorsoDiLaurea;
import model.Professore;
import model.Tesi;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GestioneTesiFXController {

  @FXML private TextField campoTitolo;
  @FXML private TextArea campoDescrizione;
  @FXML private ComboBox<String> comboCorsoLaurea;
  @FXML private Label labelErrore;

  @FXML private TableView<Tesi> tabellaTesi;
  @FXML private TableColumn<Tesi, String> colTitolo;
  @FXML private TableColumn<Tesi, String> colCorso;
  @FXML private TableColumn<Tesi, String> colStato;
  @FXML private TableColumn<Tesi, Void> colInfo;

  private final GestioneTesiController gestioneTesiController = new GestioneTesiControllerimpl();
  private final CorsoDiLaureaDAO corsoDiLaureaDAO = new CorsoDiLaureaDAOimpl();
  private Professore professore;

  @FXML
  public void initialize() {
    professore = (Professore) Sessione.getInstance().getUtenteCorrente();

    List<CorsoDiLaurea> corsi = corsoDiLaureaDAO.trovaTutti();
    comboCorsoLaurea.setItems(FXCollections.observableArrayList(
            corsi.stream().map(CorsoDiLaurea::getNome).collect(Collectors.toList())
    ));

    colTitolo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitolo()));
    colCorso.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCorsoLaurea()));
    colStato.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStato()));

    aggiungiColonnaInfo();

    tabellaTesi.setRowFactory(tv -> {
      TableRow<Tesi> row = new TableRow<>();
      row.setOnMouseClicked(event -> {
        if (!row.isEmpty() && event.getClickCount() == 2) {
          apriGestioneCandidature();
        }
      });
      return row;
    });

    caricaTabellaTesi();
  }

  private void caricaTabellaTesi() {
    List<Tesi> tesiProf = gestioneTesiController.getTesiByProfessore(professore.getIdUtente());
    tabellaTesi.setItems(FXCollections.observableArrayList(tesiProf));
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
    String nomeProfessore = professore.getNome() + " " + professore.getCognome();

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

  @FXML
  public void modificaTesiSelezionata() {
    labelErrore.setText("");
    Tesi selezionata = tabellaTesi.getSelectionModel().getSelectedItem();
    if (selezionata == null) {
      labelErrore.setText("Seleziona una tesi dalla tabella.");
      return;
    }
    if (!"BOZZA".equalsIgnoreCase(selezionata.getStato())) {
      labelErrore.setText("Puoi modificare solo tesi in stato BOZZA.");
      return;
    }

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModificaTesi.fxml"));
      Parent root = loader.load();

      ModificaTesiController controller = loader.getController();
      controller.inizializza(selezionata, this::caricaTabellaTesi);

      Stage popup = new Stage();
      popup.initModality(Modality.APPLICATION_MODAL);
      popup.setTitle("Modifica tesi");
      popup.setScene(new Scene(root));
      popup.showAndWait();
    } catch (IOException e) {
      e.printStackTrace();
      labelErrore.setText("Errore nell'apertura della finestra di modifica.");
    }
  }

  private void apriGestioneCandidature() {
    try {
      Stage stage = (Stage) tabellaTesi.getScene().getWindow();
      ui.NavigationUtil.cambiaScena(stage, "/fxml/GestioneCandidature.fxml", null);
    } catch (IOException e) {
      e.printStackTrace();
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
}