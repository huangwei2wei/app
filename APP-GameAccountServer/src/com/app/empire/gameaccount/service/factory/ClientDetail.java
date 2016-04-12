package com.app.empire.gameaccount.service.factory;
public class ClientDetail {
    private String id;
    private String passWord;
    private int    address;

    public ClientDetail(String id, String password, int address) {
        this.id = id;
        this.passWord = password;
        this.address = address;
    }

    public String getId() {
        return this.id;
    }

    public String getPassWord() {
        return this.passWord;
    }

    public int getAddress() {
        return this.address;
    }

    public void setPassWord(String password) {
        this.passWord = password;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}
