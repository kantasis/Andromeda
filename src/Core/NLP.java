/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import Graphics.Figure;
import MachineLearning.NeuralNetworks.NeuronLayer;
import MachineLearning.NeuralNetworks.RNNLayer;
import Math.Operatables.Real;
import Math.Vector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author kostis
 */
public class NLP {
    

    public static final int REPS_MAX=10;
    
    
    
    public static ArrayList<String> readDictionary(String filename){
        BufferedReader reader;
        ArrayList<String> result = new ArrayList();
        try {
            String line;
            reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine())!= null) {
                result.add(line);
            }
            reader.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return result;
    }
    
    public static int pick(double[] probs){
        Random rnd = new Random();
        double v = rnd.nextDouble();
        //System.out.printf("[[%f]]\n",v);
        double sum = 0;
        for (int i=0;i<probs.length;i++){
            sum+=probs[i];
            if (v<sum)
                return i;
        }
        
        return probs.length-1;
    }
    
    
    public static String produce(NeuronLayer neurons){
        System.out.printf("Producing...\t");
        String result ="";
        CharFeatureExtractor featmachine = new CharFeatureExtractor();
        
        Vector pattern_vector = CharFeatureExtractor.getStartPattern();
        Vector prevPattern_vector = new Vector(CharFeatureExtractor.LETTERS);
        Vector output_vector;
        result+=featmachine.pattern2data(pattern_vector);
        
        while (true){
            output_vector = neurons.classify(pattern_vector.merge(prevPattern_vector));
            Vector probs = (Vector) output_vector.div(output_vector.sum());
            
            int idx = pick(probs.getPrimitiveWeights());
            char c = featmachine.index2char(idx); 
            result += c;
            
            prevPattern_vector=pattern_vector;
            pattern_vector=featmachine.data2pattern(c);
            
            if (idx==CharFeatureExtractor.STOP || result.length()>=50)    break;
        }
        return result;
    }
    
    public static double[][] getMarkovChain(String filename){
        if (filename=="")
            filename="C:\\Users\\kostis\\Downloads\\temp\\wordlist.txt";
        ArrayList<String> words = readDictionary(filename);
        double[][] markov = new double[CharFeatureExtractor.LETTERS][CharFeatureExtractor.LETTERS];
        
        for (String word : words){
            int prev_idx;
            int curr_idx=CharFeatureExtractor.START;
            for (char c : word.toCharArray()){
                prev_idx=curr_idx;
                curr_idx=CharFeatureExtractor.char2index(c);
                markov[prev_idx][curr_idx]++;
            }
            markov[curr_idx][CharFeatureExtractor.STOP]++;
        }
        
        for (int input_idx=0; input_idx<CharFeatureExtractor.LETTERS;input_idx++){
            double sum=0;
            for (int output_idx=0; output_idx<CharFeatureExtractor.LETTERS;output_idx++){
                markov[input_idx][output_idx]=markov[input_idx][output_idx]*markov[input_idx][output_idx];
                sum+=markov[input_idx][output_idx];
            }
            for (int output_idx=0; output_idx<CharFeatureExtractor.LETTERS;output_idx++){
                markov[input_idx][output_idx]/=sum;
            }
        }
        
        for (int i=0;i<100;i++){
            String product="";
            int idx=CharFeatureExtractor.START;
            while (product.length()<=30 && idx!=CharFeatureExtractor.STOP ){
                idx=pick(markov[idx]);
                product+=CharFeatureExtractor.index2char(idx);
            }
            System.out.printf("\t%s\n",product);
        }
        
        return markov;
    }
    
    public static Vector softmax(Vector x){
        Vector temp = x.copy();
        for (int i=0;i<temp.getLength();i++){
            temp.set(i, Math.exp(x.get(i).getPrimitive()));
        }
        temp.div(temp.sum());
        return temp;
    }
    
    public static void main(String[] args){
        String filename = "C:\\Users\\kostis\\Downloads\\temp\\wordlist.txt";
        ArrayList<String> dictionary = readDictionary(filename);
        RNNLayer rnn = new RNNLayer(CharFeatureExtractor.LETTERS,CharFeatureExtractor.LETTERS);
        CharFeatureExtractor feature_extractor = new CharFeatureExtractor();
        
        int batch_count=20000*30;
        int demo_count=30;
        Vector[] pattern_list;
        Vector target_pattern;
        Random rnd = new Random();
        ArrayList<Real> errors = new ArrayList();
        Vector error=null;
        
        
        for(int i=0;i<batch_count;i++){
            //int idx = 445;
            int idx = rnd.nextInt(dictionary.size());
            String word = dictionary.get(idx);
            word=CharFeatureExtractor.padd(word);
        
            for (int j=1; j<word.length();j++){
                String subword = word.substring(0, j);
                Character target = word.charAt(j);
                //System.out.println(subword+" "+target);
                pattern_list = feature_extractor.str2patterns(subword);
                target_pattern = feature_extractor.data2pattern(target);
                error = rnn.train(pattern_list, target_pattern);
                errors.add(error.getNorm());
            }
            
            //System.out.println(error.norm());
            
            /*
            if(i%(batch_count/10)==0){
                Figure fig = new Figure();
                fig.plot(new Vector(errors));
            }
            */  
            
        }
        
        for (int i=0;i<demo_count;i++){
            rnn.reset();
            char c=CharFeatureExtractor.START_CHAR;
            String word = ""+c;
            while (true){
                Vector pattern = feature_extractor.data2pattern(c);
                Vector out = rnn.classify(pattern);
                out.power(4).div(out.sum());
                //out.show();
                //out=softmax(out);
                
                int idx = NLP.pick(out.getPrimitiveWeights());
                //int idx = out.argMax();
                
                c=CharFeatureExtractor.index2char(idx);
                //System.out.printf("%d\t%c\n", idx,c);
                word+=c;              
                if (c==CharFeatureExtractor.STOP_CHAR || word.length()>=20)    break;
            }
            System.out.printf("%s\n",word);
        } 
        rnn.save("C:\\Users\\kostis\\Downloads\\temp\\rnn."+(batch_count/1000)+".ser");
        
    }

}
