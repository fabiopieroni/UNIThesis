package dao;

import dao.impl.RichiestaDAOimpl;
import dao.impl.TesiDAOimpl;
import model.Tesi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Test di INTEGRAZIONE: serve un database Postgres reale e raggiungibile.
class RichiestaDAOimplTest {

    private RichiestaDAOimpl richiestaDAO;
    private TesiDAOimpl tesiDAO;

    @BeforeEach
    void setUp() {
        richiestaDAO = new RichiestaDAOimpl();
        tesiDAO = new TesiDAOimpl();
    }

    @Test
        // TEST 5: haRichiestaAttiva() true se esiste richiesta IN_ATTESA o ACCETTATA
    void haRichiestaAttiva_true_seCandidaturaPendente() {
        // Mario Rossi ha già una candidatura ACCETTATA nel seed di base
        int idStudenteMario = trovaIdUtentePerEmail("mario.rossi@studenti.it");
        assertTrue(idStudenteMario > 0, "Assicurati che mario.rossi@studenti.it esista nel tuo DB di test");

        boolean attiva = richiestaDAO.haRichiestaAttiva(idStudenteMario);
        assertTrue(attiva);
    }

    @Test
        // TEST 6: accettaRichiesta() aggiorna correttamente in un'unica transazione
    void accettaRichiesta_aggiornaTuttoCorrettamente() {
        // Prendo una tesi PUBBLICATA e uno studente senza candidature attive per costruire
        // uno scenario pulito, poi verifico l'intera catena di effetti dell'accettazione.
        List<Tesi> pubblicate = tesiDAO.trovaDisponibili();
        assertFalse(pubblicate.isEmpty(), "Serve almeno una tesi PUBBLICATA per questo test");

        Tesi tesiTest = pubblicate.get(0);
        int idProfessore = tesiTest.getIdProfessore();

        int idStudenteGiulia = trovaIdUtentePerEmail("giulia.bianchi@studenti.it");
        assertTrue(idStudenteGiulia > 0, "Assicurati che giulia.bianchi@studenti.it esista nel tuo DB di test");

        // Se Giulia ha già una richiesta attiva su un'altra tesi, questo test va adattato:
        // qui assumiamo un DB di test pulito o comunque coerente con lo scenario.
        if (richiestaDAO.haRichiestaAttiva(idStudenteGiulia)) {
            System.out.println("ATTENZIONE: lo studente di test ha già una candidatura attiva, il test potrebbe non essere significativo.");
            return;
        }

        model.Richiesta nuovaRichiesta = new model.Richiesta(idStudenteGiulia, tesiTest.getIdTesi(), "Candidatura di test");
        boolean salvata = richiestaDAO.salvaRichiesta(nuovaRichiesta);
        assertTrue(salvata);

        boolean accettata = richiestaDAO.accettaRichiesta(nuovaRichiesta.getId(), idProfessore);
        assertTrue(accettata);

        Tesi tesiAggiornata = tesiDAO.getTesiById(tesiTest.getIdTesi());
        assertEquals("IN_CORSO", tesiAggiornata.getStato());

        // Ripristino lo stato per non sporcare i dati
        tesiDAO.aggiornaStato(tesiTest.getIdTesi(), "PUBBLICATA");
        richiestaDAO.aggiornaStato(nuovaRichiesta.getId(), "RIFIUTATA");
    }

    @Test
        // TEST 7: accettaRichiesta() fallisce se il professore ha già 5 tesisti attivi
    void accettaRichiesta_fallisceSeProfSaturo() {
        // Uso Paolo Neri (num_tesisti_attivi=0 di norma) e lo saturo temporaneamente a 5,
        // così il test è autosufficiente e non dipende da dati preesistenti nel DB.
        int idProfessore = trovaIdUtentePerEmail("paolo.neri@unifi.it");
        assertTrue(idProfessore > 0, "Assicurati che paolo.neri@unifi.it esista nel tuo DB di test");

        int valoreOriginale = leggiNumTesistiAttivi(idProfessore);

        try {
            impostaNumTesistiAttivi(idProfessore, 5);

            List<Tesi> tesiDiPaolo = tesiDAO.cercaPerProfessore(idProfessore);
            assertFalse(tesiDiPaolo.isEmpty(), "Serve almeno una tesi di Paolo Neri per questo test");

            int idStudenteTest = trovaIdUtentePerEmail("mario.rossi@studenti.it");
            model.Richiesta richiestaFinta = new model.Richiesta(idStudenteTest, tesiDiPaolo.get(0).getIdTesi(), "Test saturazione");
            richiestaDAO.salvaRichiesta(richiestaFinta);

            boolean ok = richiestaDAO.accettaRichiesta(richiestaFinta.getId(), idProfessore);
            assertFalse(ok, "L'accettazione deve fallire se il professore ha già 5 tesisti attivi");

            // Pulizia della richiesta finta creata per il test
            richiestaDAO.aggiornaStato(richiestaFinta.getId(), "RIFIUTATA");
        } finally {
            // Ripristino sempre il valore originale, anche se il test fallisce
            impostaNumTesistiAttivi(idProfessore, valoreOriginale);
        }
    }

    private int leggiNumTesistiAttivi(int idProfessore) {
        try (var conn = dao.DatabaseConnection.getConnection();
             var stmt = conn.prepareStatement("SELECT num_tesisti_attivi FROM professori WHERE id_utente = ?")) {
            stmt.setInt(1, idProfessore);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("num_tesisti_attivi") : 0;
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void impostaNumTesistiAttivi(int idProfessore, int valore) {
        try (var conn = dao.DatabaseConnection.getConnection();
             var stmt = conn.prepareStatement("UPDATE professori SET num_tesisti_attivi = ? WHERE id_utente = ?")) {
            stmt.setInt(1, valore);
            stmt.setInt(2, idProfessore);
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
        // TEST 8: trovaPerProfessore() ritorna solo richieste per tesi di quel professore
    void trovaPerProfessore_filtraCorrettamente() {
        int idProfessore = trovaIdUtentePerEmail("luigi.verdi@unifi.it");
        assertTrue(idProfessore > 0);

        var richieste = richiestaDAO.trovaPerProfessore(idProfessore);
        assertNotNull(richieste);

        List<Tesi> tesiDelProf = tesiDAO.cercaPerProfessore(idProfessore);
        List<Integer> titoliDelProf = tesiDelProf.stream().map(Tesi::getIdTesi).toList();

        // Ogni richiesta trovata deve fare riferimento a una tesi di questo professore.
        // Verifichiamo indirettamente controllando che il titolo della richiesta compaia
        // tra i titoli delle tesi del professore.
        for (var r : richieste) {
            boolean titoloTrovato = tesiDelProf.stream()
                    .anyMatch(t -> t.getTitolo().equals(r.getTitoloTesi()));
            assertTrue(titoloTrovato, "Trovata una richiesta con titolo non riconducibile alle tesi del professore");
        }
    }

    // Metodo di supporto: recupera l'id_utente data l'email, usando una query diretta
    private int trovaIdUtentePerEmail(String email) {
        dao.UtenteDAO utenteDAO = new dao.impl.UtenteDAOimpl();
        model.Utente u = utenteDAO.trovaPerEmail(email);
        return u != null ? u.getIdUtente() : -1;
    }
}