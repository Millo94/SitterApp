package it.uniba.di.sms.sitterapp.Oggetti;

/**
 * Classe che contiene i dati di una famiglia
 */

public class UtenteFamiglia {

    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String email;
    private String numero;
    private String nazione;
    private String provincia;
    private String citta;
    private String via;
    private String civico;
    private String numFigli;
    private String animali;

    public UtenteFamiglia(String username,
                        String password,
                        String nome,
                        String cognome,
                        String email,
                        String numero,
                        String nazione,
                        String provincia,
                        String citta,
                        String via,
                        String civico,
                        String numFigli,
                        String animali)
    {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.numero = numero;
        this.nazione = nazione;
        this.provincia = provincia;
        this.citta = citta;
        this.via = via;
        this.civico = civico;
        this.numFigli = numFigli;
        this.animali = animali;
    }

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

    public String getProvincia() {
        return provincia;
    }

    public String getCitta() {
        return citta;
    }

    public String getVia() {
        return via;
    }

    public String getCivico() {
        return civico;
    }

    public String getNumFigli() {
        return numFigli;
    }

    public String getAnimali() {
        return animali;
    }
}
