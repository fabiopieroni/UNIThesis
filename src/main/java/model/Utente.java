package model;

public class Utente {
    private int idUtente;
    private String email;
    private String password;
    private String ruolo;
    private String nome;
    private String cognome;

    public Utente() {}

    public Utente(int idUtente, String email, String password, String ruolo, String nome, String cognome) {
        this.idUtente = idUtente;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
        this.nome = nome;
        this.cognome = cognome;
    }

    public Utente(String email, String password, String ruolo, String nome, String cognome) {
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
        this.nome = nome;
        this.cognome = cognome;
    }

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
}