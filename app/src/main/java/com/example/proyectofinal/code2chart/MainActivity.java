package com.example.proyectofinal.code2chart;

import android.content.Intent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private String[] pruebas;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private List<String> item = new ArrayList<String>();
    private ListView listaDeArchivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pruebas = getResources().getStringArray(R.array.nombres);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer);

        //listView.setAdapter(new ArrayAdapter<String>(this,R.layout., pruebas));
        // Set the list's click listener
        listView.setOnItemClickListener(new DrawerItemClickListener());

        File nuevaCarpeta = new File(getFilesDir(), "Code2Chart");
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
        });

    }

}
