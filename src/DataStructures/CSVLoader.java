/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;


import java.io.BufferedReader; 
import java.io.IOException; 
import java.nio.charset.StandardCharsets; 
import java.nio.file.Files; 
import java.nio.file.Path; 
import java.nio.file.Paths; 
import java.util.ArrayList; 
import java.util.List; 

import Math.Matrix;

/**
 *
 * @author kostis
 */
public class CSVLoader {
    
    public static ArrayList<String[]> readCSV(String filename){
        Path pathToFile = Paths.get(filename);
        ArrayList result = null;
        try (
            BufferedReader br = Files.newBufferedReader(pathToFile,StandardCharsets.US_ASCII)
        ){
            result = new ArrayList();
            String line = br.readLine();
            while (line!=null){
                String[] row = line.split(",");
                result.add(row);
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result;
    }
    
    public static void main(String[] args){
        String filename="C:\\Users\\kostis\\Desktop\\junk.txt";
        System.out.println("Lets read");
        ArrayList <String[]> rows = readCSV(filename);
        for (String[] row : rows){
            for (String field : row){
                System.out.print(field+" - ");
            }
            System.out.println();
        }        
    }    
}
