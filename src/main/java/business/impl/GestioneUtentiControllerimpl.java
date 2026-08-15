package business.impl;

import business.GestioneUtentiController;
import dao.UtenteDAO;
import dao.impl.UtenteDAOimpl;
import model.Professore;
import model.Ruolo;
import model.Studente;
import model.Utente;

import java.util.List;
import java.util.stream.Collectors;

public class GestioneUtentiControllerimpl implements GestioneUtentiController {

    private final UtenteDAO utenteDAO;

    public GestioneUtentiControllerimpl() {
        this.utenteDAO = new UtenteDAOimpl();
    }

    // Costruttore usato SOLO per i test, per iniettare un DAO finto (mock)
    public GestioneUtentiControllerimpl(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    @Override
    public List<Utente> getTuttiUtenti() {
        return utenteDAO.trovaTutti();
    }

    @Override
    public List<Utente> getUtentiPerRuolo(String ruolo) {
        return utenteDAO.trovaTutti().stream()
                .filter(u -> u.getRuolo() != null && u.getRuolo().name().equalsIgnoreCase(ruolo))
                .collect(Collectors.toList());
    }

    @Override
    public Utente getDettagliUtente(int idUtente) {
        return utenteDAO.trovaDettagliCompletiPerId(idUtente);
    }

    @Override
    public boolean creaStudente(Studente studente) {
        if (emailGiaEsistente(studente.getEmail())) return false;
        studente.setRuolo(Ruolo.STUDENTE);
        return utenteDAO.salvaStudente(studente);
    }

    @Override
    public boolean creaProfessore(Professore professore) {
        if (emailGiaEsistente(professore.getEmail())) return false;
        professore.setRuolo(Ruolo.PROFESSORE);
        return utenteDAO.salvaProfessore(professore);
    }

    @Override
    public boolean aggiornaStudente(Studente studente) {
        return utenteDAO.aggiornaStudente(studente);
    }

    @Override
    public boolean aggiornaProfessore(Professore professore) {
        return utenteDAO.aggiornaProfessore(professore);
    }

    private boolean emailGiaEsistente(String email) {
        return utenteDAO.trovaPerEmail(email) != null;
    }
}