package com.example.proyectofinal.code2chart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ListView listaDeArchivos;
    private EditText dato;
    private CarpetasAdapter adapterCarpetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CrearDiagrama.class);
                MainActivity.this.startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //seteo listview de archivos
        listaDeArchivos = (ListView) findViewById(R.id.listaDeArchivos);
        listarArchivos(getFilesDir());

        listaDeArchivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.buscar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterCarpetas.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_alert_window,null);
        dato = (EditText) view.findViewById(R.id.nombreNuevaCarpeta);
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nuevaCarpeta) {
            AlertDialog.Builder builderNuevaCarpeta = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Nueva Carpeta")
                    .setView(view)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            crearCarpeta(dato.getText().toString());
                            listarArchivos(getFilesDir());
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .setCancelable(false);
            AlertDialog alertDialogCarpeta = builderNuevaCarpeta.create();
            alertDialogCarpeta.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /*----------METODOS PROPIOS------------------------*/
    /*----------Listar los archivos por tipo----------*/
    private void listarArchivos(File dir){
        int cantidadDeArchivos = dir.listFiles().length;
        File[] files = dir.listFiles();
        ArrayList<FileComposite> carpetasYArchivos = new ArrayList<>();
        ArrayList<Archivo> archivos = new ArrayList<>();
        if(cantidadDeArchivos != 0){
            this.setearAdapter(files,cantidadDeArchivos,carpetasYArchivos);
        }
        adapterCarpetas = new CarpetasAdapter(this, carpetasYArchivos);
        listaDeArchivos.setAdapter(adapterCarpetas);
    }

    public void setearAdapter(File[] files, int cantidadDeArchivos, ArrayList<FileComposite> carpetasYArchivos){
        for (int i = 0; i < cantidadDeArchivos; i++) {
            if (files[i].isDirectory()) {
                carpetasYArchivos.add(new Carpeta(/*files[i].getParent() + "/" +
                        */files[i].getName() + "/", R.drawable.ic_folder));
            } else {
                carpetasYArchivos.add(new Archivo(/*files[i].getParent() + "/" +*/ files[i].getName(),
                        R.drawable.ic_file));
            }
        }
    }

    private void crearCarpeta(String nombre){
        File nuevaCarpeta = new File(getFilesDir(), nombre);
        if(!nuevaCarpeta.exists()) {
            nuevaCarpeta.mkdirs();
            Toast.makeText(getApplicationContext(), "Carpeta creada exitosamente",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Ya existe una carpeta con el mismo nombre",Toast.LENGTH_SHORT).show();
        }
    }
/*
    public void abrirArchivo(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
*/
}
