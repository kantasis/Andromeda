package MachineLearning;

import Math.Matrix;

public abstract class Classifier {
    
    public abstract Matrix classify(Matrix pattern);

    public abstract int getInputCount();
    public abstract int getOutputCount();

}
