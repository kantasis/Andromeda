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
    
    private Matrix _coefficients_mat;
    private Vector _biases_vec;
    private Real _learningRate_r;
    
    public LinearRegressor(int input_cnt, int output_cnt){
        _coefficients_mat = new Matrix(input_cnt, output_cnt);
        _biases_vec = new Vector(output_cnt);
        _learningRate_r = new Real(0.1);
    }
    
    public int getInputCount(){
        return _coefficients_mat.getRowCount();
    }
    
    public int getOutputCount(){
        return _coefficients_mat.getColumnCount();
    }
      
    public Matrix classify(Matrix dataset){
        return dataset.getProduct(_coefficients_mat).add(_biases_vec);
    }
    
    public Matrix trainEpoch(Matrix dataset, Matrix target){
        Matrix result_matNL = this.classify(dataset);
        //target.getSize().show("target");
        Matrix error_matNL = (Matrix) result_matNL.copy().diff(target);
        Matrix derivative_matNL = (Matrix) error_matNL.copy().multiply(_learningRate_r);
        
        _biases_vec.diff(derivative_matNL.getAverageVector());
        _coefficients_mat.diff(dataset.getTransposed().getProduct(derivative_matNL));
        
        return error_matNL;
    }
    
    public void train(Matrix dataset, Matrix target){
        for(int i=0;i<MAX_ITERATIONS;i++){
            Matrix error_matNL = this.trainEpoch(dataset, target);
            Vector errors_vec = error_matNL.getNormVector();
            
            boolean abort_flag = true;
            for (int j=0; j<errors_vec.getLength(); j++){
                if (errors_vec.get(j).compareTo(e)>0){
                    abort_flag=false;
                }
            }
            if (abort_flag){
                Logger.log("returning after %d iterations",i);
                return;
            }
        }
    }
    
}
