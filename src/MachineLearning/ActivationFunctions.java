/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning;

import Math.Operatables.Real;
import Math.Matrix;

public abstract class ActivationFunctions implements ActivationFunction{

    public abstract Real evaluate(Real x);
    private static boolean hasFast=false;
    
    public Matrix evaluate(Matrix x){
        Matrix result = new Matrix(x.getRowCount(),x.getColumnCount());
        for (int i=0;i<result.getRowCount();i++)
            for (int j=0;j<result.getColumnCount();j++)
                result.set(i,j, this.evaluate(x.get(i,j)));
        return result;
    }
    
    public Matrix derivative(Matrix x){
        Matrix result = new Matrix(x.getRowCount(),x.getColumnCount());
        for (int i=0;i<result.getRowCount();i++)
            for (int j=0;j<result.getColumnCount();j++)
                result.set(i,j, this.derivative(x.get(i,j)));
        return result;    
    }
    
    public Matrix fastDerivative(Matrix x){
        Matrix result = new Matrix(x.getRowCount(),x.getColumnCount());
        for (int i=0;i<result.getRowCount();i++)
            for (int j=0;j<result.getColumnCount();j++)
            result.set(i,j, this.fastDerivative(x.get(i,j)));
        return result;    
    }
    
    public boolean hasFast(){
        return hasFast;
    }
    
    /**
     * Returns the derivative from the output of the original function not the 
     * input
     * 
     * @param output the output of the evaluate function
     * @return the derivative
     */
    public Real fastDerivative(Real output){
        assert hasFast(): "This activation function does not have a fast "
            + "implementation";
        return null;    
    }
    
    public static class Sigmoid extends ActivationFunctions{
        public Real _a = new Real(1);
        public Sigmoid(){
            this(new Real(1));
        }
        public Sigmoid(Real a){
            _a = a;
            hasFast=true;
        }
        
        public Real evaluate(Real v){
            // TODO Fix this
            double result = 1.0/(1+Math.exp(-v.getPrimitive()*_a.getPrimitive()));
            return new Real(result);
        }
        
        public Real derivative(Real y){
            // TODO Fix this
            double result = y.getPrimitive();
            return new Real(result*(1-result));
        }
        
        public Real fastDerivative(Real output){
            assert hasFast(): "This activation function does not have a fast "
                + "implementation";
            return output.getMultiply(Real.unit().diff(output));    
        } 
        
    }
    public static class Tanh extends ActivationFunctions{
        public Real _a = new Real(1);
        public Tanh(Real a){
            _a = a;
        }
        public Real evaluate(Real v){
            //TODO: Fix  this
            return new Real(Math.tanh(v.getPrimitive()*_a.getPrimitive()));
        }
                
        public Real derivative(Real y){
            // TODO Fix this
            double result = 1-Math.pow(y.getPrimitive(), 2);
            return new Real(result);
        }

    }
    
    public static class Relu extends ActivationFunctions{
        public Relu(){

        }
        public Real evaluate(Real x){
            // TODO Fix this
            return new Real(Math.max(0,x.getPrimitive()));
        }
        public Real derivative(Real y){
            // TODO Fix this
            return new Real(y.getPrimitive()==0?0:1);
        }

    }
    
    public static class Identity extends ActivationFunctions{
        public Identity(){

        }

        public Real evaluate(Real v){
            // TODO Fix this
            return v;
        }
   
        public Real derivative(Real y){
            // TODO Fix this
            return new Real(1);
        }
    }
}
