package model;

import java.sql.Timestamp;

public class RevisioneCapitolo {
    private int idRevisione;
    private int idTesi;
    private int numCapitolo;
    private String titoloCapitolo;
    private String percorsoPdf;
    private byte[] pdfData;
    private String noteProfessore;
    private String statoRevisione;
    private Timestamp dataInvio;

    public RevisioneCapitolo() {}

    public RevisioneCapitolo(int idTesi, int numCapitolo, String titoloCapitolo, String percorsoPdf) {
        this.idTesi = idTesi;
        this.numCapitolo = numCapitolo;
        this.titoloCapitolo = titoloCapitolo;
        this.percorsoPdf = percorsoPdf;
        this.statoRevisione = "IN_REVISIONE";
    }

    public RevisioneCapitolo(int idTesi, int numCapitolo, String titoloCapitolo, String nomeFile, byte[] pdfData) {
        this.idTesi = idTesi;
        this.numCapitolo = numCapitolo;
        this.titoloCapitolo = titoloCapitolo;
        this.percorsoPdf = nomeFile;
        this.pdfData = pdfData;
        this.statoRevisione = "IN_REVISIONE";
    }

    public int getIdRevisione() { return idRevisione; }
    public void setIdRevisione(int idRevisione) { this.idRevisione = idRevisione; }

    public int getIdTesi() { return idTesi; }
    public void setIdTesi(int idTesi) { this.idTesi = idTesi; }

    public int getNumCapitolo() { return numCapitolo; }
    public void setNumCapitolo(int numCapitolo) { this.numCapitolo = numCapitolo; }

    public String getTitoloCapitolo() { return titoloCapitolo; }
    public void setTitoloCapitolo(String titoloCapitolo) { this.titoloCapitolo = titoloCapitolo; }

    public String getPercorsoPdf() { return percorsoPdf; }
    public void setPercorsoPdf(String percorsoPdf) { this.percorsoPdf = percorsoPdf; }

    public byte[] getPdfData() { return pdfData; }
    public void setPdfData(byte[] pdfData) { this.pdfData = pdfData; }

    public String getNoteProfessore() { return noteProfessore; }
    public void setNoteProfessore(String noteProfessore) { this.noteProfessore = noteProfessore; }

    public String getStatoRevisione() { return statoRevisione; }
    public void setStatoRevisione(String statoRevisione) { this.statoRevisione = statoRevisione; }

    public Timestamp getDataInvio() { return dataInvio; }
    public void setDataInvio(Timestamp dataInvio) { this.dataInvio = dataInvio; }
}