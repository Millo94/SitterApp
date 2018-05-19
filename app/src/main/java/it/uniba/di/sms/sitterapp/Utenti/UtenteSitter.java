package it.uniba.di.sms.sitterapp.Utenti;

/**
 * Classe che contiene i dati di una babysitter
 */

public class UtenteSitter {

    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String dataNascita;
    private String email;
    private String numero;
    private String numeroLavori;
    private String foto;
    private String genere;
    private String auto;

    public UtenteSitter(String username,
                        String password,
                        String nome,
                        String cognome,
                        String dataNascita,
                        String email,
                        String numero,
                        String genere,
                        String auto)
    {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.email = email;
        this.numero = numero;
        this.genere = genere;
        this.auto = auto;
    }

    public UtenteSitter(String username, String numeroLavori, String foto){
        this.username = username;
        this.numeroLavori = numeroLavori;
        this.foto = foto;
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

    public String getDataNascita() {
        return dataNascita;
    }

    public String getEmail() {
        return email;
    }

    public String getNumero() {
        return numero;
    }

    public String getFoto() {
        return foto;
    }

    public String getGenere() {
        return genere;
    }

    public String getAuto() { return auto; }

    public String getNumeroLavori() {
        return numeroLavori;
    }
}
