package dao;

import model.RevisioneCapitolo;
import java.util.List;

public interface RevisioneCapitoloDAO {
    boolean salva(RevisioneCapitolo revisione);
    List<RevisioneCapitolo> findByTesi(int idTesi);
    boolean aggiornaStatoENote(int idRevisione, String stato, String note);
}