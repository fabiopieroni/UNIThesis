package model;

public class Studente extends Utente {
    private String matricola;
    private int cfuTotali;
    private String corsoLaurea;

    public Studente() {
        super();
    }

    public String getMatricola() { return matricola; }
    public void setMatricola(String matricola) { this.matricola = matricola; }

    public int getCfuTotali() { return cfuTotali; }
    public void setCfuTotali(int cfuTotali) { this.cfuTotali = cfuTotali; }

    public String getCorsoLaurea() { return corsoLaurea; }
    public void setCorsoLaurea(String corsoLaurea) { this.corsoLaurea = corsoLaurea; }
}