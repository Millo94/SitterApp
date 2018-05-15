package it.uniba.di.sms.sitterapp;

/**
 * Classe che contiene i dati di una famiglia
 */

public class UtenteFamiglia {

    String username;
    String password;
    String confermaPassword;
    String nome;
    String cognome;
    String email;
    String numero;
    String nazione;
    String provincia;
    String citta;
    String via;
    String civico;
    int numFigli;
    int animali;

    public UtenteFamiglia(String username,
                        String password,
                        String confermaPassword,
                        String nome,
                        String cognome,
                        String email,
                        String numero,
                        String nazione,
                        String provincia,
                        String citta,
                        String via,
                        String civico,
                        int numFigli,
                        int animali)
    {
        this.username = username;
        this.password = password;
        this.confermaPassword = confermaPassword;
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
}
