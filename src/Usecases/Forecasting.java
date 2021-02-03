/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Usecases;

import Math.Vector;
import MachineLearning.MultilayerNetwork;
import java.util.ArrayList;
import static DataStructures.CSVLoader.readTextFile;


/**
 *
 * @author kostis
 */
public class Forecasting {
    public static ArrayList <Double> getData(String filename){
        //System.out.println("Lets read");
        ArrayList <String[]> rows = readTextFile(filename);
        ArrayList <Double> data = new ArrayList();
        for (String[] row : rows){
            if (row.length<2)
                continue;
            Double val = null;
            try{
                val=Double.parseDouble(row[1].replace("\"", ""));
                data.add(val);
            }catch (NumberFormatException e){
            }           
        }   
        return data;
    }
    
    public static void main(String args[]){
        String filename="C:\\Users\\kostis\\Downloads\\temp\\HistoricalQuotes.csv";
        ArrayList <Double> data_arr = getData(filename);
        int N=data_arr.size();
        int W=20;
        Vector data = new Vector(data_arr);
        double normalizer = data.max().getPrimitive();
        data.multiply(1.0/normalizer);
        
        double[] memory = new double[W];
        int mem_i=0;
        MultilayerNetwork neu= new MultilayerNetwork(W,10,3,1);
        double forecast=0;
        Vector pattern = new Vector(W);
        Vector neural = new Vector(N);
        
        Vector naive = new Vector(N);
               
        for(int i=0;i<W;i++)
            memory[i]=0;
        
        for (int i=0;i<N;i++){
            double value = data.get(i).getPrimitive();
            
            //  Naive
            int prev_index = Math.max(0,i-1);
            naive.set(i,data.get(prev_index));
            
            
            // NN approach
            
            neu.train(pattern, new Vector(value));
            
            memory[mem_i++]=value;
            mem_i%=W;
            
            for(int j=0;j<W;j++){
                pattern.set(j,memory[(mem_i+j)%W]);
            }
            forecast = neu.classify(pattern).get(0).getPrimitive();
            neural.set(i, forecast);
        }
        
        Vector naive_err = data.diff(naive);
        for (int i=0;i<N;i++){
            System.out.print(data.get(i).getPrimitive()*normalizer+", ");
            System.out.print(naive.get(i).getPrimitive()*normalizer+", ");
            System.out.print(neural.get(i).getPrimitive()*normalizer+"\n");
        }
        
        System.out.println("MSE:\t"+naive_err.getNorm());
    }
}
