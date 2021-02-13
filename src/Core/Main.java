package Core;


import static Core.NLP.*;
import MachineLearning.NeuralNetworks.NeuronLayer;
import MachineLearning.NeuralNetworks.RNNLayer;
import Math.Matrix;
import Math.Operatables.Real;
import Math.Vector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Main {
    
    public static final int LETTERS = CharFeatureExtractor.LETTERS;
    public static final int START = CharFeatureExtractor.START;
    public static final int STOP = CharFeatureExtractor.STOP;
    
    public static void main(String[] args){

        Core.Tests.main(args);
        
        /*
        for (char c='a';c<='z';c++){
            System.out.printf("%c - %d\n",c,(int)c);
            char2pattern(c).show();
        }      
        if (true)
            return;
        */
        
        /*
        CharFeatureExtractor featuremachine = new CharFeatureExtractor();
        
        BufferedReader reader;
            try {
                    String line;
                    NeuronLayer neurons = new NeuronLayer(LETTERS*2,LETTERS);
                    neurons.setTrainingRate(Real.unit());
                    double accel=1;//0.95;
                    
                    Vector start_pattern = new Vector(LETTERS);
                    start_pattern.set(START, 1d);
                    Vector stop_pattern =  new Vector(LETTERS);
                    stop_pattern.set(STOP, 1d);
                    
                    for (int reps=0; reps<REPS_MAX; reps++){
                        System.out.printf("%3d/%3d\n",reps,REPS_MAX);
                        reader = new BufferedReader(new FileReader(
                                    "C:\\Users\\kostis\\Downloads\\temp\\wordlist.txt"));
                        
                        
                        while ((line = reader.readLine())!= null) {

                            //System.out.printf("%3d/%3d)\t[%s]\n",reps,REPS_MAX,line);
                            
                            Vector prevPattern_vector = new Vector(LETTERS);
                            Vector pattern_vector = start_pattern;
                            Vector target;
                            for (int i=0;i<line.length();i++){
                                target = featuremachine.data2pattern(line.charAt(i));
                                neurons.train(pattern_vector.merge(prevPattern_vector), target);
                                //prevPattern_vector=neurons.classify(pattern_vector.merge(prevPattern_vector));
                                prevPattern_vector = pattern_vector;
                                pattern_vector=target;
                            }
                            target = stop_pattern;
                            neurons.train(pattern_vector.merge(prevPattern_vector), target);
                            neurons.setTrainingRate(neurons.trainingRate().getMultiply(accel));
                        }
                        reader.close();
                    }
                    
                    
                    Vector probs = neurons.classify(start_pattern.merge(featuremachine.data2pattern('a')));
                    probs.div(probs.sum());
                    Vector temp = new Vector(LETTERS);
                    
                    for (int i=0;i<30;i++)
                        System.out.printf("%s\n",produce(neurons));
                    
                    for (int i=0;i<LETTERS;i++)
                        for (int j=0;j<100000;j++){
                            int idx = pick(probs);
                            temp.set(idx, temp.get(idx).add(1));
                        }
                    temp.multiply(temp.sum().inv().multiply(100)).show();
                    probs.multiply(100).show();
                    neurons.save("C:\\Users\\kostis\\Downloads\\temp\\neuron.ser");
            } catch (IOException e) {
                    e.printStackTrace();
            }
        */
    }
    
    public static int pick(Vector x){
        if (true){
            Random rnd = new Random();
            
            double v = rnd.nextDouble();
            double sum = 0;
            for (int i=0;i<x.getLength();i++){
                sum+=x.get(i).getPrimitive();
                if (v<sum)
                    return i;
            }
            return x.getLength()-1;
        }else
            return x.argMax();
    }
}
