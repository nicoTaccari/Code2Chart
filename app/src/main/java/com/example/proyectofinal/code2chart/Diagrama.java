package com.example.proyectofinal.code2chart;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Diagrama extends AppCompatActivity implements View.OnClickListener {

    private Button guardar, descartar;
    private ImageView imagen;
    private Bitmap bitmapReal;
    private String titulo, autor;
    private String xmlName = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagrama);

        descartar = (Button) findViewById(R.id.descartar);
        descartar.setOnClickListener(this);

        guardar = (Button) findViewById(R.id.guardar);
        guardar.setOnClickListener(this);

        imagen = (ImageView) findViewById(R.id.imagen);

        Bundle bundleDiagrama = getIntent().getExtras();
        if(bundleDiagrama != null){
            titulo = bundleDiagrama.getString("tituloMando");
            autor = bundleDiagrama.getString("autorMando");
            bitmapReal = bundleDiagrama.getParcelable("imagebitmap");
            xmlName = bundleDiagrama.getString("xmlName");
        }

        imagen.setImageBitmap(bitmapReal);

    }

    @Override
    public void onClick(View v) {
        File fdelete = new File(xmlName);
        switch (v.getId()){
            case R.id.guardar:
                startSave();
                fdelete.delete();
                finish();
                break;
            case R.id.descartar:
                fdelete.delete();
                finish();
                break;

        }
    }

    public void startSave(){
        FileOutputStream fileOutputStream = null;
        File file = getFilesDir();
        if(!file.exists() && !file.mkdirs()){
            Toast.makeText(this, "No se puede crear el directorio", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = titulo + "." + autor + ".png";
        String file_name = file.getAbsolutePath()+"/"+name;
        File new_file = new File(file_name);
        try{
            fileOutputStream = new FileOutputStream(new_file);
            bitmapReal.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}