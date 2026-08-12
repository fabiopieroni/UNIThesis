package business.impl;

import business.GestioneTesiController;
import dao.TesiDAO;
import dao.impl.TesiDAOimpl;
import model.Tesi;
import model.TesiConDettagli;

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
  public Tesi getTesiById(int idTesi) {
    return tesiDAO.getTesiById(idTesi);
  }

  @Override
  public List<Tesi> cercaTesi(String keyword) {
    return tesiDAO.cercaPerParolaChiave(keyword);
  }

  @Override
  public List<Tesi> getTesiByProfessore(int idProfessore) {
    return tesiDAO.cercaPerProfessore(idProfessore);
  }

  @Override
  public boolean consegnaTesi(int idTesi) {
    Tesi tesi = tesiDAO.getTesiById(idTesi);
    if (tesi == null || !"IN_CORSO".equals(tesi.getStato())) {
      return false;
    }
    return tesiDAO.aggiornaStato(idTesi, "CONSEGNATA");
  }

  @Override
  public boolean accettaTesiFinale(int idTesi) {
    Tesi tesi = tesiDAO.getTesiById(idTesi);
    if (tesi == null || !"CONSEGNATA".equals(tesi.getStato())) {
      return false;
    }
    return tesiDAO.aggiornaStato(idTesi, "ACCETTATA");
  }

  @Override
  public boolean rifiutaTesiFinale(int idTesi) {
    Tesi tesi = tesiDAO.getTesiById(idTesi);
    if (tesi == null || !"CONSEGNATA".equals(tesi.getStato())) {
      return false;
    }
    return tesiDAO.aggiornaStato(idTesi, "IN_CORSO");
  }

  @Override
  public List<TesiConDettagli> getTesiAccettate() {
    return tesiDAO.trovaConDettagliPerStato("ACCETTATA");
  }

  @Override
  public List<TesiConDettagli> getTesiPerVistaSegreteria(String filtroStato) {
    if (filtroStato == null || filtroStato.isBlank()) {
      return tesiDAO.trovaTutteConDettagliEscludendoBozza();
    }
    return tesiDAO.trovaConDettagliPerStato(filtroStato);
  }

  @Override
  public boolean archiviaTesi(int idTesi) {
    Tesi tesi = tesiDAO.getTesiById(idTesi);
    if (tesi == null || !"ACCETTATA".equals(tesi.getStato())) {
      return false;
    }
    return tesiDAO.aggiornaStato(idTesi, "ARCHIVIATA");
  }

}


