package com.example.proyectofinal.code2chart;

import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mindfusion.diagramming.Brush;
import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramNode;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.Shape;
import com.mindfusion.diagramming.ShapeNode;
import com.mindfusion.diagramming.ShapeNodeStyle;
import com.mindfusion.diagramming.Theme;

import java.util.ArrayList;

public class DiagramActivity extends AppCompatActivity {

    DiagramView diagView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_view);
    }
}
