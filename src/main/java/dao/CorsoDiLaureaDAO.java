package dao;

import model.CorsoDiLaurea;
import java.util.List;

public interface CorsoDiLaureaDAO {
    List<CorsoDiLaurea> trovaTutti();
    boolean salva(CorsoDiLaurea corso);
}