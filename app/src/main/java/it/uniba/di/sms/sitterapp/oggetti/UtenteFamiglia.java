package it.uniba.di.sms.sitterapp.oggetti;

/**
 * Classe che contiene l'oggetto "famiglia"
 */

public class UtenteFamiglia extends User{

    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String email;
    private String numero;
    private String nazione;
    private String citta;
    private String civico;
    private String numFigli;
    private Boolean animali;


    //costruttore
    public UtenteFamiglia(String username,
                          String password,
                          String nome,
                          String cognome,
                          String email,
                          String numero,
                          String nazione,
                          String citta,
                          String numFigli,
                          Boolean animali) {
        super(username,nome+" "+cognome,null,true);
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.numero = numero;
        this.nazione = nazione;
        this.citta = citta;
        this.numFigli = numFigli;
        this.animali = animali;
    }

    //metodi get e set
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getNumero() {
        return numero;
    }

    public String getNazione() {
        return nazione;
    }

    public String getCitta() {
        return citta;
    }

    public String getCivico() {
        return civico;
    }

    public String getNumFigli() {
        return numFigli;
    }

    public Boolean getAnimali() {
        return animali;
    }
}
