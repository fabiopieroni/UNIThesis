package dao;

import model.Notifica;
import java.util.List;

public interface NotificaDAO {
    boolean salva(Notifica notifica);
    List<Notifica> findByUtente(int idUtente);
    boolean segnaComeLetta(int idNotifica);
}