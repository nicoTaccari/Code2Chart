package com.example.proyectofinal.code2chart;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.antlr.v4.runtime.Parser;

import java.net.URISyntaxException;

import parserUtils.CCompiler;

public class CrearDiagrama extends AppCompatActivity implements View.OnClickListener{

    private Button obtenerUri, generar;
    private EditText nombreTitulo;
    private ImageView icono;
    private TextView uriTexto;
    private static final int FILE_SELECT_CODE = 0;
    private String tipo;
    public static final int REQUEST_CODE = 10;
    private CCompiler compiler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        obtenerUri = (Button) findViewById(R.id.obtenerUri);
        obtenerUri.setOnClickListener(this);

        generar = (Button) findViewById(R.id.generar);
        generar.setOnClickListener(this);

        uriTexto = (TextView) findViewById(R.id.uri);
        icono = (ImageView) findViewById(R.id.iconoTipo);
        nombreTitulo = (EditText) findViewById(R.id.nombreTitulo);

    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        String archivo = getPath(CrearDiagrama.this, uri);
                        uriTexto.setText(archivo);
                        tipo = archivo.substring(archivo.indexOf(".")+1);
                        seleccionarIcono();
                    }catch(Exception e){
                        // Eat it
                    }
                }
            break;
        }
    }

    public String getPath(Context context, Uri uri) throws URISyntaxException {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_display_name" };
            Cursor cursor;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor.moveToFirst()) {
                    String name = cursor.getString(0);
                    cursor.close();
                    return name;
                }
            }catch(Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public void seleccionarIcono(){
        switch(tipo){
            case "c":
                icono.setImageResource(R.drawable.c);
                compiler = new CCompiler();
                break;
            default:
                icono.setImageResource(R.drawable.nada_logo);
                compiler = null;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.obtenerUri:
                showFileChooser();
                break;
            case R.id.generar:
                /*if(nombreTitulo.getText().toString().equals("")){
                    Toast.makeText(this, "Completar todos los campos", Toast.LENGTH_SHORT).show();
                }else {*/
                    //parser.parse(uriTexto.getText().toString());
                    //this.enviarOnClick(v);
                    Intent generarDiagrama = new Intent(this, Diagrama.class);
                    startActivity(generarDiagrama);
                break;
        }
    }

    public void enviarOnClick(View v){
        Intent intentDiagrama = new Intent(this, Diagrama.class);
        String XmlName = "SampleGraph"; //TODO
        intentDiagrama.putExtra("imagenDiagrama", XmlName +".xml");
        intentDiagrama.putExtra("nombreImagen", nombreTitulo.getText().toString());
        startActivityForResult(intentDiagrama, REQUEST_CODE);
    }

}