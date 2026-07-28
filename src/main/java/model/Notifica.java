package model;

import java.sql.Timestamp;

public class Notifica {
    private int idNotifica;
    private int idUtente;
    private String messaggio;
    private Timestamp dataInvio;
    private boolean letta;

    public Notifica() {}

    public Notifica(int idUtente, String messaggio) {
        this.idUtente = idUtente;
        this.messaggio = messaggio;
        this.letta = false;
    }

    public int getIdNotifica() { return idNotifica; }
    public void setIdNotifica(int idNotifica) { this.idNotifica = idNotifica; }

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public String getMessaggio() { return messaggio; }
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; }

    public Timestamp getDataInvio() { return dataInvio; }
    public void setDataInvio(Timestamp dataInvio) { this.dataInvio = dataInvio; }

    public boolean isLetta() { return letta; }
    public void setLetta(boolean letta) { this.letta = letta; }
}