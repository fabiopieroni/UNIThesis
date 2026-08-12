package dao;

import model.Utente;

public interface UtenteDAO {
    Utente login(String email, String password);
    Utente trovaPerEmail(String email);
    Utente trovaPerId(int idUtente);
    boolean salva(Utente utente);
    boolean aggiornaProfilo(int idUtente, String nuovaEmail, String nuovaPassword);

}