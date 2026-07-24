package business;

import model.Utente;

public interface AutenticazioneController {
    Utente login(String email, String password);
}