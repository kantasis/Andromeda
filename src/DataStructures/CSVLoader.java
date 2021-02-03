/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;


import Core.Logger;
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
    
    /**
    * Open a file and load the lines in a list
    * @param filename the filename of the file to be opened
    * @return a list of strings, one for each line
    */
    public static ArrayList<String> readTextFile(String filename){
        Path pathToFile = Paths.get(filename);
        ArrayList result = null;
        try (
            BufferedReader br = Files.newBufferedReader(pathToFile,StandardCharsets.US_ASCII)
        ){
            result = new ArrayList();
            
            String line = br.readLine();
            while (line!=null){
                //String[] row = line.split(",");
                result.add(line);
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result;
    }
    
    public static Matrix readCSV(String filename, String delimiter, boolean skipMalformed){
        ArrayList <String> rows = readTextFile(filename);
        int idx = 0;
        int N = rows.size();
        int M = rows.get(0).split(delimiter).length;
        Matrix result = new Matrix(N,M);
        boolean[] validRows = new boolean[N];
        result.getSize().show();
        for (int row=0;row<N;row++){
            String line = rows.get(row);
            validRows[row]=true;
            String[] fields = line.split(delimiter);
            if (fields.length != M){
                if (skipMalformed){
                    Logger.log("Skipping: The following line has %d fields, not %d", fields.length,M);
                    Logger.log(line);
                    validRows[row]=false;
                    continue;
                }
            }
                
            for (int col=0;col<M;col++){
                String field = fields[col];
                try{
                    double value = Double.parseDouble(field);
                    result.set(row, col, value);
                }catch (NumberFormatException e){
                    Logger.log("Skipping: The following field is not numeric:\t[%s]", field);
                    validRows[row]=false;
                    break;
                }
            }            
        }
        
        return result.pickRows(validRows);
    }
    
    public static void main(String[] args){
        String filename="C:\\Users\\kostis\\Dropbox\\iris_numeric.csv";
        
        Matrix x = readCSV(filename,",",true);
        x.getSize().show();
    }    
}
