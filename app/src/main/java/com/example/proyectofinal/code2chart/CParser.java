package com.example.proyectofinal.code2chart;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Casa on 25/04/2017.
 */

public class CParser extends Parser {

    @Override
    int parse(String name) {
        File file = new File(name);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //Eat it
            return -1;
        }

        return 0;
    }
}
