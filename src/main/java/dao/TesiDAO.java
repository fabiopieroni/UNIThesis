package dao;

import model.Tesi;
import model.TesiConDettagli;
import java.util.List;

public interface TesiDAO {
  // Inserimento proposta tesi
  boolean salvaTesi(Tesi tesi);

  // Modifica proposta tesi
  boolean aggiornaTesi(Tesi tesi);

  boolean aggiornaStato(int idTesi, String nuovoStato);

  // Ricerca per parole chiave (titolo o descrizione)
  List<Tesi> cercaPerParolaChiave(String keyword);

  // Ricerca per ID Professore
  List<Tesi> cercaPerProfessore(int idProfessore);

  // Tesi disponibili per candidatura
  List<Tesi> trovaDisponibili();

  // Recupero tesi per ID
  Tesi getTesiById(int idTesi);

  // Metodi per la segreteria
  List<TesiConDettagli> trovaConDettagliPerStato(String stato);
  List<TesiConDettagli> trovaTutteConDettagliEscludendoBozza();
}