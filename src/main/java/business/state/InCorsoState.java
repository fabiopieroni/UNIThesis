package business.state;

import model.Tesi;
import business.exception.TransizioneNonValidaException;

public class InCorsoState implements TesiState {

  @Override
  public void pubblica(Tesi tesi) {
    System.out.println("La tesi in corso viene ripubblicata.");
    tesi.setStatoOggetto(new PubblicataState());
  }

  @Override
  public void assegna(Tesi tesi) {
    throw new TransizioneNonValidaException("La tesi è già in corso, non può essere assegnata di nuovo.");
  }

  @Override
  public String getNomeStato() {
    return "IN_CORSO";
  }
}
