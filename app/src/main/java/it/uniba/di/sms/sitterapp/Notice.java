package it.uniba.di.sms.sitterapp;

import java.sql.Date;
import java.sql.Time;

public class Notice {
    private int id_notice;
    private String family;
    private Date date;
    private Time start_time;
    private Time end_time;
    private String description;

    public Notice(int id_notice,String family,Date date,Time start_time, Time end_time, String description){
        this.id_notice = id_notice;
        this.family = family;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.description = description;
    }

    public void setId_notice(int id_notice) {
        this.id_notice = id_notice;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEnd_time(Time end_time) {
        this.end_time = end_time;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public void setStart_time(Time start_time) {
        this.start_time = start_time;
    }

    public Date getDate() {
        return date;
    }

    public int getId_notice() {
        return id_notice;
    }

    public String getDescription() {
        return description;
    }

    public String getFamily() {
        return family;
    }

    public Time getEnd_time() {
        return end_time;
    }

    public Time getStart_time() {
        return start_time;
    }
}
