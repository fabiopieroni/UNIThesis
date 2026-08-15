package business;

import business.impl.GestioneTesiControllerimpl;
import business.state.BozzaState;
import business.state.PubblicataState;
import dao.TesiDAO;
import model.Tesi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestioneTesiControllerimplTest {

  @Mock
  private TesiDAO tesiDAO;

  private GestioneTesiControllerimpl controller;

  @BeforeEach
  void setUp() {
    controller = new GestioneTesiControllerimpl(tesiDAO);
  }

  // ===== SEZIONE MICHI: creazione / pubblicazione =====

  @Test
  void creaTesi_conTitoloVuoto_fallisce() {
    Tesi tesi = new Tesi("", "descrizione", "Informatica", "BOZZA", 1);

    boolean risultato = controller.creaTesi(tesi);

    assertFalse(risultato);
    verify(tesiDAO, never()).salvaTesi(any());
  }

  @Test
  void creaTesi_valida_creaTesiConStatoBozza() {
    Tesi tesi = new Tesi("Titolo valido", "descrizione", "Informatica", "BOZZA", 1);
    when(tesiDAO.salvaTesi(tesi)).thenReturn(true);

    boolean risultato = controller.creaTesi(tesi);

    assertTrue(risultato);
    assertTrue(tesi.getStatoOggetto() instanceof BozzaState);
    verify(tesiDAO, times(1)).salvaTesi(tesi);
  }

  @Test
  void pubblicaTesi_daBozza_passaAPubblicata() {
    Tesi tesi = new Tesi("Titolo", "descrizione", "Informatica", "BOZZA", 1);
    when(tesiDAO.aggiornaTesi(tesi)).thenReturn(true);

    boolean risultato = controller.pubblicaTesi(tesi);

    assertTrue(risultato);
    assertEquals("PUBBLICATA", tesi.getStato());
    assertTrue(tesi.getStatoOggetto() instanceof PubblicataState);
    verify(tesiDAO, times(1)).aggiornaTesi(tesi);
  }

  @Test
  void modificaTesi_nonInBozza_bloccata() {
    Tesi tesi = new Tesi("Titolo", "descrizione", "Informatica", "PUBBLICATA", 1);

    boolean risultato = controller.modificaTesi(tesi);

    assertFalse(risultato);
    verify(tesiDAO, never()).aggiornaTesi(any());
  }

  @Test
  void modificaTesi_inBozza_consentita() {
    Tesi tesi = new Tesi("Titolo", "descrizione", "Informatica", "BOZZA", 1);
    when(tesiDAO.aggiornaTesi(tesi)).thenReturn(true);

    boolean risultato = controller.modificaTesi(tesi);

    assertTrue(risultato);
    verify(tesiDAO, times(1)).aggiornaTesi(tesi);
  }

  @Test
  void getTesiByProfessore_ritornaSoloTesiDiQuelProfessore() {
    int idProfessore = 42;
    List<Tesi> tesiAttese = List.of(
      new Tesi("Tesi A", "desc", "Informatica", "BOZZA", idProfessore),
      new Tesi("Tesi B", "desc", "Informatica", "PUBBLICATA", idProfessore)
    );
    when(tesiDAO.cercaPerProfessore(idProfessore)).thenReturn(tesiAttese);

    List<Tesi> risultato = controller.getTesiByProfessore(idProfessore);

    assertEquals(2, risultato.size());
    verify(tesiDAO, times(1)).cercaPerProfessore(idProfessore);
  }

  // ===== SEZIONE TOMMY: consegna / archiviazione =====

  @Test
  void consegnaTesi_daInCorso_passaAConsegnata() {
    Tesi tesi = new Tesi(1, "Titolo", "descrizione", "Informatica", "IN_CORSO", 1);
    when(tesiDAO.getTesiById(1)).thenReturn(tesi);
    when(tesiDAO.aggiornaStato(1, "CONSEGNATA")).thenReturn(true);

    boolean risultato = controller.consegnaTesi(1);

    assertTrue(risultato);
    verify(tesiDAO, times(1)).aggiornaStato(1, "CONSEGNATA");
  }

  @Test
  void consegnaTesi_nonInCorso_fallisce() {
    Tesi tesi = new Tesi(1, "Titolo", "descrizione", "Informatica", "PUBBLICATA", 1);
    when(tesiDAO.getTesiById(1)).thenReturn(tesi);

    boolean risultato = controller.consegnaTesi(1);

    assertFalse(risultato);
    verify(tesiDAO, never()).aggiornaStato(anyInt(), anyString());
  }

  @Test
  void accettaTesiFinale_daConsegnata_passaAAccettata() {
    Tesi tesi = new Tesi(1, "Titolo", "descrizione", "Informatica", "CONSEGNATA", 1);
    when(tesiDAO.getTesiById(1)).thenReturn(tesi);
    when(tesiDAO.aggiornaStato(1, "ACCETTATA")).thenReturn(true);

    boolean risultato = controller.accettaTesiFinale(1);

    assertTrue(risultato);
    verify(tesiDAO, times(1)).aggiornaStato(1, "ACCETTATA");
  }

  @Test
  void accettaTesiFinale_nonConsegnata_fallisce() {
    Tesi tesi = new Tesi(1, "Titolo", "descrizione", "Informatica", "IN_CORSO", 1);
    when(tesiDAO.getTesiById(1)).thenReturn(tesi);

    boolean risultato = controller.accettaTesiFinale(1);

    assertFalse(risultato);
    verify(tesiDAO, never()).aggiornaStato(anyInt(), anyString());
  }

  @Test
  void rifiutaTesiFinale_daConsegnata_tornaAInCorso() {
    Tesi tesi = new Tesi(1, "Titolo", "descrizione", "Informatica", "CONSEGNATA", 1);
    when(tesiDAO.getTesiById(1)).thenReturn(tesi);
    when(tesiDAO.aggiornaStato(1, "IN_CORSO")).thenReturn(true);

    boolean risultato = controller.rifiutaTesiFinale(1);

    assertTrue(risultato);
    verify(tesiDAO, times(1)).aggiornaStato(1, "IN_CORSO");
  }

  @Test
  void archiviaTesi_daAccettata_passaAArchiviata() {
    Tesi tesi = new Tesi(1, "Titolo", "descrizione", "Informatica", "ACCETTATA", 1);
    when(tesiDAO.getTesiById(1)).thenReturn(tesi);
    when(tesiDAO.aggiornaStato(1, "ARCHIVIATA")).thenReturn(true);

    boolean risultato = controller.archiviaTesi(1);

    assertTrue(risultato);
    verify(tesiDAO, times(1)).aggiornaStato(1, "ARCHIVIATA");
  }

  @Test
  void archiviaTesi_nonAccettata_fallisce() {
    Tesi tesi = new Tesi(1, "Titolo", "descrizione", "Informatica", "CONSEGNATA", 1);
    when(tesiDAO.getTesiById(1)).thenReturn(tesi);

    boolean risultato = controller.archiviaTesi(1);

    assertFalse(risultato);
    verify(tesiDAO, never()).aggiornaStato(anyInt(), anyString());
  }
}
