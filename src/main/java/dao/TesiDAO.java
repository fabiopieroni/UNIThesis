package dao;

import model.Tesi;
import java.util.List;

public interface TesiDAO {

  // Inserimento proposal tesi
  boolean salvaTesi(Tesi tesi);

  // Modifica proposal tesi
  boolean aggiornaTesi(Tesi tesi);

  // Ricerca per parole chiave (titolo o descrizione)
  List<Tesi> cercaPerParolaChiave(String keyword);

  // Ricerca per ID Professore
  List<Tesi> cercaPerProfessore(int idProfessore);
}
