/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math;

/**
 *
 * @author kostis
 */
public interface Operatable <T extends Operatable<T> > {

     public Operatable getSum(T x);
     public Operatable getProduct(T x);
     public Operatable getProduct(Double x);
    
}
