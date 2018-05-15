package it.uniba.di.sms.sitterapp;

import java.sql.Date;

/**
 * Classe che contiene i dati di una babysitter
 */

public class UtenteSitter {

    String username;
    String password;
    String confermaPassword;
    String nome;
    String cognome;
    Date dataNascita;
    String email;
    String numero;
    String genere;
    boolean auto;

    public UtenteSitter(String username,
                        String password,
                        String confermaPassword,
                        String nome,
                        String cognome,
                        Date dataNascita,
                        String email,
                        String numero,
                        String genere,
                        boolean auto)
    {
        this.username = username;
        this.password = password;
        this.confermaPassword = confermaPassword;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.email = email;
        this.numero = numero;
        this.genere = genere;
        this.auto = auto;
    }
}
