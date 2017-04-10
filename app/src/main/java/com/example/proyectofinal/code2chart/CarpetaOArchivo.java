package com.example.proyectofinal.code2chart;

/**
 * Created by nico on 10/04/17.
 */

public class CarpetaOArchivo {

    private int icono;
    private String titulo;

    public CarpetaOArchivo(int unIcono, String untitulo) {
        super();
        this.icono = unIcono;
        this.titulo = untitulo;
    }

    public CarpetaOArchivo() {
        super();
    }

    public String getTitulo() {
        return titulo;
    }

    public int getIcono() {
        return icono;
    }
}
