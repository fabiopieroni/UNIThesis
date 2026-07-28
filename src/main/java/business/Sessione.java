package business;

import model.Ruolo;
import model.Utente;

public class Sessione {
    private static Sessione instance;
    private Utente utenteCorrente;

    private Sessione() {}

    public static Sessione getInstance() {
        if (instance == null) {
            instance = new Sessione();
        }
        return instance;
    }

    public void avviaSessione(Utente utente) {
        this.utenteCorrente = utente;
    }

    public void chiudiSessione() {
        this.utenteCorrente = null;
    }

    public boolean isLogged() {
        return utenteCorrente != null;
    }

    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    // --- CONTROLLO RUOLI E PERMESSI ---

    public boolean isStudente() {
        return isLogged() && utenteCorrente.getRuolo() == Ruolo.STUDENTE;
    }

    public boolean isProfessore() {
        return isLogged() && utenteCorrente.getRuolo() == Ruolo.PROFESSORE;
    }

    public boolean isSegreteria() {
        return isLogged() && utenteCorrente.getRuolo() == Ruolo.SEGRETERIA;
    }
}