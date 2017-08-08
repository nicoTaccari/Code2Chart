package com.example.proyectofinal.code2chart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mindfusion.diagramming.AutoResize;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Diagrama extends AppCompatActivity {

    DiagramView diagView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagrama);

        diagView = (DiagramView)findViewById(R.id.diag_view);

        Diagram diagram = diagView.getDiagram();

        Bundle bundleXml = getIntent().getExtras();
        if(bundleXml != null){
            String nombreXml = bundleXml.getString("imagenDiagrama");
        }

        loadGraph("SampleGraph.xml", diagram);

        File imagenDiagrama = saveBitmap(diagram, "Ejemplo");

        Intent data = new Intent();
        data.putExtra("resultadoImagen",imagenDiagrama);
        setResult(Activity.RESULT_OK,data);
        finish();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putString("diagram", diagView.saveToString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("diagram")){
            diagView.loadFromString(savedInstanceState.getString("diagram"));
        }
    }

    void loadGraph(String filepath, Diagram diagram) {
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
        List<String> nodosNoDecision = new ArrayList<String>();


        /*RectF medida = new RectF(0, 0, 800, 800);
        diagram.setBounds(medida);*/
        AutoResize ajuste = null;
        diagram.setAutoResize(ajuste.AllDirections);

        DiagramView view = new DiagramView(this);
        view.setDiagram(diagram);

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

    }

    boolean esNodoDecision(String idNodo, List<String> nodosDecision) {
        boolean decision = false;
        if (nodosDecision.contains(idNodo)){
            decision = true;
        }

        return decision;
    }

    List<String> obtenerNodosTargetsDadoUnNodoOrigenDeDecision(String idNodoDecision, NodeList links) {

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
    public void finish(){
        Intent respuesta = new Intent();
        setResult(RESULT_OK, respuesta);
        super.finish();
    }

    private File saveBitmap(Diagram diagrama, String nombre) {
        String storageDirectory = this.getFilesDir().toString();
        OutputStream outStream = null;

        File file = new File(nombre + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(storageDirectory, nombre + ".png");
            Log.e("file exist", "" + file + ",Bitmap= " + nombre);
        }

        int cantidad = getFilesDir().listFiles().length;

        try {
            // make a new bitmap from your file
            Bitmap imagenDiagrama = diagrama.getBackgroundImage();

            outStream = new FileOutputStream(file);
            imagenDiagrama.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }

}
