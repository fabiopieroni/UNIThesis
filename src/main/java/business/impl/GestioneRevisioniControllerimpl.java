package business.impl;

import business.GestioneRevisioniController;
import business.observer.GestoreNotifiche;
import business.observer.Observer;
import business.observer.Subject;
import dao.RevisioneCapitoloDAO;
import dao.TesiDAO;
import dao.impl.RevisioneCapitoloDAOimpl;
import dao.impl.TesiDAOimpl;
import model.RevisioneCapitolo;
import model.Tesi;

import java.util.ArrayList;
import java.util.List;

public class GestioneRevisioniControllerimpl implements GestioneRevisioniController, Subject {

    private final List<Observer> observers = new ArrayList<>();
    private final RevisioneCapitoloDAO revisioneDAO;
    private final TesiDAO tesiDAO;

    public GestioneRevisioniControllerimpl() {
        this(new RevisioneCapitoloDAOimpl(), new TesiDAOimpl());
        aggiungiObserver(new GestoreNotifiche());
    }

    public GestioneRevisioniControllerimpl(RevisioneCapitoloDAO revisioneDAO, TesiDAO tesiDAO) {
        this.revisioneDAO = revisioneDAO;
        this.tesiDAO = tesiDAO;
    }

    @Override
    public void aggiungiObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void rimuoviObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notificaObservers(String messaggio, int idDestinatario) {
        for (Observer obs : observers) {
            obs.update(messaggio, idDestinatario);
        }
    }

    @Override
    public boolean inviaRevisione(RevisioneCapitolo revisione) {
        Tesi tesi = tesiDAO.getTesiById(revisione.getIdTesi());
        if (tesi == null || !"IN_CORSO".equals(tesi.getStato())) {
            return false;
        }

        boolean successo = revisioneDAO.salva(revisione);

        if (successo) {
            int idProfessore = tesi.getIdProfessore();
            String messaggio = "Nuova revisione caricata per il Capitolo " + revisione.getNumCapitolo()
                    + " della tesi ID: " + tesi.getIdTesi();

            notificaObservers(messaggio, idProfessore);
        }
        return successo;
    }

    @Override
    public List<RevisioneCapitolo> getRevisioniPerTesi(int idTesi) {
        return revisioneDAO.findByTesi(idTesi);
    }

    @Override
    public RevisioneCapitolo getById(int idRevisione) {
        return revisioneDAO.getById(idRevisione);
    }

    @Override
    public boolean aggiornaStatoRevisione(int idRevisione, String nuovoStato, String note) {
        RevisioneCapitolo capitolo = revisioneDAO.getById(idRevisione);
        if (capitolo == null) {
            return false;
        }
        Tesi tesi = tesiDAO.getTesiById(capitolo.getIdTesi());
        if (tesi != null && "ACCETTATA".equals(tesi.getStato())) {
            return false;
        }
        return revisioneDAO.aggiornaStatoENote(idRevisione, nuovoStato, note);
    }

    @Override
    public boolean rinviaCorrezione(int idRevisione, String nomeFile, byte[] pdfData) {
        RevisioneCapitolo capitolo = revisioneDAO.getById(idRevisione);
        if (capitolo == null || !"DA_CORREGGERE".equals(capitolo.getStatoRevisione())) {
            return false;
        }
        return revisioneDAO.rinviaCorrezione(idRevisione, nomeFile, pdfData);
    }

    @Override
    public byte[] getPdfData(int idRevisione) {
        return revisioneDAO.getPdfData(idRevisione);
    }
}