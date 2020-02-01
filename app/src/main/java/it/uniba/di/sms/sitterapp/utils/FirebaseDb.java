package it.uniba.di.sms.sitterapp.utils;

public class FirebaseDb {

    public static final String BASE_URL = "gs://sitterapp-223aa.appspot.com";
    public static final String STOCK_PATH_PHOTO = BASE_URL+"/img/stock_img/placeholder-profile-sq.jpg";
    public static final String LOCAL_USERIMAGE_PATH = "img/user_img/";


    //Collezione Utente per
    public static final String USERS = "utente";
    public static final String USER_NOME_COMPLETO = "NomeCompleto";
    public static final String USER_EMAIL = "Email";
    public static final String USER_AVATAR = "Avatar";
    public static final String USER_CITTA = "Citta";
    public static final String USER_NAZIONE = "Nazione";
    public static final String USER_DESCRIZIONE = "Descrizione";
    public static final String USER_TELEFONO = "Telefono";
    public static final String USER_ONLINE = "online";
    public static final String USER_TIPOUTENTE = "tipoUtente";
    public static final String USER_PASSWORD = "password";


    public static final String BABYSITTER = "babysitter";
    public static final String BABYSITTER_AUTO = "Auto";
    public static final String BABYSITTER_DISPONIBILITA = "Disponibilita";
    public static final String BABYSITTER_GENERE = "Genere";
    public static final String BABYSITTER_RATING = "Rating";
    public static final String BABYSITTER_RETRIBUZIONE = "Retribuzione";
    public static final String BABYSITTER_DATANASCITA = "dataNascita";
    public static final String BABYSITTER_NUMLAVORI = "numLavori";

    public static final String FAMIGLIA = "famiglia";
    public static final String FAMIGLIA_ANIMALI = "Animali";
    public static final String FAMIGLIA_RATING = "rating";
    public static final String FAMIGLIA_NUMFIGLI = "numFigli";


    public static final String REVIEWS = "recensione";
    public static final String REVIEW_DESCRIZIONE = "Descrizione";
    public static final String REVIEW_IDANNUNCIO = "idAnnuncio";
    public static final String REVIEW_RATING = "rating";
    public static final String REVIEW_RECEIVER = "receiver";
    public static final String REVIEW_SENDER = "sender";
    public static final String REVIEW_TIPOUTENTE = "tipoUtente";

    public static final String ENGAGES = "annuncio";
    public static final String ENGAGE_CANDIDATURA  = "candidatura";
    public static final String ENGAGES_CONFERMA = "conferma";
    public static final String ENGAGES_DATA = "date";
    public static final String ENGAGES_DESCRIZIONE = "description";
    public static final String ENGAGES_ORAFINE = "end_time";
    public static final String ENGAGES_ORAINIZIO = "start_time";
    public static final String ENGAGES_FAMIGLIA = "family";
    public static final String ENGAGES_IDANNUNCIO = "idAnnuncio";
    public static final String ENGAGES_SITTER = "sitter";


    public static final String CHAT = "chat";
    public static final String CHAT_MESSAGES = "Messages";
    public static final String CHAT_MESSAGES_TEXT = "text";
    public static final String CHAT_MESSAGES_TIMESTAMP = "timestamp";
    public static final String CHAT_MESSAGES_USER = "user";

    public static final String CHAT_USERS = "Users";
    public static final String CHAT_USER_AVATAR = "avatar";
    public static final String CHAT_USER_NAME = "name";
    public static final String CHAT_USER_ONLINE = "online";

    public static final String CHAT_USERSLIST = "UsersList";
    public static final String CHAT_LASTMESSAGE = "lastMessage";
    public static final String LASTMESSAGE_ID = "id";
    public static final String LASTMESSAGE_TEXT = "text";
    public static final String LASTMESSAGE_TIMESTAMP = "timestamp";
    public static final String LASTMESSAGE_USER = "user";
    public static final String LASTMESSAGE_USER_ID = "id";
    public static final String LASTMESSAGE_USER_NAME = "name";
    public static final String LASTMESSAGE_USER_AVATAR = "avatar";
    public static final String LASTMESSAGE_USER_ONLINE = "online";

}
