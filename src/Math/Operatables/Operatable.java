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
public interface Operatable <T extends Operatable<T> > {

    public T add(T x);
    public T getProduct(T x);
    public T multiply(Real x);
    public T copy();
    
    public boolean isZero();
    public boolean isUnit();
     
}
