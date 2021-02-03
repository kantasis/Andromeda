package MachineLearning;

import Math.Matrix;
import Math.Operatables.Real;
import Math.Vector;

public abstract class Classifier {
    
    public abstract Matrix classify(Matrix pattern);
    
    public Vector getMeanSquaredError(Matrix x_mat, Matrix y_mat){
        int N = x_mat.getRowCount();
        Matrix output_mat = this.classify(x_mat);
        Matrix error_mat = (Matrix) output_mat.copy().diff(y_mat);
        return (Vector) error_mat.getSumofSquaresVector().multiply(1.0/N);
    }
    
    public static Real R2(Vector y_vec, Vector z_vec){
        Real tss = y_vec.copy().diff(y_vec.average()).getSumofSquares();
        Real rss = z_vec.copy().diff(y_vec).getSumofSquares();
        return Real.unit().diff(rss.div(tss));
    }

    public abstract int getInputCount();
    public abstract int getOutputCount();

}
