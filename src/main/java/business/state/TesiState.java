package business.state;

import model.Tesi;

public interface TesiState {
  void pubblica(Tesi tesi);
  void assegna(Tesi tesi);
  String getNomeStato();
}
