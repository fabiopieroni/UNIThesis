package business;

import business.impl.GestioneRevisioniControllerimpl;
import business.observer.Observer;
import dao.RevisioneCapitoloDAO;
import dao.TesiDAO;
import model.RevisioneCapitolo;
import model.Tesi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestioneRevisioniControllerimplTest {

    @Mock
    private RevisioneCapitoloDAO revisioneDAO;

    @Mock
    private TesiDAO tesiDAO;

    private GestioneRevisioniControllerimpl controller;

    @BeforeEach
    void setUp() {
        controller = new GestioneRevisioniControllerimpl(revisioneDAO, tesiDAO);
    }

    // ===== Test 8: tesi IN_CORSO → upload capitolo permesso =====

    @Test
    void inviaRevisione_tesiInCorso_permesso() {
        Tesi tesi = new Tesi(1, "Titolo", "descrizione", "Informatica", "IN_CORSO", 99);
        RevisioneCapitolo revisione = new RevisioneCapitolo(1, 1, "Capitolo 1", "/percorso/file.pdf");

        when(tesiDAO.getTesiById(1)).thenReturn(tesi);
        when(revisioneDAO.salva(revisione)).thenReturn(true);

        boolean risultato = controller.inviaRevisione(revisione);

        assertTrue(risultato);
        verify(revisioneDAO, times(1)).salva(revisione);
    }

    // ===== Test 9: tesi CONSEGNATA → upload capitolo bloccato =====

    @Test
    void inviaRevisione_tesiConsegnata_bloccato() {
        Tesi tesi = new Tesi(1, "Titolo", "descrizione", "Informatica", "CONSEGNATA", 99);
        RevisioneCapitolo revisione = new RevisioneCapitolo(1, 1, "Capitolo 1", "/percorso/file.pdf");

        when(tesiDAO.getTesiById(1)).thenReturn(tesi);

        boolean risultato = controller.inviaRevisione(revisione);

        assertFalse(risultato);
        verify(revisioneDAO, never()).salva(any());
    }

    // ===== Test 10: tesi ACCETTATA → correzione capitolo dal prof bloccata =====

    @Test
    void aggiornaStatoRevisione_tesiAccettata_bloccato() {
        RevisioneCapitolo capitolo = new RevisioneCapitolo(1, 1, "Capitolo 1", "/percorso/file.pdf");
        capitolo.setIdRevisione(10);
        Tesi tesi = new Tesi(1, "Titolo", "descrizione", "Informatica", "ACCETTATA", 99);

        when(revisioneDAO.getById(10)).thenReturn(capitolo);
        when(tesiDAO.getTesiById(1)).thenReturn(tesi);

        boolean risultato = controller.aggiornaStatoRevisione(10, "APPROVATO", "Ottimo lavoro");

        assertFalse(risultato);
        verify(revisioneDAO, never()).aggiornaStatoENote(anyInt(), anyString(), anyString());
    }

    // ===== Test 11: capitolo DA_CORREGGERE → "Correggi e Rinvia" abilitato solo in questo stato =====

    @Test
    void rinviaCorrezione_capitoloDaCorreggere_permesso() {
        RevisioneCapitolo capitolo = new RevisioneCapitolo(1, 1, "Capitolo 1", "/percorso/file.pdf");
        capitolo.setIdRevisione(10);
        capitolo.setStatoRevisione("DA_CORREGGERE");

        when(revisioneDAO.getById(10)).thenReturn(capitolo);
        when(revisioneDAO.rinviaCorrezione(eq(10), anyString(), any())).thenReturn(true);

        boolean risultato = controller.rinviaCorrezione(10, "capitolo_v2.pdf", new byte[]{1, 2, 3});

        assertTrue(risultato);
        verify(revisioneDAO, times(1)).rinviaCorrezione(eq(10), anyString(), any());
    }

    @Test
    void rinviaCorrezione_capitoloNonDaCorreggere_bloccato() {
        RevisioneCapitolo capitolo = new RevisioneCapitolo(1, 1, "Capitolo 1", "/percorso/file.pdf");
        capitolo.setIdRevisione(10);
        capitolo.setStatoRevisione("APPROVATO");

        when(revisioneDAO.getById(10)).thenReturn(capitolo);

        boolean risultato = controller.rinviaCorrezione(10, "capitolo_v2.pdf", new byte[]{1, 2, 3});

        assertFalse(risultato);
        verify(revisioneDAO, never()).rinviaCorrezione(anyInt(), anyString(), any());
    }

    // ===== Test 12: inviaRevisione() genera notifica per il prof proprietario della tesi =====

    @Test
    void inviaRevisione_generaNotificaPerProfessoreProprietario() {
        Tesi tesi = new Tesi(1, "Titolo", "descrizione", "Informatica", "IN_CORSO", 99);
        RevisioneCapitolo revisione = new RevisioneCapitolo(1, 3, "Capitolo 3", "/percorso/file.pdf");

        Observer observerFinto = mock(Observer.class);
        controller.aggiungiObserver(observerFinto);

        when(tesiDAO.getTesiById(1)).thenReturn(tesi);
        when(revisioneDAO.salva(revisione)).thenReturn(true);

        controller.inviaRevisione(revisione);

        verify(observerFinto, times(1)).update(anyString(), eq(99));
    }

    // ===== Test 13: il messaggio della notifica contiene numero capitolo e id tesi corretti =====

    @Test
    void inviaRevisione_messaggioNotificaContieneCapitoloETesiCorretti() {
        Tesi tesi = new Tesi(1, "Titolo", "descrizione", "Informatica", "IN_CORSO", 99);
        RevisioneCapitolo revisione = new RevisioneCapitolo(1, 3, "Capitolo 3", "/percorso/file.pdf");

        Observer observerFinto = mock(Observer.class);
        controller.aggiungiObserver(observerFinto);

        when(tesiDAO.getTesiById(1)).thenReturn(tesi);
        when(revisioneDAO.salva(revisione)).thenReturn(true);

        controller.inviaRevisione(revisione);

        ArgumentCaptor<String> messaggioCaptor = ArgumentCaptor.forClass(String.class);
        verify(observerFinto).update(messaggioCaptor.capture(), eq(99));

        String messaggio = messaggioCaptor.getValue();
        assertTrue(messaggio.contains("Capitolo 3"));
        assertTrue(messaggio.contains("tesi ID: 1"));
    }
}