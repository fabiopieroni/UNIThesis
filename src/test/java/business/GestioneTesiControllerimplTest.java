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
  // (da completare)
}
