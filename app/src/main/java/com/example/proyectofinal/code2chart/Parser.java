package com.example.proyectofinal.code2chart;

import android.support.v7.app.AppCompatActivity;

import java.util.Set;

/**
 * Created by Casa on 25/04/2017.
 */

public abstract class Parser extends AppCompatActivity {

    Set<String> palabrasReservadas;

    abstract int parse(String name);
}
