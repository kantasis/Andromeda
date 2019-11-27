/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning;

import Math.Vector;

public abstract class ActivationFunctions implements ActivationFunction{
    public abstract double evaluate(double x);
    public abstract double fast_derivative(double x);
    
    public boolean has_fast = false;
    
    public Vector evaluate(Vector x){
        Vector result = new Vector(x.getLength());
        for (int i=0;i<result.getLength();i++)
            result.set(i, this.evaluate(x.get(i)));
        return result;
    }
    
    public Vector derivative(Vector x){
        Vector result = new Vector(x.getLength());
        for (int i=0;i<result.getLength();i++)
            result.set(i, this.derivative(x.get(i)));
        return result;
    }
        
    public Vector fast_derivative(Vector y){
        assert has_fast : String.format("_fast_derivative invocation with !has_fast");
        Vector result = new Vector(y.getLength());
        for (int i=0;i<result.getLength();i++)
            result.set(i, this.fast_derivative(y.get(i)));
        return result;
    }
    
    public double derivative(double x){
        //assert has_fast : String.format("Tryign to compute fast_derivative with !has_fast");
        if (has_fast)
            return fast_derivative(evaluate(x));
        else
            return derivative(x);
    }
    
    public static class Sigmoid extends ActivationFunctions{
        public double _a = 1;
        public Sigmoid(double a){
            _a = a;
            has_fast=true;
        }
        public double evaluate(double v){
            return 1.0/(1+Math.exp(-v*this._a));
        }
        
        public double fast_derivative(double y){
            return y*(1-y);
        }
        
    }
    public static class Tanh extends ActivationFunctions{
        public double _a = 1;
        public Tanh(double a){
            _a = a;
            has_fast=true;
        }
        public double evaluate(double v){
            return Math.tanh(v*_a);
        }
                
        public double fast_derivative(double y){
            return 1-Math.pow(y, 2);
        }

    }
    
    public static class Relu extends ActivationFunctions{
        public Relu(){
            has_fast=true;
        }
        public double evaluate(double x){
            return Math.max(0,x);
        }
        public double fast_derivative(double y){
            return y==0?0:1;
        }

    }
    
    public static class Identity extends ActivationFunctions{
        public Identity(){
            has_fast=true;
        }
        public double evaluate(double v){
            return v;
        }
        public double fast_derivative(double y){
            return 1;
        }
    }
}
