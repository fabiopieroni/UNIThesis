package business.impl;

import business.AutenticazioneController;
import dao.UtenteDAO;
import dao.impl.UtenteDAOimpl;
import model.Utente;

public class AutenticazioneControllerimpl implements AutenticazioneController {
    private final UtenteDAO utenteDAO;

    public AutenticazioneControllerimpl() {
        this.utenteDAO = new UtenteDAOimpl();
    }

    @Override
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