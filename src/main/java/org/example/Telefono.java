package org.example;

public class Telefono {
    private int id;
    private int personaId;
    private String numero;

    public Telefono() {}

    public Telefono(String numero) {
        this.numero = numero;
    }

    public Telefono(int id, int personaId, String numero) {
        this.id = id;
        this.personaId = personaId;
        this.numero = numero;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPersonaId() { return personaId; }
    public void setPersonaId(int personaId) { this.personaId = personaId; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    @Override
    public String toString() {
        return numero;
    }
}