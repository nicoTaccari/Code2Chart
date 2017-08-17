package com.example.proyectofinal.code2chart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mindfusion.diagramming.DecisionLayout;
import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramNode;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.FitSize;
import com.mindfusion.diagramming.HtmlBuilder;
import com.mindfusion.diagramming.ShapeNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

        Bundle bundleDiagrama = getIntent().getExtras();
        if(bundleDiagrama != null){
            String nombreXml = bundleDiagrama.getString("imagenDiagrama");
            String nombreImagen = bundleDiagrama.getString("nombreImagen");

            loadGraph("SampleGraph.xml", diagram);

            HtmlBuilder creador = new HtmlBuilder(diagram);
            try {
                String text = creador.createImageHtml("index.html","Code2Chart",getFilesDir().toString(), "./diagrama.png", "png");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        //finish();

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
        List<String> nodosNoDecision = new ArrayList<String>();


        RectF medida = new RectF(0, 0, 100, 100);
        
        diagram.setBounds(medida);
        /*AutoResize ajuste = null;
        diagram.setAutoResize(ajuste.AllDirections);*/

        //diagram.resizeToFitItems(10);

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
    public void finish(){
        Intent data = new Intent();
        data.putExtra("resultado","correcto");
        setResult(RESULT_OK, data);
        super.finish();
    }

    private void saveBitmap(Diagram diagrama, String nombre) {
        File sd = getFilesDir();
        File dest = new File(sd, nombre + ".png");

        Bitmap bitmap = diagView.getDrawingCache(true);

        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
