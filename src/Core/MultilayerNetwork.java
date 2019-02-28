package Core;

import java.util.Random;

public class MultilayerNetwork extends Classifier {
    
    private final NeuronLayer[] _layers;
    
    public MultilayerNetwork(int inputs,int...neuronCounts){
        // (1000,2) one layer, 1000 inputs, 2 neurons
        // (1000,5,3) two layers, 1000 inputs, 5 neurons @L1, 3 neurons @L2
        //assert neuronCounts>=1
        _layers = new NeuronLayer[neuronCounts.length];
        for (int i=0;i<neuronCounts.length;i++){
            _layers[i]=new NeuronLayer(inputs,neuronCounts[i]);
            inputs=neuronCounts[i];
            //System.out.printf("Layer #%d: [%d %d]\n",i,_layers[i].getInputCount(), _layers[i].getOutputCount());
        }
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
        _train(pattern,target);
    }
    
    public double _train(Vector pattern, Vector target){
        assert pattern.getLength()==getInputCount(): String.format("Incompatible input size (%d, %d)",pattern.getLength(),getInputCount());
        assert target.getLength()==getOutputCount(): String.format("Incompatible target size (%d, %d)",target.getLength(),getOutputCount());
        // TODO: maybe some optimization can be done here cause train calls classify again
      
        int L = getLayerCount();
        double delta;
        Vector[] layer_outputs = new Vector[L];
        Vector[] layer_inputs = new Vector[L];
        
        layer_inputs[0]=pattern;
        for (int i=0;i<L;i++){
            if (i>0)
                layer_inputs[i]=layer_outputs[i-1];
            layer_outputs[i]=getLayer(i).classify(layer_inputs[i]);
        }
        
        delta = getLayer(L-1)._train(layer_inputs[L-1], target);
        Vector layer_error = layer_outputs[L-1].add(1,target,-1);
        
        for (int i=L-2;i>=0;i--){
            NeuronLayer layer = getLayer(i);
            layer_error = getLayer(i+1).reverse(layer_error);
            Vector layer_target = layer_outputs[i].add(1,layer_error,-1);
            //TODO: Fix this ugly shit
            delta+=layer._train(layer_inputs[i], layer_target);
        }
        return delta;
    }
    
    public double[] getInfluence(int layer,int neuron){
        if (layer==this.getLayerCount()-1)
            return new double[]{1};
        
        double[] influence = new double[getLayer(layer+1).getNeuronCount()];
        for (int i=0;i<influence.length;i++){
            double sum = getLayer(layer+1).getNeuron(i).getBias();
            for(int j=0;j<getLayer(layer+1).getNeuron(i).getInputCount();j++)
                sum+=Math.abs(getLayer(layer+1).getNeuron(i).getWeight(j));
            influence[i]=getLayer(layer+1).getNeuron(i).getWeight(neuron)/sum*100;
        }
        
        return influence;
    }
    
    public static void main(String[] args) {
        MultilayerNetwork neu = new MultilayerNetwork(2,3,3);
        Vector[] inputs = {
            new Vector(0,0),
            new Vector(0,1),
            new Vector(1,0),
            new Vector(1,1)
        };
        Vector[] outputs = {
            new Vector(0d,0d,0d),
            new Vector(0d,1d,1d),
            new Vector(0d,0d,1d),
            new Vector(1d,0d,0d)
        };
        double d=10;
        int count=0;
        while ( count < 1e3 ){
            int idx=count%inputs.length;
            double delta = neu._train(inputs[idx],outputs[idx]);
            d+=delta;
            count++;
            System.out.printf("%f\t%f\n",d,d/count);
        }
        System.out.printf("%d\t%.5f\n",count,d);
        for(int i=0;i<inputs.length;i++)
            System.out.printf("In: %s\ttgt: %s\tout: %s\n",inputs[i],outputs[i],neu.classify(inputs[i]));
    }
    
    public void present(Vector[] patterns, Vector[] targets){
        // assert here
        for (int i=0;i<patterns.length;i++){
            Vector output = classify(patterns[i]);
            System.out.printf("%s  -> %s\n",patterns[i],output);
            System.out.printf("%s\n",targets[i]);
            System.out.printf("%s\n",targets[i].add(output, -1));
            System.out.printf("------\n");
        }
        
    }
    
    public void teach(MultilayerNetwork that,Vector pattern){
        Vector target = this.classify(pattern);
        that.train(pattern, target);
    }
    
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
