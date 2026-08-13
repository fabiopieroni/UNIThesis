package ui.controllers;

import business.GestioneTesiController;
import business.Sessione;
import business.impl.GestioneTesiControllerimpl;
import dao.RichiestaDAO;
import dao.impl.RichiestaDAOimpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Professore;
import model.RichiestaConDettagli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TesistiAttiviController {

    @FXML private TableView<RichiestaConDettagli> tabellaTesisti;
    @FXML private TableColumn<RichiestaConDettagli, String> colNome;
    @FXML private TableColumn<RichiestaConDettagli, String> colTesi;
    @FXML private TableColumn<RichiestaConDettagli, String> colStatoTesi;
    @FXML private TableColumn<RichiestaConDettagli, Void> colAzioni;

    private final RichiestaDAO richiestaDAO = new RichiestaDAOimpl();
    private final GestioneTesiController gestioneTesi = new GestioneTesiControllerimpl();

    private final Map<Integer, String> statoTesiPerId = new HashMap<>();

    @FXML
    private void initialize() {
        colNome.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getNomeStudente() + " " + data.getValue().getCognomeStudente()));
        colTesi.setCellValueFactory(new PropertyValueFactory<>("titoloTesi"));
        colStatoTesi.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        statoTesiPerId.getOrDefault(data.getValue().getIdTesi(), "—")));

        aggiungiColonnaAzioni();
        aggiornaVista();

        tabellaTesisti.setRowFactory(tv -> {
            TableRow<RichiestaConDettagli> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    apriRevisioniPerTesi(row.getItem().getIdTesi());
                }
            });
            return row;
        });
    }

    private void aggiornaVista() {
        Professore professore = (Professore) Sessione.getInstance().getUtenteCorrente();

        var tesisti = richiestaDAO.trovaPerProfessore(professore.getIdUtente()).stream()
                .filter(r -> "ACCETTATA".equals(r.getStato()))
                .collect(Collectors.toList());

        statoTesiPerId.clear();
        for (RichiestaConDettagli r : tesisti) {
            var tesi = gestioneTesi.getTesiById(r.getIdTesi());
            if (tesi != null) {
                statoTesiPerId.put(r.getIdTesi(), tesi.getStato());
            }
        }

        tabellaTesisti.getItems().setAll(tesisti);
        tabellaTesisti.refresh();
    }

    private void aggiungiColonnaAzioni() {
        colAzioni.setCellFactory(col -> new TableCell<>() {
            private final Button accettaBtn = new Button("Accetta Tesi");
            private final Button rifiutaBtn = new Button("Rifiuta");
            private final HBox box = new HBox(6, accettaBtn, rifiutaBtn);

            {
                accettaBtn.setOnAction(e -> {
                    RichiestaConDettagli r = getTableView().getItems().get(getIndex());
                    boolean ok = gestioneTesi.accettaTesiFinale(r.getIdTesi());
                    if (ok) {
                        aggiornaVista();
                    } else {
                        mostraAvviso("La tesi non è nello stato CONSEGNATA, oppure c'è stato un errore.");
                    }
                });
                rifiutaBtn.setOnAction(e -> {
                    RichiestaConDettagli r = getTableView().getItems().get(getIndex());
                    boolean ok = gestioneTesi.rifiutaTesiFinale(r.getIdTesi());
                    if (ok) {
                        aggiornaVista();
                    } else {
                        mostraAvviso("La tesi non è nello stato CONSEGNATA, oppure c'è stato un errore.");
                    }
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
                boolean consegnata = "CONSEGNATA".equals(statoTesiPerId.get(r.getIdTesi()));
                accettaBtn.setDisable(!consegnata);
                rifiutaBtn.setDisable(!consegnata);
                setGraphic(box);
            }
        });
    }

    private void mostraAvviso(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    private void apriRevisioniPerTesi(int idTesi) {
        try {
            Stage stage = (Stage) tabellaTesisti.getScene().getWindow();
            GestioneRevisioniProfFXController controller = ui.NavigationUtil.cambiaScena(
                    stage, "/fxml/GestioneRevisioniProf.fxml", "Gestione Revisioni");
            controller.initData(idTesi);
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