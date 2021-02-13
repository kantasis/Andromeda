package MachineLearning.NeuralNetworks;

import Core.NLP;
import Graphics.Figure;
import Math.Matrix;
import Math.Operatables.Real;
import Math.Vector;
import java.util.ArrayList;
import java.util.Random;

public class RNNLayer extends NeuronLayer{

    private Vector _memory_vector;
    private int _input_c, _output_c;
    
    public static char[] dict = "helowrd.".toCharArray();
    
    public RNNLayer(int inputs, int outputs){
        super(inputs+outputs,outputs);
        _input_c = inputs;
        _output_c = outputs;
        
        _memory_vector = new Vector(outputs);
    }
    
    public int getInputCount(){
        return super.getInputCount()-getOutputCount();
    }
    
    public Vector classify(Vector input_vector){
        Vector merged_vector = input_vector.merge(_memory_vector);
        /*
        System.out.printf("---\t Classifying\n");
        _memory_vector.show();
        input_vector.show();
        */
        _memory_vector = super.classify(merged_vector);
        /*
        System.out.printf("\n");
        _memory_vector.show();
        System.out.printf("---\t / Classifying\n");
        */
        return _memory_vector;
    }
    
    public Vector _classify(Vector input_vector, Vector memory_vector){
        Vector merged_vector = input_vector.merge(_memory_vector);
        return super.classify(merged_vector);
    }
    
    public void reset(){
        _memory_vector = new Vector(getOutputCount());
    }

    
    public static Vector getPattern(char x){
        // save 3 places for START END and UNKNOWN
        Vector result = new Vector(dict.length);
        for (int i=0;i<dict.length;i++){
            if (dict[i]==x){
                result.set(i, 1d);
                break;
            }                    
        }
        //System.out.printf("Returning:\n");
        //result.show();
        return result;
    }
    
    public Vector train(Vector[] pattern_vectors, Vector target_vector){
        // Note: m[k]=o[k-1]
        int steps = pattern_vectors.length;
        reset();
        Vector[] output_vectors = new Vector[steps];
        for (int k=0;k<steps;k++){
            output_vectors[k] = classify(pattern_vectors[k]);
            //pattern_vectors[k].show();
            //output_vectors[k].show();
            //System.out.println("\t><");
        }
        
        //System.out.println("===== Training");
        Vector error_vector = target_vector.getDiff(output_vectors[steps-1]);
        Vector result = error_vector.copy();
        //System.out.printf("Error: \t[%.3f]\n",error_vector.norm());
        //System.out.printf("%.3f\n",error_vector.norm());
        Matrix delta_matrix = new Matrix(getInputCount()+getOutputCount()+1,getOutputCount());
        for (int k=steps-1;k>=0;k--){
            if (k>0)
                _memory_vector = output_vectors[k-1];
            else
                reset();
            //System.out.println(target_vector);
            Matrix mergedBiasedInput_matrix = appendBias(pattern_vectors[k].merge(_memory_vector));

            Matrix temp = getDelta(mergedBiasedInput_matrix, output_vectors[k], error_vector);
            delta_matrix.add(temp);
            
            /*
            System.out.printf("k:\t%d\n",k);
            System.out.print("Merged Biased Input Matrix");
            mergedBiasedInput_matrix.show();            
            output_vectors[k].show("Output");
            error_vector.show("Error");
            error_vector.getAdd(1,output_vectors[k],1).show("Target");
            System.out.println("----");            
            */
            
            // TODO: this does an extra matrix multiplication
            
            Vector memoryError_vector = backpropagate(error_vector);
            int j;
            for (j=0;j<error_vector.getLength();j++){
                error_vector.set(j, memoryError_vector.get(j));
            }
            error_vector.div(error_vector.getNorm());
            /*
            for (int i=0;i<_memory_vector.getLength();i++){
                _memory_vector.set(i, memoryError_vector.get(j++));
            }
            */
            
            //error_vector.show("Error+");
        }
        updateWeights(delta_matrix);
        return result;
    }
    
