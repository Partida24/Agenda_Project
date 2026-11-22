package com.example.agenda;



public class Contacte {

    private String nom;
    private String cognom;
    private String numero;
    private String dataNaix;
    private String adreca;
    private String email;



    public Contacte() {

    }

    public Contacte(String nom, String cognom, String numero, String dataNaix, String adreca, String email) {
        this.nom = nom;
        this.cognom = cognom;
        this.numero = numero;
        this.dataNaix = dataNaix;
        this.adreca = adreca;
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCognom() {
        return cognom;
    }

    public void setCognom(String cognom) {
        this.cognom = cognom;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDataNaix() {
        return dataNaix;
    }

    public void setDataNaix(String dataNaix) {
        this.dataNaix = dataNaix;
    }

    public String getAdreca() {
        return adreca;
    }

    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return nom + " " + cognom + " " + numero;
    }
}
