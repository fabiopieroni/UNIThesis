package business;

import model.RevisioneCapitolo;
import java.util.List;

public interface GestioneRevisioniController {
    List<RevisioneCapitolo> getRevisioniPerTesi(int idTesi);
    RevisioneCapitolo getById(int idRevisione);
    boolean inviaRevisione(RevisioneCapitolo revisione);
    boolean aggiornaStatoRevisione(int idRevisione, String nuovoStato, String note);
    boolean rinviaCorrezione(int idRevisione, String nomeFile, byte[] pdfData);
    byte[] getPdfData(int idRevisione);
}