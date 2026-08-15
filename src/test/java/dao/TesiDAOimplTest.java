package dao;

import dao.impl.TesiDAOimpl;
import model.Tesi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Test di INTEGRAZIONE: serve un database Postgres reale e raggiungibile
class TesiDAOimplTest {

    private TesiDAOimpl tesiDAO;

    @BeforeEach
    void setUp() {
        tesiDAO = new TesiDAOimpl();
    }

    @Test
        // TEST 1: trovaDisponibili() ritorna solo tesi con stato PUBBLICATA
    void trovaDisponibili_ritornaSoloPubblicate() {
        List<Tesi> disponibili = tesiDAO.trovaDisponibili();

        assertFalse(disponibili.isEmpty(), "Ci si aspetta almeno una tesi PUBBLICATA nel DB di test");
        for (Tesi t : disponibili) {
            assertEquals("PUBBLICATA", t.getStato(),
                    "Trovata una tesi non PUBBLICATA nel catalogo: " + t.getTitolo());
        }
    }

    @Test
        // TEST 2: aggiornaStato() modifica correttamente lo stato, verificato rileggendo la tesi
    void aggiornaStato_modificaStatoCorrettamente() {
        // Prendo una tesi qualsiasi già esistente per testare, poi la riporto allo stato originale
        List<Tesi> tuttePubblicate = tesiDAO.trovaDisponibili();
        assertFalse(tuttePubblicate.isEmpty(), "Serve almeno una tesi nel DB per questo test");

        int idTesiTest = tuttePubblicate.get(0).getIdTesi();
        String statoOriginale = tuttePubblicate.get(0).getStato();

        boolean ok = tesiDAO.aggiornaStato(idTesiTest, "IN_CORSO");
        assertTrue(ok);

        Tesi rilettaDopoUpdate = tesiDAO.getTesiById(idTesiTest);
        assertEquals("IN_CORSO", rilettaDopoUpdate.getStato());

        // Ripristino lo stato originale per non sporcare i dati di test
        tesiDAO.aggiornaStato(idTesiTest, statoOriginale);
    }

    @Test
        // TEST 3: cercaPerParolaChiave() filtra correttamente su PUBBLICATA
    void cercaPerParolaChiave_filtraSoloPubblicate() {
        List<Tesi> risultati = tesiDAO.cercaPerParolaChiave("a"); // parola generica che matcha molti titoli
        for (Tesi t : risultati) {
            assertEquals("PUBBLICATA", t.getStato(),
                    "cercaPerParolaChiave ha restituito una tesi non PUBBLICATA: " + t.getTitolo());
        }
    }

    @Test
        // TEST 4: getTesiById() con id inesistente ritorna null, non lancia eccezione
    void getTesiById_idInesistente_ritornaNull() {
        Tesi tesi = tesiDAO.getTesiById(999999);
        assertNull(tesi);
    }
}