package com.example.proyectofinal.code2chart;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
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
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AbsListView.MultiChoiceModeListener {

    private ListView listaDeArchivos;
    private ArchivosAdapter adapterArchivos;
    private ArrayList<Archivo> toDeleteItems;
    private ArrayList<Archivo> archivos;

    private SearchView searchView;

    private String titulo, autor;

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

        /*seteo el navigation drawer*/
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*inicializo los arrays de archivos*/
        archivos = new ArrayList<>();
        toDeleteItems = new ArrayList<>();

        //seteo listview de archivos
        listaDeArchivos = (ListView) findViewById(R.id.listaDeArchivos);
        listarArchivos(getFilesDir(),archivos);

        /*listener para los clicks*/
        listaDeArchivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Archivo arch = (Archivo) listaDeArchivos.getItemAtPosition(position);
                Uri imagenUri = arch.getUri();
                Intent intent = new Intent(getApplicationContext(), ImagenActivity.class);
                intent.putExtra("uri", imagenUri.toString());
                startActivity(intent);
            }
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
            String nombreArchivo = files[i].getName();

            StringTokenizer st = new StringTokenizer(nombreArchivo, ".");
            String tituloArchivo = st.nextToken();
            String autorArchivo = st.nextToken();

            Archivo archivo = new Archivo(tituloArchivo, autorArchivo);
            archivo.setUri(Uri.fromFile(getFileStreamPath(nombreArchivo)));

            archivos.add(archivo);
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
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

}
