package com.example.proyectofinal.code2chart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mindfusion.diagramming.DecisionLayout;
import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramNode;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.FitSize;
import com.mindfusion.diagramming.ShapeNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Diagrama extends AppCompatActivity implements View.OnClickListener {

    private DiagramView diagramView;
    private Button guardar, descartar;
    private Diagram diagram;
    private ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagrama);

        diagramView = (DiagramView)findViewById(R.id.diag_view);

        descartar = (Button) findViewById(R.id.descartar);
        descartar.setOnClickListener(this);

        guardar = (Button) findViewById(R.id.guardar);
        guardar.setOnClickListener(this);

        imagen = (ImageView) findViewById(R.id.imagen);

        Bundle bundleDiagrama = getIntent().getExtras();
        if(bundleDiagrama != null){
            String nombreXml = bundleDiagrama.getString("imagenDiagrama");
            String nombreImagen = bundleDiagrama.getString("nombreImagen");
        }

        diagram = diagramView.getDiagram();

        loadGraph("SampleGraph.xml", diagram);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putString("diagram", diagramView.saveToString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            diagramView.zoomToFit();
        }

        //diagramView.setVisibility(View.INVISIBLE);

    }

    public void loadGraph(String filepath, Diagram diagram) {
        NodoHandler manejador = new NodoHandler();
        HashMap<String, DiagramNode> nodeMap = new HashMap<String, DiagramNode>();
        RectF bounds = new RectF(0, 0, 15, 8);

        // load the graph xml
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document xml = null;
        try {
            xml = builder.parse(getAssets().open(filepath));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element graph = (Element) xml.getFirstChild();

        // traigo todos los nodos, y todos los links
        NodeList nodes = graph.getElementsByTagName("Node");
        NodeList links = graph.getElementsByTagName("Link");

        List<String> nodosDecision = new ArrayList<String>(); //aca voy a storear los ids de todos los nodos que son decision
        List<String> nodosNoDecision= new ArrayList<String>();

        RectF medidaIncial = new RectF(0, 0, 500, 800);
        diagram.setBounds(medidaIncial);


        for (int i = 0; i < nodes.getLength(); ++i) {

            Element node = (Element) nodes.item(i);
            //nuevocodigo
            String tipo = node.getAttribute("tipo");
            switch (tipo) {
                case "decision":
                    nodosDecision.add(node.getAttribute("id"));
                    break;

                default:
                    nodosNoDecision.add(node.getAttribute("id"));
                    break;
            }

            ShapeNode diagramNode = diagram.getFactory().createShapeNode(bounds);
            //Convierte el "tipo" ubicado en el xml en la forma
            manejador.conversor(node, diagramNode);
            String idNodo = node.getAttribute("id");
            nodeMap.put(idNodo, diagramNode);
            diagramNode.setText(idNodo);
            diagramNode.setText(node.getAttribute("nombre"));
            //Clave para que se vea bien el texto dentro del nodo
            diagramNode.resizeToFitText(FitSize.KeepRatio);
        }

        List<String> nodosYaLinkeados = new ArrayList<String>(); //para mapear de 1 sola vez
        // mapeo los links
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

        // Conn esto, menciono que si bien tome un layout de Decision, tambien tengo que mapear todas las relaciones de cada
        //uno de los nodos, es decir si hay uno que es decision, necesariamente tengo que crear los 2 links de decision seguidos,
        //no uno, y luego otro.
        DecisionLayout layout = new DecisionLayout();
        layout.setHorizontalPadding(10);
        layout.setVerticalPadding(10);
        layout.arrange(diagram);

        RectF hola = diagram.getContentBounds(false, true);
        diagram.setBounds(hola);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.guardar:
                startSave();
                startActivityMain();
                break;
            case R.id.descartar:
                startActivityMain();
                break;

        }
    }

    public void startActivityMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startSave(){
        FileOutputStream fileOutputStream = null;
        File file = getDisc();
        if(!file.exists() && !file.mkdirs()){
            Toast.makeText(this, "Can´t create directory to save image", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "Img"+date+".jpg";
        String file_name = file.getAbsolutePath()+"/"+name;
        File new_file = new File(file_name);
        try{
            fileOutputStream = new FileOutputStream(new_file);
            Bitmap bitmap = diagramView.getDrawingCache(true);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            Toast.makeText(this, "Save image success",Toast.LENGTH_SHORT).show();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshGallery(new_file);
    }

    public void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    public File getDisc(){
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file, "Image Demo");
    }

    public static Bitmap viewToBitmap(View view, int width, int height){
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}
