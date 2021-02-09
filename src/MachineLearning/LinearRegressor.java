/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning;

import Core.Logger;
import Math.Matrix;
import Math.Operatables.GenericMatrix;
import Math.Operatables.Real;
import Math.Vector;

/**
 *
 * @author kostis
 */
public class LinearRegressor extends Classifier{
    
    public static final int MAX_ITERATIONS = 1000;
    public static final Real e = new Real(1e-3);
    
    private Matrix _coefficients_matML;
    private Vector _biases_vec1L;
    private Real _learningRate_r;
    
    /**
     * Constructor
     * @param input_cnt number of input features
     * @param output_cnt number of hypotheses
     */
    public LinearRegressor(int input_cnt, int output_cnt){
        _coefficients_matML = Matrix.random(input_cnt, output_cnt);
        _biases_vec1L = Vector.random(output_cnt);
        _learningRate_r = new Real(0.1);
    }
    
    /**
     * Get the number of input features
     * @return an int expressing the feature count
     */
    public int getInputCount(){
        return _coefficients_matML.getRowCount();
    }
    
    /**
     * Get the number of outputs
     * @return an int expressing the hypothes count
     */
    public int getOutputCount(){
        return _coefficients_matML.getColumnCount();
    }

    /**
     * Classify the input into an output hypothesis
     * @param x_mat
     * @return the output hypothesis
     */
    public Matrix classify(Matrix x_mat){
        return x_mat.getProduct(_coefficients_matML).add(_biases_vec1L);
    }

    /**
     * Perform one training pass of the classifier using input and target data
     * @param x_matNM the input data matrix
     * @param y_matNL the target data matrix
     * @return the error matrix
     */
    public Matrix trainEpoch(Matrix x_matNM, Matrix y_matNL){
        Matrix h_matNL = this.classify(x_matNM);
        
        // Note: the copy() here can be omitted but result_matNL will be muted
        Matrix error_matNL = (Matrix) h_matNL.copy().diff(y_matNL);
        Matrix derivative_matNL = (Matrix) error_matNL.copy().multiply(_learningRate_r); //.multiply(2);
        
        _biases_vec1L.diff(derivative_matNL.getSumVector());
        _coefficients_matML.diff(x_matNM.getTransposed().getProduct(derivative_matNL) );//.multiply(1/x_matNM.getRowCount()));
        
        return error_matNL;
    }
    
    /**
     * Perform successive training passes (trainEpoch()) until either the error
     * is less than 'e' or less than MAX_ITERATIONS
     * @param dataset
     * @param target 
     */
    public void train(Matrix dataset, Matrix target){
        int i;
        Vector errors_vec = null;
        
        for(i=0;i<MAX_ITERATIONS;i++){
            Matrix error_matNL = this.trainEpoch(dataset, target);
            errors_vec = error_matNL.getNormVector();
            
            boolean abort_flag = true;
            for (int j=0; j<errors_vec.getLength(); j++){
                if (errors_vec.get(j).compareTo(e)>0){
                    abort_flag=false;
                }
            }
            if (abort_flag)
                break;
        }
        
        Logger.log("returning after %d iterations, e=%s",i, errors_vec);
    }
    
    /**
     * Show the information about this classifier
     * @param name the name to be displayed at the console
     */
    public void show(String name){
        _coefficients_matML.show("Coefficients of "+name);
        _biases_vec1L.show("Biases of "+name);
    }
    
}
