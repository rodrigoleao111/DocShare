package com.example.docshare.outros;

public class HistoricoOS {

    private String titulo, data;

    public HistoricoOS(String titulo, String data) {
        this.titulo = titulo;
        this.data = data;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
