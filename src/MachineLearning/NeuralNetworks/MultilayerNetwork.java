package MachineLearning.NeuralNetworks;

import Core.Logger;
import Math.Vector;
import Math.Matrix;
import Math.Operatables.Real;
import java.util.Random;

public class MultilayerNetwork  {
    
    private NeuronLayer[] _layers;
    
    public MultilayerNetwork(int inputs,int...neuronCounts){
        // (1000,2) one layer, 1000 inputs, 2 neurons
        // (1000,5,3) two layers, 1000 inputs, 5 neurons @L1, 3 neurons @L2
        //assert neuronCounts>=1
        assert neuronCounts.length>0: "Trying to declare a MultilayerNetwork with no layers";
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
      
    public Matrix classify(Matrix input){
        assert input.getColumnCount()==getInputCount(): String.format("Incompatible input size (%d, %d)",input.getColumnCount(),getInputCount());
        
        Matrix output = input;
        for (int i=0;i<getLayerCount();i++){
            output = getLayer(i).classify(output);
        }
        return output;
    }
    
    
    public void train(Matrix input_matrix, Matrix target_matrix, Real learningRate){
        assert input_matrix.getColumnCount()==this.getInputCount(): 
            String.format("The input should be %d long (not %d)",
            this.getInputCount(),
            input_matrix.getColumnCount()
        );        
        
        assert target_matrix.getColumnCount()==this.getOutputCount(): 
            String.format("The target should be %d long (not %d)",
            this.getOutputCount(),
            target_matrix.getColumnCount()
        );        
        
        assert input_matrix.getRowCount()==target_matrix.getRowCount(): 
            String.format("The input and target matrices should have the same "+
                "row count (%d %d)",                    
                input_matrix.getRowCount(),
                target_matrix.getRowCount()
        );
        // TODO: maybe some optimization can be done here cause train calls classify again
      
        int L = getLayerCount();
        Matrix[] layer_outputs = new Matrix[L];
        Matrix[] layer_inputs = new Matrix[L];
        
        layer_inputs[0]=input_matrix;
        layer_outputs[0]=getLayer(0).classify(layer_inputs[0]);
        for (int i=1;i<L;i++){
            layer_inputs[i]=layer_outputs[i-1];
            layer_outputs[i]=getLayer(i).classify(layer_inputs[i]);
        }
        Matrix[] temp = getLayer(L-1).train(layer_inputs[L-1], target_matrix, learningRate,true);
        for (int i=L-2;i>=0;i--){
            Matrix error_matrix = temp[0];
            Matrix biasError_matrix = temp[1];
            temp = getLayer(i).train(layer_inputs[i], error_matrix, layer_outputs[i], learningRate, true);
        }
        return;
    }

    public void show(String x){
        for (int i=0;i<this.getLayerCount();i++)
            this.getLayer(i).show(x+" Layer #"+i);
    }

    public void teach(MultilayerNetwork that,Matrix dataset, Real rate){
        Matrix target_matrix = this.classify(dataset);
        that.train(dataset, target_matrix, rate);
    }
    
    /**
     * Unittest the MLN class.
     * Testcase: Evaluating the XOR function
     */
    public static void unittest(){
        int max_iterations = 10000;
        Real rate = new Real(4);
        Matrix inputs = new Matrix(new double[][]{
            {0,0},
            {0,1},
            {1,0},
            {1,1},
        });
        Matrix targets = new Matrix(new double[][]{
            {0,1},
            {1,1},
            {1,0},
            {0,0},
        });
        
        MultilayerNetwork neu = new MultilayerNetwork(inputs.getColumnCount(),2,targets.getColumnCount());
        
        Random rnd = new Random();
        Logger.setLogLevel(Logger.LL_ERROR);
        
        for (int pct = 0; pct<100; pct++){
            Logger.log("Completed %3d%%",pct);
            for (int i=0;i<max_iterations/100;i++){
                int idx = rnd.nextInt(inputs.getRowCount());
                neu.train(inputs.getRow(idx).getAsRowMatrix(), targets.getRow(idx).getAsRowMatrix(), rate);
            }
        }
        neu.classify(inputs).show("\nResults");
    }
    
    public static void main(String[] args) {
        unittest();
    }
    
}