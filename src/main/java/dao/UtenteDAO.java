package dao;

import model.Professore;
import model.Studente;
import model.Utente;

import java.util.List;

public interface UtenteDAO {
    Utente login(String email, String password);
    Utente trovaPerEmail(String email);
    Utente trovaPerId(int idUtente);
    boolean salva(Utente utente);
    boolean aggiornaProfilo(int idUtente, String nuovaEmail, String nuovaPassword);

    List<Utente> trovaTutti();
    Utente trovaDettagliCompletiPerId(int idUtente);
    boolean salvaStudente(Studente studente);
    boolean salvaProfessore(Professore professore);
    boolean aggiornaStudente(Studente studente);
    boolean aggiornaProfessore(Professore professore);
}