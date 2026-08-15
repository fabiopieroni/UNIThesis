package dao;

import dao.impl.UtenteDAOimpl;
import model.Ruolo;
import model.Studente;
import model.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Test di INTEGRAZIONE: serve un database Postgres reale e raggiungibile.
class UtenteDAOimplTest {

    private UtenteDAOimpl utenteDAO;

    @BeforeEach
    void setUp() {
        utenteDAO = new UtenteDAOimpl();
    }

    @Test
        // TEST 12: salvaStudente() crea correttamente sia la riga in utenti che in studenti, con lo stesso id
    void salvaStudente_creaEntrambeLeRighe() {
        Studente s = new Studente();
        s.setNome("TestNomeJUnit");
        s.setCognome("TestCognomeJUnit");
        s.setEmail("test.junit." + System.currentTimeMillis() + "@example.com");
        s.setPassword("pass123");
        s.setCorsoLaurea("Informatica");
        s.setMatricola("TEST" + System.currentTimeMillis());
        s.setCfuTotali(42);
        s.setRuolo(Ruolo.STUDENTE);

        boolean ok = utenteDAO.salvaStudente(s);
        assertTrue(ok);
        assertTrue(s.getIdUtente() > 0, "L'id utente deve essere valorizzato dopo il salvataggio");

        Utente riletto = utenteDAO.trovaDettagliCompletiPerId(s.getIdUtente());
        assertNotNull(riletto);
        assertInstanceOf(Studente.class, riletto);
        assertEquals(42, ((Studente) riletto).getCfuTotali());
        assertEquals(s.getEmail(), riletto.getEmail());
    }

    @Test
        // TEST 13: trovaPerEmail() trova correttamente un utente esistente
    void trovaPerEmail_trovaUtenteEsistente() {
        Utente u = utenteDAO.trovaPerEmail("mario.rossi@studenti.it");
        assertNotNull(u, "Assicurati che mario.rossi@studenti.it esista nel tuo DB di test");
        assertEquals("Mario", u.getNome());
        assertEquals("Rossi", u.getCognome());
    }

    @Test
        // Bonus: trovaPerEmail() con email inesistente ritorna null, non lancia eccezione
    void trovaPerEmail_emailInesistente_ritornaNull() {
        Utente u = utenteDAO.trovaPerEmail("nessuno.esiste.davvero@nowhere.it");
        assertNull(u);
    }
}