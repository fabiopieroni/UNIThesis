package business;

import model.Tesi;
import java.util.List;

public interface GestioneTesiController {
    List<Tesi> getTesiDisponibili();
    boolean creaTesi(Tesi tesi);
    boolean modificaTesi(Tesi tesi);
}