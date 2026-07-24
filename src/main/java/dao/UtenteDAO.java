package dao;

import model.Utente;

public interface UtenteDAO {
    Utente trovaPerEmail(String email);
    boolean salva(Utente utente);
}