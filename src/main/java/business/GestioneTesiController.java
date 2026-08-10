package business;

import model.Tesi;
import java.util.List;

public interface GestioneTesiController {
    List<Tesi> getTesiDisponibili();
    boolean creaTesi(Tesi tesi);
    boolean modificaTesi(Tesi tesi);

    boolean pubblicaTesi(Tesi tesi);
    List<Tesi> cercaTesi(String keyword);
    List<Tesi> getTesiByProfessore(int idProfessore);
}
