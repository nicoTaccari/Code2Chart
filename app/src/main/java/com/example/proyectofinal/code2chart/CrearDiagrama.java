package com.example.proyectofinal.code2chart;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mindfusion.diagramming.ContainerNode;
import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramNode;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.FitSize;
import com.mindfusion.diagramming.LayeredLayout;
import com.mindfusion.diagramming.ShadowsStyle;
import com.mindfusion.diagramming.ShapeNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import parserUtils.ASTContainer;
import parserUtils.AbstractSyntaxTreeConverter;
import parserUtils.CCompiler;
import parserUtils.MyCVisitor;
import parserUtils.ParserToXmlAdapter;
import parserUtils.XmlBuilder;

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

    private DiagramView diagramView;
    private Diagram diagram;
    private String xmlName = null;
    private String xml = null;
    private String codigo;
    private HashMap<String, DiagramNode> nodeMap = new HashMap<String, DiagramNode>();
    private ArrayList<ContainerNode> listaDeBucles = new ArrayList<>();
    private ArrayList<ArrayList<Object>> listasDeNodosParaBucles = new ArrayList<>();
    private ArrayList<String> nodosDecision = new ArrayList<>();
    private ArrayList<String> nodosNoDecision = new ArrayList<>();
    private int cantidadTotalDeBucles = 0;
    private NodoHandler manejador = new NodoHandler();
    private RectF medidaDiagrama;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        diagramView = (DiagramView)findViewById(R.id.diag_view);
        diagram = diagramView.getDiagram();
        diagram.setShadowsStyle(ShadowsStyle.None);

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

    public void enviarOnClick(){
        Intent intentDiagrama = new Intent(this, Diagrama.class);
        intentDiagrama.putExtra("tituloMando", nombreTitulo.getText().toString());
        Bundle extras = new Bundle();
        extras.putParcelable("imagebitmap", bitmap);
        intentDiagrama.putExtras(extras);
        intentDiagrama.putExtra("xmlName", xmlName);
        if(!nombreUsuario.equals("Android")) {
            intentDiagrama.putExtra("autorMando", nombreUsuario);
        }else{
            intentDiagrama.putExtra("autorMando", nombreAutor.getText().toString());
        }
        startActivity(intentDiagrama);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.obtenerUri:
                showFileChooser();
                break;
            case R.id.generar:
                progressBar();

                if(validarEditText(listaEditText)){
                    if(archivoExistente(titulos, autores)){
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    generarCodigo();
                                    xml = magia(uri.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    loadGraph(xml, diagram);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                armarElLayout(diagram);

                                for(int i = listaDeBucles.size()-1; i >= 0; --i){
                                    for (int k = 0; k < listasDeNodosParaBucles.get(i).size(); ++k) {
                                        listaDeBucles.get(i).add((DiagramNode) listasDeNodosParaBucles.get(i).get(k));
                                    }
                                    armarElLayout(diagram);
                                }

                                bitmap = diagram.createImage();
                                enviarOnClick();
                            }
                        });
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

    public void generarCodigo(){
        codigo = "vieneVacío";
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

    public void progressBar(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialogprogreso, null);
        ProgressBar progressBar = (ProgressBar) mView.findViewById(R.id.progress);
        progressBar.setProgress(0);
        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    public String magia(String unaUri) throws Exception {
        xmlName = new String(Environment.getExternalStorageDirectory()+ File.separator+"xml");
        String filePreParse = "";
        if(codigo.equals("vieneVacío")) {
            Uri myUri = Uri.parse(unaUri);
            InputStream inputStream = getContentResolver().openInputStream(myUri);
            filePreParse = convertStreamToString(inputStream);
        }else{
            filePreParse = codigo;
        }
        CCompiler compiler = new CCompiler();
        AbstractSyntaxTreeConverter ast = compiler.compile(filePreParse);

        MyCVisitor visitor = new MyCVisitor();
        visitor.visit(ast,null);

        ParserToXmlAdapter adapter = new ParserToXmlAdapter();
        LinkedList<ASTContainer> list = adapter.getConvertedList(ast);

        XmlBuilder builder = new XmlBuilder(xmlName);
        builder.setXmlStructure();

        for(int i = 0 ; i<list.size(); ++i){
            builder.appendNode(list.get(i).getId(), list.get(i).getTipo(), list.get(i).getContent());
            if (list.get(i).getTipo() == "decisión") {
                builder.appendLink(list.get(i).getFather(), list.get(i).getId(), "decision");
            } else {
                builder.appendLink(list.get(i).getFather(), list.get(i).getId(), "");
            }
        }

        builder.build();

        String resultado = builder.getFile().getAbsolutePath();

        return resultado;
    }

    public String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public void loadGraph(String filepath, Diagram diagram) {

        RectF bounds = new RectF(0, 0, 20, 10);

        // load the graph xml
        Document document = loadXmlFile(filepath);
        Element root = document.getDocumentElement();

        //traigo todos los nodos, y todos los links
        NodeList nodes = root.getElementsByTagName("Node");
        NodeList links = root.getElementsByTagName("Link");

        RectF medidaIncial = new RectF(0, 0, 1000, 1000);
        diagram.setBounds(medidaIncial);

        dibujarLosNodosYClasificarlos(nodes, bounds);

        List<String> nodosYaLinkeados = new ArrayList<String>(); //para mapear de 1 sola vez
        //mapeo los links
        for (int i = 0; i < links.getLength(); ++i) {

            Element link = (Element) links.item(i);
            DiagramNode origin = nodeMap.get(link.getAttribute("origin"));
            if (!esNodoDecision(link.getAttribute("origin"), nodosDecision)) {
                //es un nodo comun
                DiagramNode target = nodeMap.get(link.getAttribute("target"));
                diagram.getFactory().createDiagramLink(origin, target);
                nodosYaLinkeados.add(link.getAttribute("origin"));
            } else {
                //primero me fijo si ya fueron mapeados sus links
                //entrando a esta parte significa que es un nodo de decision
                if (!nodosYaLinkeados.contains(link.getAttribute("origin"))) {
                    List<String> idsTarget = new ArrayList<String>();
                    idsTarget = obtenerNodosTargetsDadoUnNodoOrigenDeDecision(link.getAttribute("origin"), links);
                    DiagramNode target1 = nodeMap.get(idsTarget.get(0));
                    DiagramNode target2 = nodeMap.get(idsTarget.get(1));
                    diagram.getFactory().createDiagramLink(origin, target1).setText("SI");
                    diagram.getFactory().createDiagramLink(origin, target2).setText("NO");
                    nodosYaLinkeados.add(link.getAttribute("origin"));
                }

            }
        }

    }

    public Document loadXmlFile(String filepath){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(true);

        Document document = null;
        DocumentBuilder builder;
        try{
            File file = new File(filepath);
            builder = factory.newDocumentBuilder();
            document = builder.parse(file);
        }catch (Exception e){
            e.printStackTrace();
        }
        return document;
    }

    public void dibujarLosNodosYClasificarlos(NodeList nodes, RectF bounds){

        String idBucle = null;
        int enDonde = 0;

        for (int i = 0; i < nodes.getLength(); ++i) {

            Element node = (Element) nodes.item(i);
            //nuevocodigo
            String tipo = node.getAttribute("tipo");
            switch (tipo) {
                case "decisión":
                    nodosDecision.add(node.getAttribute("id"));
                    break;

                case "bucle":

                    nodosNoDecision.add(node.getAttribute("id"));
                    idBucle = node.getAttribute("id");

                    ContainerNode nodoBuclePrimitivo = dibujarUnNodoBucle(bounds, node, nodeMap);
                    listaDeBucles.add(nodoBuclePrimitivo);

                    listasDeNodosParaBucles.add(new ArrayList<>());

                    for(int j = i + 1; !((Element) nodes.item(j)).getAttribute("tipo").equals("finBucle"+idBucle); ++j){

                        Element nodoInside = (Element) nodes.item(j);

                        String tipoEnBucle = nodoInside.getAttribute("tipo");

                        switch (tipoEnBucle){
                            case "bucle":
                                ContainerNode nodoContainer  = dibujarUnNodoBucle(bounds, nodoInside, nodeMap);
                                listasDeNodosParaBucles.get(enDonde).add(nodoContainer);
                                listasDeNodosParaBucles.add(new ArrayList<>());
                                ++enDonde;
                                ++cantidadTotalDeBucles;
                                listaDeBucles.add(nodoContainer);
                                nodosNoDecision.add(nodoInside.getAttribute("id"));
                                break;

                            case "proceso":
                                ShapeNode nodoProceso = dibujarUnNodo(bounds, nodoInside, nodeMap);
                                listasDeNodosParaBucles.get(enDonde).add(nodoProceso);
                                nodosNoDecision.add(nodoInside.getAttribute("id"));
                                break;

                            case "decisión":
                                ShapeNode nodoDecision = dibujarUnNodo(bounds, nodoInside, nodeMap);
                                listasDeNodosParaBucles.get(enDonde).add(nodoDecision);
                                nodosDecision.add(nodoInside.getAttribute("id"));
                                break;

                            default:
                                --enDonde;
                                break;
                        }

                        i=j;

                    }

                    ++cantidadTotalDeBucles;
                    enDonde = cantidadTotalDeBucles;

                    break;

                default:
                    if (!node.getAttribute("tipo").matches(".*\\d+.*")) {
                        nodosNoDecision.add(node.getAttribute("id"));
                    }
                    break;
            }

            if (!node.getAttribute("tipo").matches(".*\\d+.*") && !tipo.equals("bucle")) {
                dibujarUnNodo(bounds, node, nodeMap);
            }
        }
    }

    public ShapeNode dibujarUnNodo(RectF bounds, Element node, HashMap<String, DiagramNode> nodeMapFuncion){

        ShapeNode diagramNode = diagram.getFactory().createShapeNode(bounds);

        //Convierte el "tipo" ubicado en el xml en la forma
        manejador.conversor(node, diagramNode);
        String idNodo = node.getAttribute("id");
        nodeMapFuncion.put(idNodo, diagramNode);
        diagramNode.setText(node.getAttribute("nombre"));

        //Clave para que se vea bien el texto dentro del nodo
        diagramNode.resizeToFitText(FitSize.KeepRatio);

        return diagramNode;
    }

    public ContainerNode dibujarUnNodoBucle(RectF bounds, Element node,  HashMap<String, DiagramNode> nodeMapFuncion){

        ContainerNode bucle = diagram.getFactory().createContainerNode(bounds);
        bucle.setAutoShrink(true);

        //Convierte el "tipo" ubicado en el xml en la forma
        manejador.conversorNodoContainer(node, bucle);
        String idNodo = node.getAttribute("id");
        nodeMapFuncion.put(idNodo, bucle );
        bucle.setEditedText(node.getAttribute("nombre"));

        return bucle;

    }

    public void armarElLayout(Diagram diagrama){
        //Conn esto, menciono que si bien tome un layout de Decision, tambien tengo que mapear todas las relaciones de cada
        //uno de los nodos, es decir si hay uno que es decision, necesariamente tengo que crear los 2 links de decision seguidos,
        //no uno, y luego otro.
        LayeredLayout layout = new LayeredLayout();
        layout.setLayerDistance(10);
        layout.setNodeDistance(25);
        layout.setStraightenLongLinks(true);
        layout.arrange(diagrama);

        medidaDiagrama = diagrama.getContentBounds(false, true);
        diagrama.setBounds(medidaDiagrama);

    }

    public boolean esNodoDecision(String idNodo, List<String> nodosDecision) {
        boolean decision = false;
        if (nodosDecision.contains(idNodo)){
            decision = true;
        }
        return decision;
    }

    public List<String> obtenerNodosTargetsDadoUnNodoOrigenDeDecision(String idNodoDecision, NodeList links) {

        List<String> nodosTarget = new ArrayList<String>();
        for (int i = 0; i < links.getLength(); ++i){
            Element link = (Element)links.item(i);
            String idorigen = link.getAttribute("origin");
            if (idorigen.equals(idNodoDecision)) {
                nodosTarget.add(link.getAttribute("target"));
            }
        }
        return nodosTarget;
    }

}
