package business.impl;

import business.GestioneCandidatureController;
import model.Candidatura;

import java.util.List;

public class GestioneCandidatureControllerimpl implements GestioneCandidatureController {
    @Override
    public List<Candidatura> getCandidaturePerStudente(int idStudente) {
        return List.of();
    }

    @Override
    public List<Candidatura> getCandidaturePerProfessore(int idProfessore) {
        return List.of();
    }

    @Override
    public boolean inviaCandidatura(Candidatura candidatura) {
        return false;
    }

    @Override
    public boolean aggiornaStatoCandidatura(int idCandidatura, String nuovoStato) {
        return false;
    }
    // Scheletro base per le candidature
}