package it.uniba.di.sms.sitterapp.oggetti;

/**
 * Classe che contiene l'oggetto "Recensione"
 */

public class Recensione {

    String descrizione;
    Float rating;
    String idAnnuncio;
    String receiver;
    String sender;


    //costruttore
    public Recensione(String descrizione, Float rating, String idAnnuncio, String receiver, String sender) {
        this.descrizione = descrizione;
        this.rating = rating;
        this.idAnnuncio = idAnnuncio;
        this.receiver = receiver;
        this.sender = sender;

    }


    public Recensione(){

    }
    //metodi get e set

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getIdAnnuncio() {
        return idAnnuncio;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }



}


