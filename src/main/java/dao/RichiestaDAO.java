package dao;

import model.RichiestaConDettagli;
import model.Richiesta;
import java.util.List;

public interface RichiestaDAO {
    boolean salvaRichiesta(Richiesta richiesta);
    List<Richiesta> trovaPerStudente(int idStudente);
    boolean aggiornaStato(int idRichiesta, String nuovoStato);

    // NUOVI
    List<RichiestaConDettagli> trovaPerProfessore(int idProfessore);
    boolean accettaRichiesta(int idRichiesta, int idProfessore);
    boolean rifiutaRichiesta(int idRichiesta);
}