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
public class Complex extends Real{
    
    private Double _imag;
    
    public Complex(Double a, Double b){
        super(a);
        _imag=b;
    }
    
    public Double getReal(){
        return super.get();
    }
    
    public Double getImag(){
        return _imag;
    }
    
    public Complex set(Double x){
        return null;
    }
    
    public Double get(){
        return null;
    }
    
    public Double getMag(){
        return Math.sqrt( Math.pow(getReal(),2) + Math.pow(getImag(),2));
    }
    
    public Double getPhi(){
        return Math.atan2(getImag(), getReal());
    }
    
    public Complex getConjugate(){
        return new Complex(getReal(),-getImag());
    }
    
    public Complex setMag(Double mag){
        double phi = this.getPhi();
        this.setReal(Math.cos(phi)*mag);
        this.setImag(Math.cos(phi)*mag);
        return this;
    }
    
    public Complex setPhi(Double phi){
        double mag = this.getMag();
        this.setReal(Math.cos(phi)*mag);
        this.setImag(Math.cos(phi)*mag);
        return this;
    }
    
    public Complex getPower(double x){
        this.setMag( Math.pow(this.getMag(),x) );
        this.setPhi( this.getPhi()*x );
        return this;
    }
    
    public Complex setImag(Double x){
        _imag=x;
        return this;
    }
    
    public Complex setReal(Double x){
        return (Complex) super.set(x);
    }
    
    public Complex getSum(Complex that){
        return new Complex(
                this.getReal()*that.getReal(),
                this.getImag()*that.getImag()
        );
    }
    
    public Complex getProduct(Double x){
        return new Complex(this.getReal()*x,this.getImag()*x);
    }
    
    public Complex getProduct(Complex that){
        return new Complex(
                this.getReal()*that.getReal() - this.getImag()*that.getImag(),
                this.getReal()*that.getImag() + this.getImag()*that.getReal()
        );
    }
    
}
