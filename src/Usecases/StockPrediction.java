/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Usecases;

import MachineLearning.MultilayerNetwork;
import Math.Vector;
import java.util.ArrayList;
import java.util.Random;
import static DataStructures.CSVLoader.readTextFile;

/**
 *
 * @author kostis
 */
public class StockPrediction {
    
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
    
    public static class Agent{
        public int assets=0;
        public double funds=0;
        public int buys=0;
        public int sells=0;
        public Agent(){
            assets=0;
        }
        public void buy(Double price){
            assets++;
            buys++;
            funds-=price;
        }
        
        public void sell(Double price){
            assets--;
            sells++;
            funds+=price;
        }
        public void show(){
            System.out.printf("this agent has\n");
            System.out.printf("\tbuys:\t%d\n",buys);
            System.out.printf("\tsels:\t%d\n",sells);
            System.out.printf("\tAssets:\t%d\n",assets);
            System.out.printf("\tFunds:\t%.2f\n",funds);
        }
        
    } 
    
    public static void main(String args[]){
        String filename="C:\\Users\\kostis\\Downloads\\temp\\HistoricalQuotes.csv";
        ArrayList <Double> data = getData(filename);
        int N=data.size();
        Agent smith = new Agent();
        int window=10;
        MultilayerNetwork neu = new MultilayerNetwork(window,5,2,1);
        ArrayList <Double> pattern_arr = new ArrayList();
        Random rnd = new Random();
        
        double first_price = data.get(0);
        double last_price = data.get(N-1);
        
        double temp=0;
        for (Double val :data)
            temp+=val;
        for(int i=0;i<N;i++)
            data.set(i, data.get(i)-temp/N);
        
        double output=.5;
        Vector pattern = new Vector(window);
        for (int i=0;i<N;i++){
            double price = data.get(i);
            double next = data.get(Math.min(i+1, N-1));
            /*
            if (output>0.5 && price > data.get(i-1)){
                neu.train(pattern, new Vector(1.0));
            }else{
                neu.train(pattern, new Vector(0.0));
            }
            */
            pattern_arr.add(price);
            pattern = new Vector(pattern_arr);
            pattern.multiply(1.0/pattern.sum().getPrimitive());
            
            //pattern.show();
            output = 1.0;
            //output = neu.classify(pattern).get(0);
            //output = (price<next)?1.0:0.0;
            //output = rnd.nextDouble();
            if (output>0.5){
                smith.buy(price);
            }else{
                smith.sell(price);
            }
            //smith.show();
            //System.out.printf("\tEquity:\t%.2f\t\t(%.2f)\t%.2f\n",smith.funds+smith.assets*price,price,next);
            System.out.printf("%.2f\n",smith.funds+smith.assets*price);
            if (pattern_arr.size()>window)
                pattern_arr.remove(0);
        }
        //last_price=first_price=100;
        smith.funds+=smith.assets*last_price;
        smith.assets=0;
        smith.show();
        System.out.printf("%.2f\t%.2f\t%.2f\n",first_price,last_price,last_price-first_price);
        System.out.printf("%.2f\n",smith.funds/(smith.buys+smith.sells));
        
        
        
    }
}
