package business;

import dao.UtenteDAO;
import dao.impl.UtenteDAOimpl;
import model.Utente;

public class AuthService {
    private final UtenteDAO utenteDAO;

    public AuthService() {
        this.utenteDAO = new UtenteDAOimpl();
    }

    public boolean login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        Utente utente = utenteDAO.login(email.trim(), password.trim());
        if (utente != null) {
            Sessione.getInstance().avviaSessione(utente);
            return true;
        }
        return false;
    }

    public void logout() {
        Sessione.getInstance().chiudiSessione();
    }

    public boolean aggiornaProfilo(String nuovaEmail, String nuovaPassword) {
        if (!Sessione.getInstance().isLogged()) return false;

        Utente corrente = Sessione.getInstance().getUtenteCorrente();
        boolean esito = utenteDAO.aggiornaProfilo(corrente.getIdUtente(), nuovaEmail, nuovaPassword);

        if (esito) {
            corrente.setEmail(nuovaEmail);
            corrente.setPassword(nuovaPassword);
        }
        return esito;
    }
}