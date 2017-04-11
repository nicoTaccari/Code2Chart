package com.example.proyectofinal.code2chart;

import java.util.ArrayList;

public class CarpetaOArchivo {

    private int icono;
    private String titulo;
    private ArrayList<CarpetaOArchivo> subcarpetas;

    public CarpetaOArchivo(String untitulo, int unIcono) {
        this.icono = unIcono;
        this.titulo = untitulo;
    }

    public CarpetaOArchivo(String untitulo, int unIcono, ArrayList<CarpetaOArchivo> subcarpetas) {
        this.icono = unIcono;
        this.titulo = untitulo;
        this.subcarpetas = subcarpetas;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getIcono() {
        return icono;
    }
}
