package model;

import java.sql.Timestamp;

public class Richiesta {
    private int id;
    private int idStudente;
    private int idTesi;
    private String stato;
    private Timestamp dataRichiesta;
    private String motivazione;

    public Richiesta() {}

    public Richiesta(int idStudente, int idTesi, String motivazione) {
        this.idStudente = idStudente;
        this.idTesi = idTesi;
        this.motivazione = motivazione;
        this.stato = "IN_ATTESA";
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdStudente() { return idStudente; }
    public void setIdStudente(int idStudente) { this.idStudente = idStudente; }

    public int getIdTesi() { return idTesi; }
    public void setIdTesi(int idTesi) { this.idTesi = idTesi; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public Timestamp getDataRichiesta() { return dataRichiesta; }
    public void setDataRichiesta(Timestamp dataRichiesta) { this.dataRichiesta = dataRichiesta; }

    public String getMotivazione() { return motivazione; }
    public void setMotivazione(String motivazione) { this.motivazione = motivazione; }
}