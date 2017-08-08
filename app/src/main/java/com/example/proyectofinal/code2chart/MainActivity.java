package com.example.proyectofinal.code2chart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AbsListView.MultiChoiceModeListener {

    private ListView listaDeArchivos;
    private ArchivosAdapter adapterArchivos;
    private ArrayList<Archivo> toDeleteItems;
    private ArrayList<Archivo> archivos;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CrearDiagrama.class);
            startActivity(intent);
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*seteo el navigation drawer*/
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*inicializo los arrays de archivos*/
        archivos = new ArrayList<>();
        toDeleteItems = new ArrayList<>();

        /*Crear archivo*/
        try {
            openFileOutput("Carlos", 0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //seteo listview de archivos
        listaDeArchivos = (ListView) findViewById(R.id.listaDeArchivos);
        listarArchivos(getFilesDir(),archivos);

        /*listener para los clicks*/
        listaDeArchivos.setOnItemClickListener((parent, view, position, id) -> {
            Archivo arch = (Archivo) listaDeArchivos.getItemAtPosition(position);
            //TODO implentar abrir archivo
            arch.abrir(MainActivity.this.getApplicationContext(), listaDeArchivos);
        });

        /*listener para los long clicks*/
        listaDeArchivos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listaDeArchivos.setMultiChoiceModeListener(this);

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
        searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterArchivos.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*----------METODOS PROPIOS------------------------*/
    /*----------Listar los archivos por tipo----------*/
    public void listarArchivos(File dir,ArrayList<Archivo> archivos) {
        int cantidadDeArchivos = dir.listFiles().length;
        File[] files = dir.listFiles();

        if (cantidadDeArchivos != 0) {
            this.setearAdapter(files, cantidadDeArchivos, archivos);
        }
        adapterArchivos = new ArchivosAdapter(this, archivos);
        listaDeArchivos.setAdapter(adapterArchivos);
    }

    public void setearAdapter(File[] files, int cantidadDeArchivos, ArrayList<Archivo> archivos) {
        for (int i = 0; i < cantidadDeArchivos; i++) {
            archivos.add(new Archivo(files[i].getName(),R.drawable.ic_file));
        }
    }


    /*---------Ocultar teclado y cerrar search cuando se toca en la pantalla---------*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean handleReturn = super.dispatchTouchEvent(ev);

        View view = getCurrentFocus();

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (view instanceof EditText) {
            View innerView = getCurrentFocus();

            if (ev.getAction() == MotionEvent.ACTION_UP &&
                    !getLocationOnScreen(innerView).contains(x, y)) {

                InputMethodManager input = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }

        searchView.setQuery("", false);
        searchView.setIconified(true);

        return handleReturn;
    }

    protected Rect getLocationOnScreen(View mEditText) {
        Rect mRect = new Rect();
        int[] location = new int[2];

        mEditText.getLocationOnScreen(location);

        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + mEditText.getWidth();
        mRect.bottom = location[1] + mEditText.getHeight();

        return mRect;
    }

    /*----------Metodos para implementar seleccion multiple de archivos---------*/
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.overlay_toolbar, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()){
            case (R.id.action_delete):
                for (Archivo arch: toDeleteItems) {
                    archivos.remove(arch);
                }
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        toDeleteItems.clear();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            listaDeArchivos.setSelector(R.drawable.selector);
            listaDeArchivos.setSelected(true);
            toDeleteItems.add((Archivo) adapterArchivos.getItem(position));
        } else {
            toDeleteItems.remove(adapterArchivos.getItem(position));
        }
    }

    public void createFile(String sFileName, String sBody){
        try{
            File root = new File(getFilesDir(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
