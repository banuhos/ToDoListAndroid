package com.banu.bh318.todolist.Modal;

/**
 * Created by bh318 on 23.08.2019.
 */

public class User {
    private String email;
    private String sifre;

    public User(String email, String sifre) {
        this.email = email;
        this.sifre = sifre;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }
}
