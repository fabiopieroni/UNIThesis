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
    Tesi tesi = new Tesi();
    tesi.setStatoOggetto(new InCorsoState());

    assertThrows(TransizioneNonValidaException.class, tesi::assegna);
  }

  // ================================
  // STATO: PUBBLICATA
  // ================================

  @Test
  void pubblicata_candidabile_assegnaTransizionaInCorso() {
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
  // STATO: CONSEGNATA
  // ================================

  @Test
  void consegnata_pubblica_lanciaEccezione() {
    Tesi tesi = new Tesi();
    tesi.setStatoOggetto(new ConsegnataState());

    assertThrows(TransizioneNonValidaException.class, tesi::pubblica);
  }

  @Test
  void consegnata_assegna_lanciaEccezione() {
    Tesi tesi = new Tesi();
    tesi.setStatoOggetto(new ConsegnataState());

    assertThrows(TransizioneNonValidaException.class, tesi::assegna);
  }

  // ================================
  // STATO: ACCETTATA
  // ================================

  @Test
  void accettata_pubblica_lanciaEccezione() {
    Tesi tesi = new Tesi();
    tesi.setStatoOggetto(new AccettataState());

    assertThrows(TransizioneNonValidaException.class, tesi::pubblica);
  }

  @Test
  void accettata_assegna_lanciaEccezione() {
    Tesi tesi = new Tesi();
    tesi.setStatoOggetto(new AccettataState());

    assertThrows(TransizioneNonValidaException.class, tesi::assegna);
  }

  // ================================
  // VERIFICA NOMI STATO
  // ================================

  @Test
  void getNomeStato_ritornaValoriCorretti() {
    assertEquals("BOZZA", new BozzaState().getNomeStato());
    assertEquals("IN_CORSO", new InCorsoState().getNomeStato());
    assertEquals("PUBBLICATA", new PubblicataState().getNomeStato());
    assertEquals("CONSEGNATA", new ConsegnataState().getNomeStato());
    assertEquals("ACCETTATA", new AccettataState().getNomeStato());
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

  // ================================
  // setStato() CON CONSEGNATA / ACCETTATA
  // ================================

  @Test
  void setStato_consegnata_usaConsegnataState() {
    Tesi tesi = new Tesi();
    tesi.setStato("CONSEGNATA");

    assertTrue(tesi.getStatoOggetto() instanceof ConsegnataState);
    assertEquals("CONSEGNATA", tesi.getStatoOggetto().getNomeStato());
  }

  @Test
  void setStato_accettata_usaAccettataState() {
    Tesi tesi = new Tesi();
    tesi.setStato("ACCETTATA");

    assertTrue(tesi.getStatoOggetto() instanceof AccettataState);
    assertEquals("ACCETTATA", tesi.getStatoOggetto().getNomeStato());
  }
}