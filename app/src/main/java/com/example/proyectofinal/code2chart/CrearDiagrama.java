package com.example.proyectofinal.code2chart;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class CrearDiagrama extends AppCompatActivity implements View.OnClickListener{

    private Button generar, obtenerUri;
    private EditText nombreUrl, nombreTitulo, nombreAutor;
    private ImageView icono;
    private TextView uriTexto;
    private static final int FILE_SELECT_CODE = 0;
    private String url, tipo;

    private String archivo = "nada";
    private Uri uri;
    private ArrayList<EditText> listaEditText = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        generar = (Button) findViewById(R.id.generar);
        generar.setOnClickListener(this);

        obtenerUri = (Button) findViewById(R.id.obtenerUri);
        obtenerUri.setOnClickListener(this);

        uriTexto = (TextView) findViewById(R.id.uri);
        icono = (ImageView) findViewById(R.id.iconoTipo);

        nombreUrl = (EditText) findViewById(R.id.url);

        nombreTitulo = (EditText) findViewById(R.id.nombreTitulo);
        nombreAutor = (EditText) findViewById(R.id.nombreAutor);
        listaEditText.add(nombreTitulo);
        listaEditText.add(nombreAutor);

    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/x-c\n/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Seleccione un archivo .c"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Por favor instale un Administrador de Archivos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        uri = data.getData();
                        archivo = getPath(CrearDiagrama.this, uri);
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
                break;
            default:
                icono.setImageResource(R.drawable.nada_logo);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.obtenerUri:
                showFileChooser();
                break;
            case R.id.generar:

                if(validarEditText(listaEditText)){
                    this.enviarOnClick(v);
                }else{
                    return;
                }
                break;
        }
    }

    private boolean validarEditText(ArrayList<EditText> textos){
        boolean error = true;

        for(int i=0; i<textos.size(); i++){
            EditText currentField = textos.get(i);
            String string = currentField.getText().toString();
            if(TextUtils.isEmpty(string) && string.trim().matches("")){
                currentField.setError("Complete el campo");
                error = false;
            }
        }

        url = nombreUrl.getText().toString();
        if (TextUtils.isEmpty(url) && url.trim().matches("") && !archivo.substring(archivo.length() - 2).equals(".c")) {
            Toast.makeText(this, "Archivo o url de Github inválidos", Toast.LENGTH_LONG).show();
            error = false;
        }

        if(!TextUtils.isEmpty(url) && !url.trim().matches("") && archivo.substring(archivo.length() - 2).equals(".c")){
            Toast.makeText(this, "Elegir un archivo o una url", Toast.LENGTH_LONG).show();
            error = false;
        }

        return error;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void enviarOnClick(View v){
        Intent intentDiagrama = new Intent(this, Diagrama.class);
        intentDiagrama.putExtra("uriDelArchivo", uri.toString());
        intentDiagrama.putExtra("tituloMando", nombreTitulo.getText().toString());
        intentDiagrama.putExtra("autorMando", nombreAutor.getText().toString());
        startActivity(intentDiagrama);

        finish();
    }

}