package dao;

import model.Richiesta;
import java.util.List;

public interface RichiestaDAO {
    boolean salvaRichiesta(Richiesta richiesta);
    List<Richiesta> trovaPerStudente(int idStudente);
    boolean aggiornaStato(int idRichiesta, String nuovoStato);
}