/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning.NeuralNetworks;

import Core.Logger;
import MachineLearning.ActivationFunction;
import MachineLearning.ActivationFunctions;
import Math.Vector;
import Math.Matrix;
import Math.Operatables.Real;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.ArrayList;

/**
 *
 * @author kostis
 */
public class NeuronLayer implements java.io.Serializable{
    
    private Matrix _weights_matrix;
    private Vector _bias_vector;
    private ActivationFunction _activationFunction;
    
    public NeuronLayer(int inputs,int outputs){
        
        _activationFunction = new ActivationFunctions.Sigmoid();
        _weights_matrix = new Matrix(inputs,outputs);
        _bias_vector = new Vector(outputs);
                
        Random rnd = new Random();
        
        for( int j=0; j < outputs; j++){
            for( int i=0; i < inputs; i++){
                _weights_matrix.set(i, j, rnd.nextDouble());
                //_weights_matrix.set(i, j, 0);
            }            
        }
        for( int i=0; i < outputs; i++)
            _bias_vector.set(i, rnd.nextDouble());
            //_bias_vector.set(i, 0);
    }
        
    public int getOutputCount(){
       return _weights_matrix.getColumnCount();
    }
    
    public void _set(Matrix x, Vector y){
        _weights_matrix=x;
        _bias_vector=y;
    }
    
    public int getInputCount(){
        return _weights_matrix.getRowCount();
    }
        
    public Matrix classify(Matrix input_matrix){
        assert input_matrix.getColumnCount()==this.getInputCount(): 
            String.format("The input should be %d long (not %d)",
            this.getInputCount(),input_matrix.getColumnCount()
        );
        
        Matrix weightedSum_matrix = getWeightedSum(input_matrix);
        return _activationFunction.evaluate(weightedSum_matrix);
    }
        
    public Matrix[] train(Matrix input_matrix, Matrix target_matrix, Real learningRate, boolean doPropagate){
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
        
        Matrix output_matrix = this.classify(input_matrix);
        Matrix error_matrix = (Matrix) output_matrix.copy().diff(target_matrix);
                
        return train(input_matrix, error_matrix, output_matrix, learningRate, doPropagate);
    }
    
     public Matrix getWeightedSum(Matrix input_matrix){
        assert input_matrix.getColumnCount()==this.getInputCount(): 
            String.format("The input should be %d long (not %d)",
            this.getInputCount(),input_matrix.getColumnCount()
        );

        return input_matrix.getProduct(_weights_matrix).add(_bias_vector);
    }
    
    public Matrix[] train(Matrix input_matrix, Matrix error_matrix, Matrix output_matrix, Real learningRate, boolean doPropagate){

        Matrix derivative_matrix;
        if (this._activationFunction.hasFast())
            derivative_matrix = _activationFunction.fastDerivative(output_matrix);
        else
            derivative_matrix = _activationFunction.derivative(getWeightedSum(input_matrix));
        Matrix predelta_matrix = (Matrix) error_matrix.copy().multiplyElements(derivative_matrix);
        Matrix weightsDelta_matrix = input_matrix.getTransposed().getProduct(predelta_matrix);
        Vector biasDelta_vector = predelta_matrix.getSumVector();
        
        Matrix[] result = null;
        if (doPropagate){
            result = new Matrix[]{
                predelta_matrix.getProduct(_weights_matrix.getTransposed()),
                biasDelta_vector.getProduct(_bias_vector.getAsRowMatrix())
            };
        }    
        
        _weights_matrix.diff(weightsDelta_matrix.getMultiply(learningRate));
        _bias_vector.diff(biasDelta_vector.getMultiply(learningRate));
        
        return result;
    }

    public void show(String name){
        _weights_matrix.show(name+" weights");
        _bias_vector.show(name+" biases");
    }

    public void show(){
        this.show("");
    }
    
    public void save(String filename){
        try {
         FileOutputStream fileOut = new FileOutputStream(filename);
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(this);
         out.close();
         fileOut.close();
      } catch (IOException i) {
         i.printStackTrace();
      }
    }
    
    public static NeuronLayer load(String filename){
        NeuronLayer result;
        try {
         FileInputStream fileIn = new FileInputStream(filename);
         ObjectInputStream in = new ObjectInputStream(fileIn);
         result = (NeuronLayer) in.readObject();
         in.close();
         fileIn.close();
         return result;
      } catch (IOException i) {
         i.printStackTrace();
         return null;
      } catch (ClassNotFoundException c) {
         System.out.println("NeuronLayer class not found");
         c.printStackTrace();
         return null;
      }
    }
    
    public static void main(String[] args){
        
        Matrix inputs = new Matrix(new double[][]{
            {0,0},
            {0,1},
            {1,0},
            {1,1},
        });
        
        Matrix targets = new Matrix(new double[][]{
            {1,0,1,1},
            {0,1,1,0},
            {0,1,0,0},
            {0,1,0,1},
        });
        
        Real rate = new Real(10);
        
        inputs.show("Inputs");
        
        NeuronLayer neu = new NeuronLayer(inputs.getColumnCount(),targets.getColumnCount());
        neu.show("Layer");
        
        neu.getWeightedSum(inputs).show("Weigthed sums");
        neu.classify(inputs).show("Output");
        
        Logger.log("---");
        Logger.setLogLevel(Logger.LL_ERROR);
        for (int i=0;i<1000;i++){
            neu.train(inputs, targets, rate,false);
        }
        
        neu.show("Trained Layer");
        targets.show("Targets");
        neu.classify(inputs).show("Output");
    }
}
