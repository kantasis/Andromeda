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
public class Real implements Operatable<Real>{
    
    private Double _data;
    
    public Real(Double x){
        _data=x;
    }
    
    public Double get(){
        return _data;
    }
    
    public Real set(Double x){
        _data=x;
        return this;
    }
    
    public Real getSum(Real that){
        return new Real(this.get()+that.get());
    }
    
    public Real getProduct(Double x){
        return new Real(this.get()*x);
    }
    
    public Real getProduct(Real x){
        return this.getProduct(x.get());
    }
    
}
