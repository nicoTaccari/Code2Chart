package com.example.proyectofinal.code2chart;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrearDiagrama extends AppCompatActivity implements View.OnClickListener{

    private Button generar, obtenerUri, eliminarUri;
    private EditText nombreUrl, nombreTitulo, nombreAutor;
    private ImageView icono;
    private TextView uriTexto;
    private static final int FILE_SELECT_CODE = 0;
    private String url, nombreUsuario;

    private ArrayList<String> titulos = new ArrayList<>();
    private ArrayList<String> autores = new ArrayList<>();

    private String tipo = "nada";
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

        uri = Uri.parse("vacía");

        Bundle bundleDiagrama = getIntent().getExtras();
        if(bundleDiagrama != null){
            nombreUsuario= bundleDiagrama.getString("usuario");
            titulos = (ArrayList<String>) bundleDiagrama.get("titulos");
            autores = (ArrayList<String>) bundleDiagrama.get("autores");
        }

        if(!nombreUsuario.equals("Android")) {
            nombreAutor.setText(nombreUsuario);
        }

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
                    if(!uri.toString().equals("vacía")){
                        eliminarUri.setVisibility(View.VISIBLE);
                        obtenerUri.setVisibility(View.INVISIBLE);
                    }else {
                        eliminarUri.setVisibility(View.INVISIBLE);
                        obtenerUri.setVisibility(View.VISIBLE);
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
                    if(archivoExistente(titulos, autores)){
                        enviarOnClick();
                    }
                }
                break;
            case R.id.eliminarUri:
                uriTexto.setText("Seleccionar archivo");
                eliminarUri.setVisibility(View.INVISIBLE);
                obtenerUri.setVisibility(View.VISIBLE);
                archivo = "Seleccionar archivo";
                tipo = "nada";
                uri = Uri.parse("vacía");
                seleccionarIcono();
                break;
        }
    }

    public boolean archivoExistente(ArrayList<String> losTitulos, ArrayList<String> losAutores){
        boolean seEncuentra = true;
        for(int i =  0; i < losTitulos.size(); i++){
            if(losTitulos.get(i).equals(nombreTitulo.getText().toString())
                    && losAutores.get(i).equals(nombreAutor.getText().toString())){
                seEncuentra = false;
                Toast.makeText(this, "Ya existe un archivo con esos datos", Toast.LENGTH_LONG).show();
                break;
            }
        }
        return seEncuentra;
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

        //Archivo inválido -> no termina en .c
        if (!archivo.substring(archivo.length() - 2).equals(".c") && !archivo.equals("Seleccionar archivo") && TextUtils.isEmpty(url) && url.trim().matches("")) {
            Toast.makeText(this, "Archivo inválido", Toast.LENGTH_LONG).show();
            error = false;
        }

        //Verificar URL
        if (!TextUtils.isEmpty(url) && !url.trim().matches("") && archivo.equals("Seleccionar archivo")){
            if(internetConnection()) {
                if (!isValidUrl(url)) {
                    Toast.makeText(this, "Url inválida", Toast.LENGTH_LONG).show();
                    error = false;
                }
            }else{
                Toast.makeText(this, "No hay conección a Internet", Toast.LENGTH_LONG).show();
                error = false;
            }
        }

        //Caso en el que pone un archivo y una url
        if((!TextUtils.isEmpty(url) || !url.trim().matches("")) && (!archivo.equals("Seleccionar archivo") || archivo.substring(archivo.length() - 2).equals(".c"))){
            Toast.makeText(this, "Elegir un archivo o una url", Toast.LENGTH_LONG).show();
            error = false;
        }

        //Caso en el que no pone ni archivo, ni una url
        if((TextUtils.isEmpty(url) && url.trim().matches("")) && archivo.equals("Seleccionar archivo")){
            Toast.makeText(this, "Elegir un archivo o una url", Toast.LENGTH_LONG).show();
            error = false;
        }

        return error;
    }

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    public boolean internetConnection(){
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext()
                .getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivity != null){
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if(info != null){
                if(info.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void enviarOnClick(){
        String codigo = "vieneVacío";
        Intent intentDiagrama = new Intent(this, Diagrama.class);
        intentDiagrama.putExtra("tituloMando", nombreTitulo.getText().toString());
        if(!nombreUsuario.equals("Android")) {
            intentDiagrama.putExtra("autorMando", nombreUsuario);
        }else{
            intentDiagrama.putExtra("autorMando", nombreAutor.getText().toString());
        }
        if(!TextUtils.isEmpty(url) && !url.trim().matches("") && url.substring(0,12).equals("https://raw.")) {
            try {
                codigo = escribirEnFS(url);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Url inválida", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if(codigo.equals("vieneVacío") && uriTexto.getText().toString().equals("Seleccionar archivo")){
            Toast.makeText(this, "Url inválida", Toast.LENGTH_LONG).show();
        }else {
            intentDiagrama.putExtra("uriDelArchivo", uri.toString());
            intentDiagrama.putExtra("codigo", codigo);
            startActivity(intentDiagrama);
            finish();
        }
    }

    public String escribirEnFS(String dirUrl) throws IOException {
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
