package ui.controllers;

import business.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        errorLabel.setText("");

        boolean successo = authService.login(email, password);

        if (successo) {
            caricaSceltaRuolo(event);
        } else {
            errorLabel.setText("Email o password non corrette.");
        }
    }

    private void caricaSceltaRuolo(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            ui.NavigationUtil.cambiaScena(stage, "/fxml/SceltaRuolo.fxml", "Scelta ruolo");
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Errore nel caricamento della schermata successiva.");
        }
    }
}