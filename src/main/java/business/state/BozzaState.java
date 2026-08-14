package business.state;

import model.Tesi;
import business.exception.TransizioneNonValidaException;

public class BozzaState implements TesiState {

  @Override
  public void pubblica(Tesi tesi) {
    System.out.println("La tesi è stata pubblicata.");
    tesi.setStatoOggetto(new PubblicataState());
  }

  @Override
  public void assegna(Tesi tesi) {
    throw new TransizioneNonValidaException("Impossibile assegnare una tesi in Bozza. Va prima pubblicata!");
  }

  @Override
  public String getNomeStato() {
    return "BOZZA";
  }
}
