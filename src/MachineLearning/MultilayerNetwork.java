package MachineLearning;

import Math.Vector;
import Math.Matrix;
import java.util.Random;

public class MultilayerNetwork extends Classifier {
    
    private NeuronLayer[] _layers;
    
    public MultilayerNetwork(int inputs,int...neuronCounts){
        // (1000,2) one layer, 1000 inputs, 2 neurons
        // (1000,5,3) two layers, 1000 inputs, 5 neurons @L1, 3 neurons @L2
        //assert neuronCounts>=1
        _layers = new NeuronLayer[neuronCounts.length];
        for (int i=0;i<neuronCounts.length;i++){
            _layers[i]=new NeuronLayer(inputs,neuronCounts[i]);
            inputs=neuronCounts[i];
        }
    }
    
    public MultilayerNetwork(NeuronLayer[] layers){
        _layers=layers;
    }
    
    public NeuronLayer getLayer(int i){
        return _layers[i];
    }
    
    public int getLayerCount(){
        return _layers.length;
    }
    
    public int getOutputCount(){
        return getLayer(getLayerCount()-1).getOutputCount();
    }
    
    public int getInputCount(){
        return getLayer(0).getInputCount();
    }
    
    public double getTrainingRate(){
        return getLayer(0).getTrainingRate();
    }
    
    public void setTrainingRate(double v){
        for(int i=0;i<getLayerCount();i++)
            getLayer(i).setTrainingRate(v);
    }
    
    public Vector classify(Vector input){
        assert input.getLength()==getInputCount(): String.format("Incompatible input size (%d, %d)",input.getLength(),getInputCount());
        
        Vector output = input;
        for (int i=0;i<getLayerCount();i++)
            output = getLayer(i).classify(output);
        return output;
    }
    
    
    public void train(Vector pattern, Vector target){
        assert pattern.getLength()==getInputCount(): String.format("Incompatible input size (%d, %d)",pattern.getLength(),getInputCount());
        assert target.getLength()==getOutputCount(): String.format("Incompatible target size (%d, %d)",target.getLength(),getOutputCount());
        // TODO: maybe some optimization can be done here cause train calls classify again
      
        int L = getLayerCount();
        Vector[] layer_outputs = new Vector[L];
        Vector[] layer_inputs = new Vector[L];
        
        layer_inputs[0]=pattern;
        for (int i=0;i<L;i++){
            if (i>0)
                layer_inputs[i]=layer_outputs[i-1];
            layer_outputs[i]=getLayer(i).classify(layer_inputs[i]);
        }
        Vector prevError_vector = getLayer(L-1).train(layer_inputs[L-1], target);
        Vector target_vector;
        
        for (int i=L-2;i>=0;i--){
            //prevError_vector.multiplyElements(getLayer(i).getActivationFunction()._fast_derivative(layer_outputs[i]));
            target_vector = prevError_vector.getAdd(1, layer_outputs[i], 1);
            prevError_vector = getLayer(i).train(layer_inputs[i], target_vector );
        }
        
        
        return;
    }

    public static void main(String[] args) {
        MultilayerNetwork neu = new MultilayerNetwork(2,30,5);
        neu.setTrainingRate(0.1);
        
        Vector[] inputs = {
            new Vector(0d,0d),
            new Vector(0d,1d),
            new Vector(1d,0d),
            new Vector(1d,1d)
        };
        
        Vector[] outputs = {
            new Vector(1d,0d,0d,0d,0d),
            new Vector(0d,1d,0d,0d,1d),
            new Vector(0d,0d,1d,0d,1d),
            new Vector(0d,0d,0d,1d,0d)
        };
        
        Random rnd = new Random();
        
        for (int i=0; i<1e4;i++){
            int idx = rnd.nextInt(inputs.length);
            //System.out.print("---");
            neu.train(inputs[idx],outputs[idx]);
            
            Vector error = neu.classify(inputs[idx]).diff(outputs[idx]);
            System.out.printf("\t%5.2f\n",error.norm());
        }
        
        for (Vector input : inputs){
            neu.classify(input).show();//._apply((d)->{return Math.round(d);}).show();
        }
        
    }
    
    public void present(Vector[] patterns, Vector[] targets){
        // assert here
        for (int i=0;i<patterns.length;i++){
            Vector output = classify(patterns[i]);
            System.out.printf("%s  -> %s\n",patterns[i],output);
            System.out.printf("%s\n",targets[i]);
            System.out.printf("%s\n",targets[i].getAdd(1,output, -1));
            System.out.printf("------\n");
        }
        
    }
    
    /*
    public void teach(MultilayerNetwork that,Vector pattern){
        Vector target = this.classify(pattern);
        that.train(pattern, target);
    }
    */
    
    /*
    public double[] evaluate(double[][] patterns, double[][] target){
        double[] err;
        double[] result=new double[target[0].length];
        for (int i=0;i<patterns.length;i++){
            double[] p_out = classify(patterns[i]);
            for(int j=0;j<p_out.length;j++)
                result[j]+=Math.pow(target[i][j]-p_out[j],2);
        }
        for(int j=0;j<result.length;j++)
            result[j]=Math.sqrt(result[j]);
        return result;
    }
      */
}
