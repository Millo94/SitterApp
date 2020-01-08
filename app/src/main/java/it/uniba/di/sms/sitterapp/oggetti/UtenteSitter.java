package it.uniba.di.sms.sitterapp.oggetti;

/**
 * Classe che contiene l'oggetto "babysitter"
 */

public class UtenteSitter extends User {

    private String dataNascita;
    private String genere;
    private Boolean auto;
    private float rating;
    private int numLavori;
    private String retribuzioneOra;

    //costruttore
    public UtenteSitter(String id,
                          String nameC,
                          String avatar,
                          String email,
                          String password,
                          String nazione,
                          String citta,
                          String telefono,
                          boolean online,
                          String dataNascita,
                          String genere,
                          float rating,
                          int numLavori,
                          Boolean auto,
                        String retribuzioneOra) {
        super(id,nameC,avatar,email,password,nazione,citta,telefono,online);
        this.dataNascita = dataNascita;
        this.genere = genere;
        this.rating = rating;
        this.numLavori = numLavori;
        this.auto = auto;
        this.retribuzioneOra = retribuzioneOra;
    }


    public UtenteSitter(String id, String nameC, String avatar, boolean online){
        super(id,nameC,avatar,online);
    }

    public UtenteSitter(String id, String nameC, String avatar, boolean online, float rating, int numLavori){
        super(id,nameC,avatar,online);
        this.rating = rating;
        this.numLavori = numLavori;
    }




    //metodi get
    public String getDataNascita() {
        return dataNascita;
    }

    public String getGenere() {
        return genere;
    }

    public Boolean getAuto() {
        return auto;
    }

    public float getRating() {
        return rating;
    }

    public int getNumLavori() {
        return numLavori;
    }

    public String getRetribuzioneOra() {
        return retribuzioneOra;
    }
}
