package it.uniba.di.sms.sitterapp.oggetti;

/**
 * Classe che contiene l'oggetto "babysitter"
 */

public class UtenteSitter extends User {

    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String dataNascita;
    private String email;
    private String numero;
    private String foto;
    private String genere;
    private String nazione;
    private String cap;
    private String auto;
    private float rating;
    private int numLavori;

    //costruttore
    public UtenteSitter(String username,
                        String password,
                        String nome,
                        String cognome,
                        String dataNascita,
                        String email,
                        String numero,
                        String genere,
                        String nazione,
                        String cap,
                        String auto) {
        super(username,nome+" "+cognome,null,true);
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.email = email;
        this.numero = numero;
        this.genere = genere;
        this.nazione = nazione;
        this.cap = cap;
        this.auto = auto;
    }

    public UtenteSitter(String username,String pathPhoto){
        super(username,"",pathPhoto,false);
        this.username = username;
        this.foto = pathPhoto;
    }

    //costruttore
    public UtenteSitter(String username, String foto, float rating, int numLavori) {
        super(username,"",foto,false);
        this.username = username;
        this.foto = foto;
        this.rating = rating;
        this.numLavori = numLavori;
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

    public String getAuto() {
        return auto;
    }

    public String getNazione() {
        return nazione;
    }

    public String getCap() {
        return cap;
    }

    public float getRating() {
        return rating;
    }

    public int getNumLavori() {
        return numLavori;
    }
}
