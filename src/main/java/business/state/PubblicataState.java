package business.state;

import model.Tesi;

public class PubblicataState implements TesiState {

  @Override
  public void pubblica(Tesi tesi) {
    System.out.println("⚠️ La tesi è già stata pubblicata!");
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
