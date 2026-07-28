package business.state;

import model.Tesi;

public class BozzaState implements TesiState {

  @Override
  public void pubblica(Tesi tesi) {
    System.out.println("La tesi è stata pubblicata.");
    tesi.setStatoOggetto(new PubblicataState());
  }

  @Override
  public void assegna(Tesi tesi) {
    System.out.println("❌ Impossibile assegnare una tesi in Bozza. Va prima pubblicata!");
  }

  @Override
  public String getNomeStato() {
    return "BOZZA";
  }
}
