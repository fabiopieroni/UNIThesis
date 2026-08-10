package business.impl;

import business.GestioneTesiController;
import dao.TesiDAO;
import dao.impl.TesiDAOimpl;
import model.Tesi;

import java.util.List;

public class GestioneTesiControllerimpl implements GestioneTesiController {

  private final TesiDAO tesiDAO = new TesiDAOimpl();

  @Override
  public List<Tesi> getTesiDisponibili() {
    return tesiDAO.cercaPerParolaChiave("");
  }

  @Override
  public boolean creaTesi(Tesi tesi) {
    if (tesi.getTitolo() == null || tesi.getTitolo().isBlank()) return false;
    return tesiDAO.salvaTesi(tesi);
  }

  @Override
  public boolean modificaTesi(Tesi tesi) {
    return tesiDAO.aggiornaTesi(tesi);
  }

  @Override
  public boolean pubblicaTesi(Tesi tesi) {
    tesi.pubblica();
    return tesiDAO.aggiornaTesi(tesi);
  }

  @Override
  public List<Tesi> cercaTesi(String keyword) {
    return tesiDAO.cercaPerParolaChiave(keyword);
  }

  @Override
  public List<Tesi> getTesiByProfessore(int idProfessore) {
    return tesiDAO.cercaPerProfessore(idProfessore);
  }
}
