package com.example.proyectofinal.code2chart;

import com.mindfusion.diagramming.AnchorPattern;
import com.mindfusion.diagramming.ContainerNode;
import com.mindfusion.diagramming.ShapeNode;
import com.mindfusion.diagramming.SimpleShape;

import org.w3c.dom.Element;

import static com.mindfusion.diagramming.Shape.fromId;

public class NodoHandler {

    public NodoHandler() {
    }

    public void conversor(Element nodo,ShapeNode diagramaNodo) {
        String tipo = nodo.getAttribute("tipo");
        switch (tipo) {
            case "proceso":
                diagramaNodo.setShape(fromId("Rectangle"));
                break;
            case "inicio":
                diagramaNodo.setShape(fromId("Start"));
                break;

            case "decisión":

                diagramaNodo.setShape(fromId("Decision"));
                diagramaNodo.setAnchorPattern(AnchorPattern.fromId("Decision2In2Out"));
                diagramaNodo.setTag(true);

                break;

            case "fin":
                diagramaNodo.setShape(fromId("Terminator"));
                break;

            case "entrada":
                diagramaNodo.setShape(fromId("Save"));
                break;

            case "salida":
                diagramaNodo.setShape(fromId("Document"));
                break;
        }
    }

    public void conversorNodoContainer(Element nodo, ContainerNode diagramaNodo) {
        String tipo = nodo.getAttribute("tipo");

        switch (tipo) {

            case "bucle":

                diagramaNodo.setShape(SimpleShape.Rectangle);

                break;
        }

    }


}