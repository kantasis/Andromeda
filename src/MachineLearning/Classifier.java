package MachineLearning;

import Math.Matrix;
import Math.Operatables.Real;
import Math.Vector;

public abstract class Classifier {
    
    public abstract Matrix classify(Matrix pattern);
    
    /**
     * Get the mean squared error metric of the classifier
     * @param x_mat the input matrix NxM
     * @param y_mat the output matrix NxL
     * @return a 1xL vector of the average squared error of this classifier
     */
    public Vector getMeanSquaredError(Matrix x_mat, Matrix y_mat){
        // a Vector of shape 1xL 
        int N = x_mat.getRowCount();
        Matrix output_mat = this.classify(x_mat);
        Matrix error_mat = (Matrix) output_mat.copy().diff(y_mat);
        return error_mat.power(2).getAverageVector();
    }
    
    public static Real R2(Vector y_vec, Vector z_vec){
        Real tss = y_vec.copy().diff(y_vec.average()).getSumofSquares();
        Real rss = z_vec.copy().diff(y_vec).getSumofSquares();
        return Real.unit().diff(rss.div(tss));
    }

    public abstract int getInputCount();
    public abstract int getOutputCount();

}
