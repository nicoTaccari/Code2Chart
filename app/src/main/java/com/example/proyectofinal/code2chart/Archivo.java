package com.example.proyectofinal.code2chart;

import android.app.Activity;
import android.net.Uri;

public class Archivo extends Activity {

    private String titulo;
    private String autor;
    private Uri uri;

    public Archivo (String untitulo, String unAutor) {
        this.titulo = untitulo;
        this.autor = unAutor;
    }


    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

}
