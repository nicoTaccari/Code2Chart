package com.example.proyectofinal.code2chart;

public class CarpetaOArchivo {

    private int icono;
    private String titulo;

    public CarpetaOArchivo(String untitulo, int unIcono) {
        this.icono = unIcono;
        this.titulo = untitulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getIcono() {
        return icono;
    }
}
