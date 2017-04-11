package com.example.proyectofinal.code2chart;

/**
 * Created by nico on 11/04/17.
 */

import java.util.ArrayList;

public class Carpeta extends FileComposite {

    private ArrayList<FileComposite> subCarpetas;


    public Carpeta (String untitulo, int unIcono) {
        this.icono = unIcono;
        this.titulo = untitulo;
        this.subCarpetas = new ArrayList<>();
    }

    public void addSubcarpeta(FileComposite sub){
        this.subCarpetas.add(sub);
    }

    /*accesors*/

    public ArrayList<FileComposite> getSubcarpetas() {
        return this.subCarpetas;
    }

    public void setSubcarpetas(ArrayList<FileComposite> subcarpetas) {
        this.subCarpetas = subcarpetas;
    }

    @Override
    public void abrir() {

    }
}

