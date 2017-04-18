package com.example.proyectofinal.code2chart;

import android.content.Context;
import android.widget.ListView;

/**
 * Created by nico on 11/04/17.
 */

public class Archivo {

    public int getIcono() {
        return icono;
    }

    public String getTitulo() {
        return titulo;
    }

    private int icono;
    private String titulo;

    public Archivo (String untitulo, int unIcono) {
        this.icono = unIcono;
        this.titulo = untitulo;
    }

    public void abrir(Context context, ListView listaDeArchivos) {
        //TODO implementar cuando haya archivos para mostrar
    }
}
