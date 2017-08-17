package com.example.proyectofinal.code2chart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by nico on 11/04/17.
 */

public class Archivo extends Activity {

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

    public void abrir(Context context, ListView listaDeArchivos, File file, String mimeType) {
        Toast.makeText(context, "carlos", Toast.LENGTH_SHORT).show();

        Uri imagenUri = Uri.fromFile(file);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(imagenUri, mimeType);
        startActivity(intent);

    }



}
