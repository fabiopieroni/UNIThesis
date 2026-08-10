package model;

public class Professore extends Utente {
    private String matricolaDocente;
    private String corsoLaurea;
    private int numTesistiAttivi;

    public Professore() {
        super();
    }

    public String getMatricolaDocente() { return matricolaDocente; }
    public void setMatricolaDocente(String matricolaDocente) { this.matricolaDocente = matricolaDocente; }

    public String getCorsoLaurea() { return corsoLaurea; }
    public void setCorsoLaurea(String corsoLaurea) { this.corsoLaurea = corsoLaurea; }

    public int getNumTesistiAttivi() { return numTesistiAttivi; }
    public void setNumTesistiAttivi(int numTesistiAttivi) { this.numTesistiAttivi = numTesistiAttivi; }
}