    public static void main(String[] args){
        RNNLayer rnn = new RNNLayer(dict.length,dict.length);
        //rnn.show();
        //rnn.setActivationFunction(new ActivationFunctions.Tanh(1));
        //ArrayList<String> words = readDictionary("C:\\Users\\kostis\\Downloads\\temp\\aawordlist.txt");
        Random rnd = new Random();
        //String str = "hello world welcome.";
        String str = "hello_world.";
        
        ArrayList <Vector[]> inputSequence_list = new ArrayList<Vector[]>();
        ArrayList <Vector[]> outputSequence_list = new ArrayList<Vector[]>();
        
        int len = str.length();
        int steps = 3;
        int batches=1000;
        Real rate = new Real(1);
        Real l = new Real(0.999);
        int demo_count=0;
        Vector errors = new Vector(batches);
        
        for (int i=0; i<len-steps; i++){
            Vector[] patterns = new Vector[steps];
            Vector[] outputs = new Vector[steps];
            int idx=i;
            //int idx=0;
            rnn.reset();
            for (int k=0;k<steps;k++){
                patterns[k] = getPattern(str.charAt(idx+k));
                outputs[k] = rnn.classify(patterns[k]);
            }
            inputSequence_list.add(patterns);
            outputSequence_list.add(outputs);
        }
        
        for (int j=0; j<inputSequence_list.size();j++){
            String in ="";
            String out="";
            for(int i=0;i<inputSequence_list.get(j).length;i++){
                in+=dict[inputSequence_list.get(j)[i].argMax()];
                out+=dict[outputSequence_list.get(j)[i].argMax()];
            }                
            //System.out.printf("in:  %s\n",in);
            //System.out.printf("out: %s\n",out);
        }
        
        rnn.setTrainingRate(rate);
        
        for (int batch=0;batch<batches;batch++){
            //int idx=batch%inputSequence_list.size();
            int idx=rnd.nextInt(inputSequence_list.size());
            //int idx=rnd.nextInt(inputSequence_list.size());
            //System.out.printf("\t%d\n",idx);
            rnn.reset();
            Vector target = getPattern(str.charAt(idx+steps));
            Vector error_vector = rnn.train(inputSequence_list.get(idx), target);
            errors.set(batch,error_vector.getNorm());
            
//             since the rate variable is shared, this should change the
//             training rate of all the Layer
            rate.multiply(l);
//            rnn.setTrainingRate(rnn.getTrainingRate()*l);
        }
        
        
        for (int i=0;i<demo_count;i++){
            rnn.reset();
            String word ="";
            char c='h';
            while (true){
                Vector pattern = getPattern(c);
                Vector out = rnn.classify(pattern);
                //int idx = NLP.pick(out.getMultiplied(1.0/out.sum()).toArray());//out.argMax();
                int idx = NLP.pick(NLP.softmax(out).getPrimitiveWeights());
                //int idx = out.argMax();
                c=dict[idx];
                word+=c;              
                if (c=='.' || word.length()>=50)    break;
            }
            System.out.printf("%s\n",word);
        } 
        //System.out.printf("[%f]\n",rnn.getTrainingRate());
        
        for (int i=0; i<inputSequence_list.size(); i++){
            Vector[] patterns = inputSequence_list.get(i);
            rnn.reset();
            String in="";
            String out="";
            Vector output=null;
            for (Vector pattern : patterns){
                output = rnn.classify(pattern);
                in+=dict[pattern.argMax()];
            }
            out+=dict[output.argMax()];
            System.out.printf("%d %s\t%s\n",inputSequence_list.size(),in,out);
            //output.show();
        }
        //errors.show();
        Figure fig = new Figure();
        fig.plot(errors);
        System.out.printf("\tEVALUATING\t(%f)\n",errors.average());
        rnn.reset();
        int idx=0;
        for (idx=0;idx<inputSequence_list.size();idx++){

            Vector[] patterns = inputSequence_list.get(idx);
            Vector output = null;
            Vector target =  getPattern(str.charAt(idx+steps));
            for (Vector pattern : patterns){
                output = rnn.classify(pattern);
            }
            //target.show("target ");
            //output.show("pre out");
            //target.getAdd(1,output,-1).show("Error   ");
            rnn.train(patterns, target);
            for (Vector pattern : patterns){
                output = rnn.classify(pattern);
            }
            //output.show("pst out");
        }
    }
    public static void main3(String[] args){

    }
}
