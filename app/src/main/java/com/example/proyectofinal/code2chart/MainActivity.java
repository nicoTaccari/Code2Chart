package com.example.proyectofinal.code2chart;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.AndroidCharacter;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //atributos para el drawer nav panel
    private String[] pruebas;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private android.support.v7.app.ActionBarDrawerToggle actionBarDrawerToggle;
    private CharSequence title;
    private CharSequence drawertitle;

    private List<String> item = new ArrayList<String>();
    private ListView listaDeArchivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = drawertitle = getTitle();
        pruebas = getResources().getStringArray(R.array.nombres);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer);

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, R.layout.item_prueba, pruebas);
        listView.setAdapter(itemsAdapter);
        // seteo de listeners para los botones del menu lateral
        listView.setOnItemClickListener(new DrawerItemClickListener());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

        actionBarDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(title);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(drawertitle);
                invalidateOptionsMenu();
            }
        };
        //se setea el menu como listener de boton
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // todo aca hay que codear otros botones de la barra que se quieran agregar

        return super.onOptionsItemSelected(item);
        }


        /*File nuevaCarpeta = new File(getFilesDir(), "Code2Chart");
        if(!nuevaCarpeta.exists()) {
            nuevaCarpeta.mkdirs();
        } else {
            try {
                File gpxfile = new File(nuevaCarpeta, "Guachoo");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append("TEXTO DE PRUEBA");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File[] files = nuevaCarpeta.listFiles();

        for (int i = 0; i < files.length; i++)    {
            File file = files[i];
            if (file.isDirectory()) {
                item.add(file.getName() + "/");
            } else {

                item.add(file.getName());
            }
        }

        ListView listOpciones = (ListView) findViewById(R.id.listaDeArchivos);
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listOpciones.setAdapter(fileList);

        listOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra("filename", item.get(position));
                setResult(RESULT_OK, data);

            }
        });*/

    }

