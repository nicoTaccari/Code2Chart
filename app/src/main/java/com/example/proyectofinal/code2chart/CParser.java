package com.example.proyectofinal.code2chart;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Created by Casa on 25/04/2017.
 */

public class CParser extends Parser {


    public CParser(){
        String[] palabras = new String[]{"char", "const", "double", "enum", "extern", "float","int", "long", "register", "short", "signed",
                                            "sizeof", "static", "struct", "typedef", "union", "unsigned", "void", "volatile"};
        palabrasReservadas = new HashSet<String>(Arrays.asList(palabras));
    }

    /*regex para tipo de datos de C*/
    Pattern identificador = Pattern.compile("[_a-zA-Z]\\w{0,30}");//testeado
    Pattern opAritmetico = Pattern.compile("(\\+|-|\\*|/|%|\\+\\+|--)");//testeado
    Pattern opRelacional = Pattern.compile("([=|!|>|<]=|>|<)");//testeado
    Pattern opLogico = Pattern.compile("(&&|\\|\\||!)");//testeado
    Pattern opBitwise = Pattern.compile("(&|\\^|<<|>>|\\|\\|)");//testeado
    Pattern opDeAsignacion = Pattern.compile("[\\+|-|\\*|/|%]?="); //testeado
    Pattern opTernario = Pattern.compile("(.*)(\\?)(.*)(\\:)(.*)");  //testeado
    Pattern opEspecial= Pattern.compile("(sizeof|&|\\*)");//testeado
    Pattern comentario = Pattern.compile("//");//testeado



    Pattern ifStruct = Pattern.compile("(if)(\\s)*(\\((.*)\\))((\\s)*\\{(?si)(.*)\\}|(\\s)+(.*))");                                         //testeado
    Pattern forStruct = Pattern.compile("(for)(\\s)*(\\()(.*)(;)(.*)(;)(.*)(\\))((\\s)*\\{(.*)\\}|(\\s)+(.*))");                            //testeado
    Pattern dowhileStruct = Pattern.compile("(do)((\\s)+(.*)(\\s)+|(\\s)*(\\{)(.*)(\\})(\\s)*)(while)([\\s]*)(\\()(.*)(\\))");              //testeado
    Pattern whileStruct = Pattern.compile("(while)(\\s)*\\((.*)\\)((\\s)*\\{(.*)\\}|(\\s)+(.*))");                                          //testeado

    @Override
    int parse(String name) {
        File file = new File(name);

        StringBuilder text = new StringBuilder(), decodedText = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String [] palabra;

            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    text.append(line);
                    text.append('\n');

                    palabra = line.split(" ");
                    if (palabra[0].startsWith("//")) {
                        decodedText.append("Comentario\n");

                    } else if (palabra[0].startsWith("/*")) {
                        while (!line.contains("*/")) line = br.readLine();
                        decodedText.append("Comentario de varias líneas\n");

                    } else if (palabrasReservadas.contains(palabra[0])) {
                        if (palabra[1].contains("(") || palabra[2].contains("(")){
                            decodedText.append("Declaración de función\n");

                        } else {
                            decodedText.append("Declaración de variable\n");

                        }
                    } else {
                        decodedText.append("Not yet\n");

                    }
                }
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
