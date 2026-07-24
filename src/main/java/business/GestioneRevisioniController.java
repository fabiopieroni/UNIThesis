package business;

import model.RevisioneCapitolo;
import java.util.List;

public interface GestioneRevisioniController {
    List<RevisioneCapitolo> getRevisioniPerTesi(int idTesi);
    boolean inviaRevisione(RevisioneCapitolo revisione);
    boolean aggiornaStatoRevisione(int idRevisione, String nuovoStato, String note);
}