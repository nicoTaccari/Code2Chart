package com.example.proyectofinal.code2chart;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AbsListView.MultiChoiceModeListener, GoogleApiClient.OnConnectionFailedListener {

    private ListView listaDeArchivos;
    private ArchivosAdapter adapterArchivos;
    private ArrayList<Archivo> toDeleteItems;
    private ArrayList<Archivo> archivos;

    private Button logOutButton;

    private SearchView searchView;

    private GoogleApiClient googleApiClient;
    private SignInButton signInButton;
    public static final int SIGN_IN_CODE = 777;
    private ImageView imagenUsuario;
    private TextView nombreUsuario, correoUsuario;

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
                startActivity(intent);
                finish();
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
        View hView = navigationView.getHeaderView(0);
        imagenUsuario = (ImageView) hView.findViewById(R.id.imagenUsuario);
        nombreUsuario = (TextView) hView.findViewById(R.id.nombreUsuario);
        correoUsuario = (TextView) hView.findViewById(R.id.correoUsuario);

        /*GoogleLogin*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.googleLogin);
        logOutButton = (Button) findViewById(R.id.googleLogout);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
                logOutButton.setVisibility(View.VISIBLE);
                signInButton.setVisibility(View.INVISIBLE);
            }
        });
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
                signInButton.setVisibility(View.VISIBLE);
                logOutButton.setVisibility(View.INVISIBLE);
            }
        });


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

        switch (item.getItemId()){
            case R.id.aboutUs:

                break;
            default:
                break;
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
                    File deletef = new File(arch.getUri().getPath());
                    deletef.delete();
                    archivos.remove(arch);
                }
                Toast.makeText(this, "Eliminación Correcta", Toast.LENGTH_SHORT).show();
                mode.finish();
                return true;
            case (R.id.action_share):
                ArrayList<Uri> uris = new ArrayList<>();
                Intent shareintent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                shareintent.setType("image/png");
                for(Archivo arch: toDeleteItems){
                    try {
                        enviar(arch.getUri(), uris);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                shareintent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                startActivity(Intent.createChooser(shareintent, "Compartir"));
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    public void enviar(Uri unaUri, ArrayList<Uri> uris) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), unaUri);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, unaUri.toString());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        OutputStream outstream;
        try {
            outstream = getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        uris.add(uri);
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

    /*GoogleLogin*/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            nombreUsuario.setText(account.getDisplayName());
            correoUsuario.setText(account.getEmail());
            Glide.with(this).load(account.getPhotoUrl()).into(imagenUsuario);
        }else {
            Toast.makeText(this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
            signInButton.setVisibility(View.VISIBLE);
            logOutButton.setVisibility(View.INVISIBLE);
        }
    }

    private void logOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess()){
                    nombreUsuario.setText("Android");
                    correoUsuario.setText("android@android.com");
                    imagenUsuario.setImageResource(R.drawable.common_google_signin_btn_icon_light);
                }else{
                    Toast.makeText(getApplicationContext(),"No se pudo cerrar sesion",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
