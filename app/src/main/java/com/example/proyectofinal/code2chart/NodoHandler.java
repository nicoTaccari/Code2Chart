package com.example.proyectofinal.code2chart;

import android.graphics.Path;

import com.mindfusion.diagramming.AnchorPattern;
import com.mindfusion.diagramming.ElementTemplate;
import com.mindfusion.diagramming.LineTemplate;
import com.mindfusion.diagramming.Shape;
import com.mindfusion.diagramming.ShapeNode;

import org.w3c.dom.Element;

public class NodoHandler {

    public NodoHandler() {
    }

    public void conversor(Element nodo,ShapeNode diagramaNodo) {
        String tipo = nodo.getAttribute("tipo");
        switch (tipo) {
            case "proceso":
                diagramaNodo.setShape(Shape.fromId("Rectangle"));
                break;
            case "inicio":
                diagramaNodo.setShape(Shape.fromId("Start"));
                break;

            case "decision":

                diagramaNodo.setShape(Shape.fromId("Decision"));
                diagramaNodo.setAnchorPattern(AnchorPattern.fromId("Decision2In2Out"));
                diagramaNodo.setTag(true);

                break;

            case "fin":
                diagramaNodo.setShape(Shape.fromId("Terminator"));
                break;

            case "entrada":
                diagramaNodo.setShape(Shape.fromId("Save"));
                break;

            case "salida":
                diagramaNodo.setShape(Shape.fromId("Save"));
                break;

            case "bucle":
                Shape nodoBucle = new Shape(

                        Shape.fromId("Rectangle").getOutline(),

                        // rect part
                        new ElementTemplate[]{
                            new LineTemplate(0, 30, 100, 30)
                        },

                        null,


                        Path.FillType.WINDING, "bucle");

                diagramaNodo.setShape(Shape.fromId("bucle"));

                break;

        }

    }

}