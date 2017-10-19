package com.example.proyectofinal.code2chart;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class CrearDiagrama extends AppCompatActivity implements View.OnClickListener{

    private Button generar, obtenerUri, eliminarUri;
    private EditText nombreUrl, nombreTitulo, nombreAutor;
    private ImageView icono;
    private TextView uriTexto;
    private static final int FILE_SELECT_CODE = 0;
    private String url, tipo;

    private String archivo = "Seleccionar archivo";
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
        eliminarUri = (Button) findViewById(R.id.eliminarUri);
        eliminarUri.setOnClickListener(this);

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
                eliminarUri.setVisibility(View.VISIBLE);
                obtenerUri.setVisibility(View.INVISIBLE);
                break;
            case R.id.generar:
                if(validarEditText(listaEditText)){
                    this.enviarOnClick(v);
                }
                break;
            case R.id.eliminarUri:
                uriTexto.setText("Seleccionar archivo");
                eliminarUri.setVisibility(View.VISIBLE);
                obtenerUri.setVisibility(View.INVISIBLE);
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

        if (!archivo.substring(archivo.length() - 2).equals(".c") && !archivo.equals("Seleccionar archivo") && TextUtils.isEmpty(url) && url.trim().matches("")) {
            Toast.makeText(this, "Archivo inválido", Toast.LENGTH_LONG).show();
            error = false;
        }

        if (!TextUtils.isEmpty(url) && !url.trim().matches("") && !url.substring(url.length() - 2).equals(".c")&& archivo.equals("Seleccionar archivo")){
            Toast.makeText(this, "Url de Github inválida", Toast.LENGTH_LONG).show();
            error = false;
        }

        if((!TextUtils.isEmpty(url) || !url.trim().matches("")) && !archivo.equals("Seleccionar archivo")){
            Toast.makeText(this, "Elegir un archivo o una url", Toast.LENGTH_LONG).show();
            error = false;
        }

        if((TextUtils.isEmpty(url) && url.trim().matches("")) && archivo.equals("Seleccionar archivo")){
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
        String codigo = "vieneVacío";
        Intent intentDiagrama = new Intent(this, Diagrama.class);
        intentDiagrama.putExtra("tituloMando", nombreTitulo.getText().toString());
        intentDiagrama.putExtra("autorMando", nombreAutor.getText().toString());
        if(!TextUtils.isEmpty(url) && !url.trim().matches("") && url.substring(url.length() - 2).equals(".c")) {
            try {
                codigo = escribirEnFS(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            intentDiagrama.putExtra("uriDelArchivo", uri.toString());
        }
        intentDiagrama.putExtra("codigo", codigo);
        startActivity(intentDiagrama);
        finish();
    }

    public static String escribirEnFS(String dirUrl) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //set connection and url
        URL url = new URL(dirUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);

        String completo = new String();

        //read and store in my string variable the whole content
        BufferedReader reader = new BufferedReader ( new InputStreamReader(connection.getInputStream()));
        for (String line; (line = reader.readLine()) != null;) {
            completo = completo.concat(line);
        }
        reader.close();

        return completo;
    }

}
