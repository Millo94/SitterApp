package it.uniba.di.sms.sitterapp.oggetti;

import com.stfalcon.chatkit.commons.models.IUser;

/*
 * Created by troy379 on 04.04.17.
 */
public class User implements IUser {

    private String id;
    private String nameC;
    private String avatar;
    private String email;
    private String password;
    private String nazione;
    private String citta;
    private String telefono;
    private boolean online;

    public User(String id, String nameC, String avatar, String email, String password,
                String nazione, String citta, String telefono, boolean online) {
        this.id = id;
        this.nameC = nameC;
        this.avatar = avatar;
        this.email = email;
        this.password = password;
        this.nazione = nazione;
        this.citta = citta;
        this.telefono = telefono;
        this.online = online;
    }

    public User(String id, String nameC, String avatar, boolean online) {
        this.id = id;
        this.nameC = nameC;
        this.avatar = avatar;
        this.online = online;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return nameC;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNazione() {
        return nazione;
    }

    public String getCitta() {
        return citta;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isOnline() {
        return online;
    }
}
