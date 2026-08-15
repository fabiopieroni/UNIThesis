package business;

import business.impl.GestioneUtentiControllerimpl;
import dao.UtenteDAO;
import model.Professore;
import model.Ruolo;
import model.Studente;
import model.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Test FUNZIONALI (Black Box) con Mockito: il DAO è "finto", nessun database reale necessario.
@ExtendWith(MockitoExtension.class)
class GestioneUtentiControllerimplTest {

    @Mock
    private UtenteDAO utenteDAOMock;

    private GestioneUtentiControllerimpl gestioneUtenti;

    @BeforeEach
    void setUp() {
        gestioneUtenti = new GestioneUtentiControllerimpl(utenteDAOMock);
    }

    @Test
        // TEST 14: creazione studente con email già esistente fallisce
    void creaStudente_emailEsistente_fallisce() {
        Studente s = new Studente();
        s.setEmail("gia.esistente@test.it");

        when(utenteDAOMock.trovaPerEmail("gia.esistente@test.it")).thenReturn(new Utente());

        boolean ok = gestioneUtenti.creaStudente(s);

        assertFalse(ok);
        verify(utenteDAOMock, never()).salvaStudente(any());
    }

    @Test
        // TEST 15: creazione studente con email nuova va a buon fine
    void creaStudente_emailNuova_riesce() {
        Studente s = new Studente();
        s.setEmail("nuovo@test.it");

        when(utenteDAOMock.trovaPerEmail("nuovo@test.it")).thenReturn(null);
        when(utenteDAOMock.salvaStudente(s)).thenReturn(true);

        boolean ok = gestioneUtenti.creaStudente(s);

        assertTrue(ok);
        verify(utenteDAOMock).salvaStudente(s);
    }

    @Test
        // TEST 16: creazione professore ha num_tesisti_attivi = 0 di default
    void creaProfessore_numTesistiZero() {
        Professore p = new Professore();
        p.setEmail("nuovoprof@test.it");

        when(utenteDAOMock.trovaPerEmail("nuovoprof@test.it")).thenReturn(null);
        when(utenteDAOMock.salvaProfessore(p)).thenReturn(true);

        gestioneUtenti.creaProfessore(p);

        assertEquals(0, p.getNumTesistiAttivi());
    }

    @Test
        // TEST 17: getUtentiPerRuolo() filtra correttamente
    void getUtentiPerRuolo_filtraCorrettamente() {
        Studente studente = new Studente();
        studente.setRuolo(Ruolo.STUDENTE);
        Professore prof = new Professore();
        prof.setRuolo(Ruolo.PROFESSORE);

        when(utenteDAOMock.trovaTutti()).thenReturn(List.of(studente, prof));

        List<Utente> risultato = gestioneUtenti.getUtentiPerRuolo("STUDENTE");

        assertEquals(1, risultato.size());
        assertEquals(Ruolo.STUDENTE, risultato.get(0).getRuolo());
    }
}