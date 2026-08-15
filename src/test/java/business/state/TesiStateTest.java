package business.state;

import business.exception.TransizioneNonValidaException;
import model.Tesi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TesiStateTest {

  // ================================
  // STATO: BOZZA
  // ================================

  @Test
  void bozza_pubblica_transizionaInPubblicata() {
    Tesi tesi = new Tesi();
    tesi.setStatoOggetto(new BozzaState());

    tesi.pubblica();

    assertEquals("PUBBLICATA", tesi.getStato());
    assertTrue(tesi.getStatoOggetto() instanceof PubblicataState);
  }

  @Test
  void bozza_assegna_lanciaEccezione() {
    Tesi tesi = new Tesi();
    tesi.setStatoOggetto(new BozzaState());

    assertThrows(TransizioneNonValidaException.class, tesi::assegna);
  }

  // ================================
  // STATO: IN_CORSO
  // ================================

  @Test
  void inCorso_pubblica_transizionaInPubblicata() {
    Tesi tesi = new Tesi();
    tesi.setStatoOggetto(new InCorsoState());

    tesi.pubblica();

    assertEquals("PUBBLICATA", tesi.getStato());
    assertTrue(tesi.getStatoOggetto() instanceof PubblicataState);
  }

  @Test
  void inCorso_nonPiuCandidabile_assegnaLanciaEccezione() {
    // Una tesi già IN_CORSO non è più candidabile: una nuova assegnazione deve fallire
    Tesi tesi = new Tesi();
    tesi.setStatoOggetto(new InCorsoState());

    assertThrows(TransizioneNonValidaException.class, tesi::assegna);
  }

  // ================================
  // STATO: PUBBLICATA
  // ================================

  @Test
  void pubblicata_candidabile_assegnaTransizionaInCorso() {
    // Una tesi PUBBLICATA è candidabile: assegna() deve avere successo
    Tesi tesi = new Tesi();
    tesi.setStatoOggetto(new PubblicataState());

    assertDoesNotThrow(tesi::assegna);
    assertEquals("IN_CORSO", tesi.getStato());
    assertTrue(tesi.getStatoOggetto() instanceof InCorsoState);
  }

  @Test
  void pubblicata_pubblica_lanciaEccezione() {
    Tesi tesi = new Tesi();
    tesi.setStatoOggetto(new PubblicataState());

    assertThrows(TransizioneNonValidaException.class, tesi::pubblica);
  }

  // ================================
  // VERIFICA NOMI STATO
  // ================================

  @Test
  void getNomeStato_ritornaValoriCorretti() {
    assertEquals("BOZZA", new BozzaState().getNomeStato());
    assertEquals("IN_CORSO", new InCorsoState().getNomeStato());
    assertEquals("PUBBLICATA", new PubblicataState().getNomeStato());
  }

  // ================================
  // setStato() CON VALORI SCONOSCIUTI
  // ================================

  @Test
  void setStato_valoreSconosciuto_usaDefaultBozza() {
    Tesi tesi = new Tesi();
    tesi.setStato("XYZ");

    assertTrue(tesi.getStatoOggetto() instanceof BozzaState);
  }

  @Test
  void setStato_consegnata_cadeNelDefaultBozza_documentaComportamento() {
    // CASO LIMITE da discutere col gruppo: "CONSEGNATA" non è gestita nello
    // switch di Tesi.setStato(), quindi cade nel ramo default e diventa BozzaState.
    // Questo test documenta il comportamento ATTUALE, non necessariamente quello corretto.
    Tesi tesi = new Tesi();
    tesi.setStato("CONSEGNATA");

    assertTrue(tesi.getStatoOggetto() instanceof BozzaState);
  }

  @Test
  void setStato_accettata_cadeNelDefaultBozza_documentaComportamento() {
    // Stesso caso limite di "CONSEGNATA": "ACCETTATA" non gestita, finisce in BozzaState.
    Tesi tesi = new Tesi();
    tesi.setStato("ACCETTATA");

    assertTrue(tesi.getStatoOggetto() instanceof BozzaState);
  }
}
