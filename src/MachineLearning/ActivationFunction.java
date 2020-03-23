/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning;

import Math.Matrix;
import Math.Operatables.Real;
import Math.Vector;

public interface ActivationFunction extends java.io.Serializable{

    public Matrix evaluate(Matrix x);
    public Matrix derivative(Matrix x);
    public Matrix fastDerivative(Matrix x);
    
    public Real evaluate(Real x);
    public Real derivative(Real x);
    public Real fastDerivative(Real x);
    
    public boolean hasFast();
        
}
