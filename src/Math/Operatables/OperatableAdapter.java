/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math.Operatables;

/**
 *
 * @author kostis
 */
public abstract class OperatableAdapter <T extends Operatable<T> > 
        implements Operatable<T>{
    
    public T getAdd(T x){
        return (T) this.copy().add(x);
    }
        
    public T getMultiply(Real x){
        return this.copy().multiply(x);
    }
    
    public T multiply(double x){
        return multiply(new Real(x));
    }
    
    public T getMultiply(double x){
        return this.getMultiply(new Real(x));
    }

}
