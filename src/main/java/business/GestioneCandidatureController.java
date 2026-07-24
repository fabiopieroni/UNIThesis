package business;

import model.Candidatura;
import java.util.List;

public interface GestioneCandidatureController {
    List<Candidatura> getCandidaturePerStudente(int idStudente);
    List<Candidatura> getCandidaturePerProfessore(int idProfessore);
    boolean inviaCandidatura(Candidatura candidatura);
    boolean aggiornaStatoCandidatura(int idCandidatura, String nuovoStato);
}