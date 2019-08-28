package com.banu.bh318.todolist.Modal;

/**
 * Created by bh318 on 23.08.2019.
 */

public class ToDoList_Gonderiler {
    private String baslik,aciklama,date,gorevId,gorunumCheckbox,gorunumLock,kilitDurum;


    public ToDoList_Gonderiler(String gorevId, String gorev, String tarih){
        this.gorevId=gorevId;
        this.baslik=gorev;
        this.date=tarih;
    }
    public ToDoList_Gonderiler(String gorevId, String gorev, String aciklama, String tarih, String gorunumCheckbox, String gorunumLock){
        this.gorevId=gorevId;
        this.baslik=gorev;
        this.aciklama=aciklama;
        this.date=tarih;
        this.gorunumCheckbox=gorunumCheckbox;
        this.gorunumLock=gorunumLock;
    }

    public String getGorevId() {
        return gorevId;
    }

    public void setGorevId(String gorevId) {
        this.gorevId = gorevId;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGorunumCheckbox() {
        return gorunumCheckbox;
    }

    public void setGorunumCheckbox(String gorunumCheckbox) {
        this.gorunumCheckbox = gorunumCheckbox;
    }

    public String getGorunumLock() {
        return gorunumLock;
    }

    public void setGorunumLock(String gorunumLock) {
        this.gorunumLock = gorunumLock;
    }

    public String getKilitDurum() {
        return kilitDurum;
    }

    public void setKilitDurum(String kilitDurum) {
        this.kilitDurum = kilitDurum;
    }
}
