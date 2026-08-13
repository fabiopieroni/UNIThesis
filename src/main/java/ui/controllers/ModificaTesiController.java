package ui.controllers;

import business.GestioneTesiController;
import business.impl.GestioneTesiControllerimpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Tesi;

public class ModificaTesiController {

    @FXML private TextField campoTitolo;
    @FXML private TextArea campoDescrizione;
    @FXML private Label labelErrore;

    private final GestioneTesiController gestioneTesiController = new GestioneTesiControllerimpl();
    private Tesi tesi;
    private Runnable onSalvato;

    public void inizializza(Tesi tesi, Runnable onSalvato) {
        this.tesi = tesi;
        this.onSalvato = onSalvato;
        campoTitolo.setText(tesi.getTitolo());
        campoDescrizione.setText(tesi.getDescrizione());
    }

    @FXML
    private void salvaModifiche(ActionEvent event) {
        String nuovoTitolo = campoTitolo.getText();
        String nuovaDescrizione = campoDescrizione.getText();

        if (nuovoTitolo == null || nuovoTitolo.isBlank()) {
            labelErrore.setText("Il titolo non può essere vuoto.");
            return;
        }

        tesi.setTitolo(nuovoTitolo);
        tesi.setDescrizione(nuovaDescrizione);

        boolean successo = gestioneTesiController.modificaTesi(tesi);
        if (successo) {
            if (onSalvato != null) {
                onSalvato.run();
            }
            chiudiFinestra(event);
        } else {
            labelErrore.setText("Errore durante il salvataggio.");
        }
    }

    @FXML
    private void annulla(ActionEvent event) {
        chiudiFinestra(event);
    }

    private void chiudiFinestra(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}