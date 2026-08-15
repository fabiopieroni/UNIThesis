package business;

import model.Professore;
import model.Ruolo;
import model.SegreteriaDidattica;
import model.Studente;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Test STRUTTURALI sul Singleton Sessione, nessuna dipendenza esterna.
class SessioneTest {

    @AfterEach
    void tearDown() {
        Sessione.getInstance().chiudiSessione();
    }

    @Test
        // getInstance() ritorna sempre la stessa istanza (comportamento Singleton)
    void getInstance_ritornaSempreLaStessaIstanza() {
        Sessione prima = Sessione.getInstance();
        Sessione seconda = Sessione.getInstance();

        assertSame(prima, seconda);
    }

    @Test
        // isLogged() false prima di avviare una sessione
    void isLogged_falseSenzaSessioneAttiva() {
        assertFalse(Sessione.getInstance().isLogged());
    }

    @Test
        // avviaSessione() rende isLogged() true e salva l'utente corretto
    void avviaSessione_rendeLoggedETrattengeUtente() {
        Studente studente = new Studente();
        studente.setEmail("test@test.it");
        studente.setRuolo(Ruolo.STUDENTE);

        Sessione.getInstance().avviaSessione(studente);

        assertTrue(Sessione.getInstance().isLogged());
        assertEquals("test@test.it", Sessione.getInstance().getUtenteCorrente().getEmail());
    }

    @Test
        // isStudente() true solo se l'utente loggato è uno Studente
    void isStudente_trueSoloConRuoloStudente() {
        Studente studente = new Studente();
        studente.setRuolo(Ruolo.STUDENTE);
        Sessione.getInstance().avviaSessione(studente);

        assertTrue(Sessione.getInstance().isStudente());
        assertFalse(Sessione.getInstance().isProfessore());
        assertFalse(Sessione.getInstance().isSegreteria());
    }

    @Test
        // isProfessore() true solo se l'utente loggato è un Professore
    void isProfessore_trueSoloConRuoloProfessore() {
        Professore prof = new Professore();
        prof.setRuolo(Ruolo.PROFESSORE);
        Sessione.getInstance().avviaSessione(prof);

        assertTrue(Sessione.getInstance().isProfessore());
        assertFalse(Sessione.getInstance().isStudente());
        assertFalse(Sessione.getInstance().isSegreteria());
    }

    @Test
        // isSegreteria() true solo se l'utente loggato è Segreteria
    void isSegreteria_trueSoloConRuoloSegreteria() {
        SegreteriaDidattica seg = new SegreteriaDidattica();
        seg.setRuolo(Ruolo.SEGRETERIA);
        Sessione.getInstance().avviaSessione(seg);

        assertTrue(Sessione.getInstance().isSegreteria());
        assertFalse(Sessione.getInstance().isStudente());
        assertFalse(Sessione.getInstance().isProfessore());
    }

    @Test
        // chiudiSessione() riporta isLogged() a false
    void chiudiSessione_rendeLoggedFalse() {
        Studente studente = new Studente();
        studente.setRuolo(Ruolo.STUDENTE);
        Sessione.getInstance().avviaSessione(studente);

        assertTrue(Sessione.getInstance().isLogged());

        Sessione.getInstance().chiudiSessione();

        assertFalse(Sessione.getInstance().isLogged());
    }
}