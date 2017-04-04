package com.example.proyectofinal.code2chart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File nuevaCarpeta = new File(getFilesDir(), "Code2Chart");
        if(!nuevaCarpeta.exists()) {
            nuevaCarpeta.mkdirs();
        }

    }
}
