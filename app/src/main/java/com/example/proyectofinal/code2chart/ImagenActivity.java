package com.example.proyectofinal.code2chart;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagenActivity extends Activity {

    private String uri;
    private ImageView imagen;
    PhotoViewAttacher pAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen);

        Bundle bundleDiagrama = getIntent().getExtras();
        if(bundleDiagrama != null){
            uri = bundleDiagrama.getString("uri");
        }

        imagen = (ImageView) findViewById(R.id.imagenDiagrama);

        imagen.setImageURI(Uri.parse(uri));

        pAttacher = new PhotoViewAttacher(imagen);
        pAttacher.update();

    }
}
