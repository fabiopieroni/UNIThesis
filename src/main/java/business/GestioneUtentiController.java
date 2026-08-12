package business;

import model.Professore;
import model.Studente;
import model.Utente;

import java.util.List;

public interface GestioneUtentiController {
    List<Utente> getTuttiUtenti();
    List<Utente> getUtentiPerRuolo(String ruolo);
    Utente getDettagliUtente(int idUtente);
    boolean creaStudente(Studente studente);
    boolean creaProfessore(Professore professore);
    boolean aggiornaStudente(Studente studente);
    boolean aggiornaProfessore(Professore professore);
}