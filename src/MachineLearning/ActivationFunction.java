/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning;

import Math.Vector;

public interface ActivationFunction extends java.io.Serializable{

    public Vector evaluate(Vector x);
    public Vector derivative(Vector x);
    public Vector fast_derivative(Vector y);
    
    public double evaluate(double x);
    public double derivative(double x);
    public double fast_derivative(double y);
        
}
