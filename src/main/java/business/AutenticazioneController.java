package business;

import dao.UtenteDAO;
import dao.impl.UtenteDAOimpl;
import model.Utente;

public class AutenticazioneController {
    private final UtenteDAO utenteDAO;

    public AutenticazioneController() {
        this.utenteDAO = new UtenteDAOimpl();
    }

    public AutenticazioneController(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    public Utente login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        Utente utente = utenteDAO.trovaPerEmail(email.trim());

        if (utente != null && utente.getPassword().equals(password)) {
            return utente;
        }

        return null;
    }
}