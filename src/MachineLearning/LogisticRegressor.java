package MachineLearning;

import Core.Logger;
import Math.Matrix;
import Math.Operatables.Real;
import Math.Vector;
import java.util.Random;

public class LogisticRegressor extends Classifier{
    
    public static final int MAX_ITERATIONS = 10000;
    public static final Real e = new Real(1e-3);
    
    private Matrix _coefficients_matML;
    private Vector _biases_vec1L;
    private Real _learningRate_r;
    
    /**
     * Constructor
     * @param input_cnt number of input features
     * @param output_cnt number of hypotheses
     */
    public LogisticRegressor(int input_cnt, int output_cnt){
        _coefficients_matML = Matrix.random(input_cnt, output_cnt);
        _biases_vec1L = Vector.random(output_cnt);
        _learningRate_r = new Real(0.01);
    }
    
    /**
     * Apply the sigmoid function to the input Real
     * @param input_r the input Real
     * @return A new Real with the result of the sigmoid function
     */
    private Real _getSigmoid(Real input_r){
        Real temp = input_r.copy().negate().getExp().add(1).inv();
        if (temp.copy().abs().getPrimitive() > 1)
            Logger.log("WHAAA: check this out %s",temp);
        return temp;
    }

    /**
     * Apply the sigmoid function to the input matrix
     * @param input the input matrix
     * @return the input matrix
     */
    private Matrix _applySigmoid(Matrix input){
        for (int i=0;i<input.getRowCount();i++)
            for (int j=0;j<input.getColumnCount();j++){
                Real result_r = _getSigmoid(input.valueAt(i, j));
                if (result_r.copy().abs().getPrimitive() > 1)
                    Logger.log("SHIT: check this out %s",result_r);
                input.set(i, j, result_r);
                
            }
        return input;
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
        return _applySigmoid(x_mat.getProduct(_coefficients_matML).add(_biases_vec1L));
    }
    
    /**
     * Perform one training pass of the classifier using input and target data
     * @param x_matNM the input data matrix
     * @param y_matNL the target data matrix
     * @return the error matrix
     */
    public Matrix trainEpoch(Matrix x_matNM, Matrix y_matNL){
        int N = y_matNL.getRowCount();
        int L = y_matNL.getColumnCount();
        Matrix h_matNL = this.classify(x_matNM);
        
        // Note: the copy() here can be omitted but result_matNL will be muted
        Matrix factor1_matNL = (Matrix) y_matNL.copy().multiplyElements(h_matNL.copy().log());
        Matrix factor2_matNL = (Matrix) Matrix.ones(N,L).diff(y_matNL).multiplyElements( ((Matrix) Matrix.ones(N,L).diff(h_matNL)).log() );
        Matrix cost_matNL = (Matrix) factor1_matNL.copy().add(factor2_matNL).multiply(-1);
        
        Matrix error_matNL = (Matrix) h_matNL.copy().diff(y_matNL);
        
        Matrix derivative_matNL = (Matrix) error_matNL.copy().multiplyElements(h_matNL).multiplyElements(Matrix.ones(N,L).diff(h_matNL)).multiply(_learningRate_r);
        //Matrix derivative_matNL = (Matrix) error_matNL.copy().multiplyElements(h_matNL).multiplyElements(Matrix.ones(N,L).diff(h_matNL)).multiply(_learningRate_r);
        //Matrix derivative_matNL = (Matrix) error_matNL.multiply(_learningRate_r);
        
        _biases_vec1L.diff(derivative_matNL.getSumVector());
        _coefficients_matML.diff(x_matNM.getTransposed().getProduct(derivative_matNL) );//.multiply(1/x_matNM.getRowCount()));
        
        return error_matNL;
    }
    
    /**
     * Perform successive training passes (trainEpoch()) until either the error
     * is less than 'e' or less than MAX_ITERATIONS
     * @param x_mat
     * @param y_mat 
     */
    public void train(Matrix x_mat, Matrix y_mat){
        int i;
        Vector errors_vec = null;
        
        for(i=0;i<MAX_ITERATIONS;i++){
            Matrix error_matNL = this.trainEpoch(x_mat, y_mat);
            errors_vec = (Vector) error_matNL.getNormVector().multiply(1.0/x_mat.getRowCount());
            
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
