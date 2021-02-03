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
public class LinearRegressor {
    
    private Vector _coefficients;
    private Real _learningRate;
    
    public LinearRegressor(int input_count){
        _coefficients = new Vector(input_count);
        _learningRate = new Real("0.1");
    }
    
    public int getInputs(){
        return _coefficients.getLength();
    }
    
    public Real getLearningRate(){
        return _learningRate.copy();
    }
    
    public void setLearningRate(Real x){
        _learningRate = x;
    }
    
    public Vector getCoefficients(){
        return _coefficients.copy();
    }
    
    public void setCoefficients(Vector x){
        _coefficients = x;
    }
        
    public Vector classify(Matrix dataset){
        Matrix result = dataset.getProduct(getCoefficients());
        return result.getColumn(0);
    }
    
    public Real classify(Vector pattern){
        return classify((Matrix) pattern).get(0);
    }
    
    public void train(Matrix dataset, Vector target){
        Vector output = this.classify(dataset);
        Vector signed_error_vector = output.diff(target);
        Matrix temp = signed_error_vector.getAsColMatrix().getProduct(Matrix.ones(1,this.getInputs()));
        temp.multiplyElements(dataset);
        _coefficients.add(temp.getAverageVector().multiply(getLearningRate().getNegative()));
        signed_error_vector.power(2).sum().show("Error");
    }
    
    public static void main(String[] args){
        Matrix raw_dataset = new Matrix(new double[][]{
            {2104,     5,      1,   45  },
            {1416,     3,      2,   40  },
            {1534,     3,      2,   130  },
            //{1534,     3,      2,   30  },
            {852,      2,      1,   36  }
        });
        
        Vector raw_target = new Vector(new double[]{
            460,
            232,
            315,
            178
        });
        
        int N=raw_dataset.getRowCount();
        int M=raw_dataset.getColumnCount();
        Matrix dataset = raw_dataset.copy();
        dataset.show("Raw");
        dataset.ground();
        dataset.scale();
        
        dataset=(Matrix)dataset.getMergeLR(Matrix.ones(N, 1));
        M++;
        
        dataset.show("Preprocessed");
        
        LinearRegressor reg = new LinearRegressor(M);
        
        for (int i=0;i<3000;i++)
            reg.train(dataset, raw_target);
        reg.getCoefficients().show("Coefficients");
    }
}
