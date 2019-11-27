/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning;

import Math.Vector;
import Math.Matrix;
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
    
    private Matrix _weights;
    private double _trainingRate=1;
    private ActivationFunction _activationFunction;
    
    public NeuronLayer(int inputs,int outputs){
        _activationFunction = new ActivationFunctions.Sigmoid(1);
        _weights = new Matrix(inputs+1,outputs);
        
        Random rnd = new Random();
        for( int j=0; j < _weights.getColumns(); j++){
            Vector temp = new Vector(_weights.getRows());
            for( int i=0; i < _weights.getRows(); i++)
                temp.set(i,rnd.nextDouble());
            temp.add(-temp.average());
            temp.times(temp.norm());
            for( int i=0; i < _weights.getRows(); i++)
                _weights.set(i, j, temp.get(i));            
        }
            
                
            
                
        //System.out.println(_weights.getSize());
    }
        
    public int getOutputCount(){
        return _weights.getColumns();
    }
    
    public int getInputCount(){
        return _weights.getRows()-1;
    }
    
    public double getTrainingRate(){
        return _trainingRate;
    }
    
    public void setTrainingRate(double v){
        _trainingRate=v;
    }
    
    public ActivationFunction getActivationFunction(){
        return _activationFunction;
    }
    
    public void setActivationFunction(ActivationFunction x){
        _activationFunction=x;
    }
    
    public Matrix addBias(Vector unbiased_vector){
        // gets a vector and returns it as a row matrix with a '1' appended
        Matrix biased_matrix = new Matrix(1, unbiased_vector.getLength()+1);
        for (int i=0; i<unbiased_vector.getLength(); i++)
            biased_matrix.set(0, i, unbiased_vector.get(i));
        biased_matrix.set(0, unbiased_vector.getLength(), 1d);
        return biased_matrix;
    }
    
    public Vector getWeightedSum(Vector input_vector){
        return getWeightedSum(input_vector.getAsRowMatrix());
    }
    
    public Vector getWeightedSum(Matrix input_matrix){
        assert input_matrix.getRows()==1: String.format("The input of this function should be a Row-Matrix (%s)",input_matrix.getSize());
        assert input_matrix.getColumns()==_weights.getRows(): String.format("The input should be %d long (not %d)",_weights.getRows(),input_matrix.getColumns());
        //System.out.printf("Getting weighted sum of:\n");
        //input_matrix.show();
        //_weights.show();
        return input_matrix.getMult(_weights).getVector();
    }
    
    public Vector classify(Vector unbiasedInput_vector){
//        assert unbiasedInput_vector.getLength()==getInputCount(): String.format("Incompatible input size (%d, %d)",unbiasedInput_vector.getLength(),getInputCount());
        Matrix biasedInput_matrix = addBias(unbiasedInput_vector);
        Vector weightedSum_Vector = getWeightedSum(biasedInput_matrix);
        return getActivationFunction().evaluate(weightedSum_Vector);
    }
        
    public Matrix getDelta(Matrix biasedInput_matrix, Vector output_vector, Vector error_vector){
        Vector derivative_vector = getActivationFunction().fast_derivative(output_vector);
        Vector delta_vector = error_vector.copy().multiplyElements(derivative_vector);
        Matrix delta_matrix = delta_vector.getMult(biasedInput_matrix).getTransposed();
        return delta_matrix;
    }
    
    public Vector train(Vector pattern, Vector target){
        /// Returns the error of the previous layer
        assert pattern.getLength()==_weights.getRows()-1: 
                String.format("Incompatible input size (%d, %d)",pattern.getLength(),_weights.getRows()-1);
        assert target.getLength()==getOutputCount(): 
                String.format("Incompatible target size (%d, %d)",target.getLength(),getOutputCount());
    
        Matrix biasedInput_matrix = addBias(pattern);
        Vector weightedSum_Vector = getWeightedSum(biasedInput_matrix);
        Vector output_vector = getActivationFunction().evaluate(weightedSum_Vector);
        Vector error_vector = target.copy().diff(output_vector);
        
        Matrix delta_matrix = getDelta(biasedInput_matrix, output_vector, error_vector);
        updateWeights(delta_matrix);
        
        Vector prev_matrix = backpropagate(error_vector);
        
        return prev_matrix;
    }
    
    public void updateWeights(Matrix diff_matrix){
        _weights.add(1,diff_matrix,getTrainingRate());
        //_weights.show();
    }
    
    public Vector backpropagate(Matrix error_matrix){
        // return the error of the previous step
        Matrix prevError_matrix = _weights.getMult(error_matrix);
        Vector unbiasedPrevError_vector = new Vector(prevError_matrix.getRows()-1);
        for (int i=0; i<unbiasedPrevError_vector.getLength();i++)
            unbiasedPrevError_vector.set(i,prevError_matrix.get(i,0));
        return unbiasedPrevError_vector;
    }
        
    public void show(){
        _weights.show();
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
        NeuronLayer neu = new NeuronLayer(2,5);
        neu.show();
        neu.setTrainingRate(0.1);
        neu.setActivationFunction(new ActivationFunctions.Sigmoid(1));
        
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
        outputs[0]
            .show();
        
        Random rnd = new Random();
        for (int i=0; i<1e5;i++){
            int idx = rnd.nextInt(inputs.length);
            //System.out.print(idx);
            neu.train(inputs[idx],outputs[idx]);
            
            Vector error = neu.classify(inputs[idx]).diff(outputs[idx]);
            //System.out.printf("\t%5.2f\n",error.norm());
        }
        
        for (Vector input : inputs){
            neu.classify(input)._apply((d)->{return Math.round(d);}).show();
        }
        neu.show();
                
    }
}
