package com.example.proyectofinal.code2chart;

/**
 * Created by nico on 11/04/17.
 */

public abstract class FileComposite {

    protected int icono;
    protected String titulo;

    public int getIcono() {
        return icono;
    }

    public String getTitulo() {
        return titulo;
    }

    abstract void abrir();
}
