package model;

import business.state.*;

public class Tesi {
  private int idTesi;
  private String titolo;
  private String descrizione;
  private String corsoLaurea;
  private String stato;
  private int idProfessore;
  private TesiState statoOggetto;

  public Tesi() {
    setStatoOggetto(new BozzaState());
  }

  public Tesi(int idTesi, String titolo, String descrizione, String corsoLaurea, String stato, int idProfessore) {
    this.idTesi = idTesi;
    this.titolo = titolo;
    this.descrizione = descrizione;
    this.corsoLaurea = corsoLaurea;
    this.stato = stato;
    this.idProfessore = idProfessore;
    setStato(stato);
  }

  public Tesi(String titolo, String descrizione, String corsoLaurea, String stato, int idProfessore) {
    this.titolo = titolo;
    this.descrizione = descrizione;
    this.corsoLaurea = corsoLaurea;
    setStato(stato);
    this.idProfessore = idProfessore;
  }

  public void pubblica() {
    if (statoOggetto != null) {
      statoOggetto.pubblica(this);
    }
  }

  public void assegna() {
    if (statoOggetto != null) {
      statoOggetto.assegna(this);
    }
  }

  public void setStatoOggetto(TesiState nuovoStato) {
    this.statoOggetto = nuovoStato;
    if (nuovoStato != null) {
      this.stato = nuovoStato.getNomeStato();
    }
  }

  public TesiState getStatoOggetto() {
    return statoOggetto;
  }

  public int getIdTesi() { return idTesi; }
  public void setIdTesi(int idTesi) { this.idTesi = idTesi; }

  public String getTitolo() { return titolo; }
  public void setTitolo(String titolo) { this.titolo = titolo; }

  public String getDescrizione() { return descrizione; }
  public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

  public String getCorsoLaurea() { return corsoLaurea; }
  public void setCorsoLaurea(String corsoLaurea) { this.corsoLaurea = corsoLaurea; }

  public String getStato() { return stato; }
  public void setStato(String stato) {
    this.stato = stato;
    if (stato != null) {
      switch (stato.toUpperCase()) {
        case "BOZZA":
          this.statoOggetto = new BozzaState();
          break;
        case "IN_CORSO":
          this.statoOggetto = new InCorsoState();
          break;
        case "PUBBLICATA":
        case "DISPONIBILE":
          this.statoOggetto = new PubblicataState();
          break;
        case "CONSEGNATA":
          this.statoOggetto = new ConsegnataState();
          break;
        case "ACCETTATA":
          this.statoOggetto = new AccettataState();
          break;
        default:
          this.statoOggetto = new BozzaState();
          break;
      }
    }
  }

  public int getIdProfessore() { return idProfessore; }
  public void setIdProfessore(int idProfessore) { this.idProfessore = idProfessore; }
}
