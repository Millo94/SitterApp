package it.uniba.di.sms.sitterapp.oggetti;

/**
 * Classe che contiene l'oggetto "famiglia"
 */

public class UtenteFamiglia extends User{

    private String numFigli;
    private Boolean animali;


    //costruttore
    public UtenteFamiglia(String id,
                          String nameC,
                          String avatar,
                          String email,
                          String password,
                          String nazione,
                          String citta,
                          String telefono,
                          boolean online,
                          String numFigli,
                          Boolean animali) {
        super(id,nameC,avatar,email,password,nazione,citta,telefono,online);
        this.numFigli = numFigli;
        this.animali = animali;
    }

    //metodi get e set

    public String getNumFigli() {
        return numFigli;
    }

    public Boolean getAnimali() {
        return animali;
    }
}
