package model;

public class TesiConDettagli {
    private int idTesi;
    private String titolo;
    private String stato;
    private String nomeProfessore;
    private String nomeStudente;

    public TesiConDettagli(int idTesi, String titolo, String stato, String nomeProfessore, String nomeStudente) {
        this.idTesi = idTesi;
        this.titolo = titolo;
        this.stato = stato;
        this.nomeProfessore = nomeProfessore;
        this.nomeStudente = nomeStudente;
    }

    public int getIdTesi() { return idTesi; }
    public String getTitolo() { return titolo; }
    public String getStato() { return stato; }
    public String getNomeProfessore() { return nomeProfessore; }
    public String getNomeStudente() { return nomeStudente; }
}