package model;

public class CorsoDiLaurea {
    private int id;
    private String nome;
    private String dipartimento;

    public CorsoDiLaurea() {}

    public CorsoDiLaurea(int id, String nome, String dipartimento) {
        this.id = id;
        this.nome = nome;
        this.dipartimento = dipartimento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDipartimento() { return dipartimento; }
    public void setDipartimento(String dipartimento) { this.dipartimento = dipartimento; }
}