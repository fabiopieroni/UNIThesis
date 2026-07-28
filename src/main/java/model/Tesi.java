package model;

public class Tesi {
  private int idTesi;
  private String titolo;
  private String descrizione;
  private String corsoLaurea;
  private String stato;
  private int idProfessore;

  // Costruttore vuoto
  public Tesi() {}

  // Costruttore completo (per leggere dal DB)
  public Tesi(int idTesi, String titolo, String descrizione, String corsoLaurea, String stato, int idProfessore) {
    this.idTesi = idTesi;
    this.titolo = titolo;
    this.descrizione = descrizione;
    this.corsoLaurea = corsoLaurea;
    this.stato = stato;
    this.idProfessore = idProfessore;
  }

  // Costruttore per nuovo inserimento (l'idTesi verrà generato dal DB)
  public Tesi(String titolo, String descrizione, String corsoLaurea, String stato, int idProfessore) {
    this.titolo = titolo;
    this.descrizione = descrizione;
    this.corsoLaurea = corsoLaurea;
    this.stato = stato;
    this.idProfessore = idProfessore;
  }

  // Getter e Setter
  public int getIdTesi() { return idTesi; }
  public void setIdTesi(int idTesi) { this.idTesi = idTesi; }

  public String getTitolo() { return titolo; }
  public void setTitolo(String titolo) { this.titolo = titolo; }

  public String getDescrizione() { return descrizione; }
  public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

  public String getCorsoLaurea() { return corsoLaurea; }
  public void setCorsoLaurea(String corsoLaurea) { this.corsoLaurea = corsoLaurea; }

  public String getStato() { return stato; }
  public void setStato(String stato) { this.stato = stato; }

  public int getIdProfessore() { return idProfessore; }
  public void setIdProfessore(int idProfessore) { this.idProfessore = idProfessore; }
}
