package model;

public class RichiestaConDettagli {
    private int id;
    private String nomeStudente;
    private String cognomeStudente;
    private String titoloTesi;
    private String stato;
    private java.sql.Timestamp dataRichiesta;
    private String motivazione;

    public RichiestaConDettagli(int id, String nomeStudente, String cognomeStudente,
                                String titoloTesi, String stato,
                                java.sql.Timestamp dataRichiesta, String motivazione) {
        this.id = id;
        this.nomeStudente = nomeStudente;
        this.cognomeStudente = cognomeStudente;
        this.titoloTesi = titoloTesi;
        this.stato = stato;
        this.dataRichiesta = dataRichiesta;
        this.motivazione = motivazione;
    }

    public int getId() { return id; }
    public String getNomeStudente() { return nomeStudente; }
    public String getCognomeStudente() { return cognomeStudente; }
    public String getTitoloTesi() { return titoloTesi; }
    public String getStato() { return stato; }
    public java.sql.Timestamp getDataRichiesta() { return dataRichiesta; }
    public String getMotivazione() { return motivazione; }
}