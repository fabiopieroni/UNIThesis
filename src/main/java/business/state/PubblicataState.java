package business.state;

import model.Tesi;
import business.exception.TransizioneNonValidaException;

public class PubblicataState implements TesiState {

  @Override
  public void pubblica(Tesi tesi) {
    throw new TransizioneNonValidaException("La tesi è già stata pubblicata!");
  }

  @Override
  public void assegna(Tesi tesi) {
    System.out.println("La tesi è stata assegnata.");
    tesi.setStatoOggetto(new InCorsoState());
  }

  @Override
  public String getNomeStato() {
    return "PUBBLICATA";
  }
}
