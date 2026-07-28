package business.state;

import model.Tesi;

public class InCorsoState implements TesiState {

  @Override
  public void pubblica(Tesi tesi) {
    System.out.println("La tesi in corso viene ripubblicata.");
    tesi.setStatoOggetto(new PubblicataState());
  }

  @Override
  public void assegna(Tesi tesi) {
    System.out.println("⚠️ La tesi è già in corso.");
  }

  @Override
  public String getNomeStato() {
    return "IN_CORSO";
  }
}
