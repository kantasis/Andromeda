/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math.Operatables;

import Core.Logger;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 *
 * @author kostis
 */
public class Real extends OperatableAdapter<Real> implements Comparable<Real>{
    
    public static final int MAX_NEWTON_ITERATIONS=300;
    public static final int PRECISSION=4;
    
    private BigDecimal _data;
    private MathContext _mc;
    public static final MathContext DEFAULT_MATHCONTEXT = new MathContext(PRECISSION,RoundingMode.HALF_EVEN);

    
    
    public Real(BigDecimal x){
        // you can share the same object because it is immutable and 
        // copying is not required
        _mc=DEFAULT_MATHCONTEXT;
        _data=new BigDecimal(x.toString(),_mc);
    }
    
    public Real(double x){
        _mc=DEFAULT_MATHCONTEXT;
        _data=BigDecimal.valueOf(x);
    }
    
    public Real(String str){
        _mc=DEFAULT_MATHCONTEXT;
        _data=new BigDecimal(str);
    }
    
    public double getPrimitive(){
        return _data.doubleValue();
    }
    
    public Real setMathContext(MathContext x){
        //this.show("Before rounding");
        if (x==null)
            _mc=DEFAULT_MATHCONTEXT;
        else
            _mc=x;
        this.set(this._get().round(_mc));
        //this.show("After rounding");
        //Logger.log("we've set the mathcontext to %d",_mc.getPrecision());
        return this;
    }
    
    public BigDecimal getBigDecimal(){
        return new BigDecimal(_get().toString());
    }
    
    public Real set(BigDecimal x){
        _data=x;
        //if ( _data < PRECISSION && _data > -PRECISSION) _data=0.0;
        return this;
    }
    
    private BigDecimal _get(){
        return _data;
    }
    
    public MathContext getMathContext(){
        return _mc;
    }
    
    public Real add(Real that){
        return this.set(this._get().add(that._get(),getMathContext()));
    }
    
    public Real add(double value){
        return this.add(new Real(value));
    }
    
    public Real multiply(Real that){
        return this.set( this._get().multiply(that._get(), getMathContext()));
    }
    
    public Real multiply(double that){
        return this.multiply(new Real(that));
    }
    
    public Real getProduct(Real that){
        return this.copy().multiply(that);
    }
    
    public Real copy(){
        return new Real(_get()).setMathContext(getMathContext());
    }
    
    public boolean isZero(){
        return _get().compareTo(BigDecimal.ZERO)==0;
    }
    
    public boolean isUnit(){
        return _get().compareTo(BigDecimal.ONE)==0;
    }
    
    public int compareTo(Real that){
        return this._get().compareTo(that._get());
    }
    
    public static Real unit(){
        return new Real(BigDecimal.ONE);
    }
    
    public static Real zero(){
        return new Real(BigDecimal.ZERO);
    }
    
    public String toString(){
        //return String.format("[%5.2f]",this.getPrimitive());
        int x = getMathContext().getPrecision()+4;
        if (isZero())
            return String.format("[%"+x+"s]",0);
        else 
            return String.format("[%"+x+"s]",_get().toEngineeringString());
    }
    
    public void show(String title){
        Logger.log("%s %s",title,toString());
    }
    
    public void show(){
        this.show("");
    }
    
    public String toStringE(){
        return String.format("[%e]",this.getPrimitive());
    }
    
    public Complex getComplex(){
        return new Complex(this.getPrimitive(),0.0);
    }
    
    public boolean equals(Object that){
        if (this==that)
            return true;
        if (that==null)
            return false;
        if (!(that instanceof Real))
            return false;
        Real temp = (Real) that;
        return this.equals(temp);
    }
    
    public int hashCode(){
        return _data.hashCode();
    }

    public boolean equals(Real that){
        
        return this._get().compareTo(that._get())==0;
    }
    
    public Real power(int exponent){
        // TODO: Check the testcase new Real(-1).power(0.5)
        return set(_get().pow(exponent));
        
    }

    public Real getPower(int exponent){
        // TODO: Check the testcase new Real(-1).power(0.5)
        return copy().power(exponent);
    }
    
    public Real sqr(){
        return this.multiply(this);
    }
    
    public Real div(Real that){
        assert that.getPrimitive()!=0 : String.format("Error: Real division by zero");
        return set(this._get().divide(that._get(),getMathContext()));
    }

    public Real diff(Real that){
        return set(this._get().subtract(that._get(),getMathContext()));
    }

    public Real abs(){
        return set(_get().abs(getMathContext()));
    }
    
    
    public Real getSqrt(){
        return copy().sqrt();
    }
    
    public Real sqrt(){
        assert this.getPrimitive()>=0 : String.format("Tried to find the square root of a negative real");
        
        // These two are identity cases
        if (isZero() || isUnit())
            return this;
        
        Real guess = Real.unit();
        // (guess^2-this)/(2*guess)
        Real residual;
        int i=0;
        for (;i<MAX_NEWTON_ITERATIONS;i++){
            residual = guess.copy().power(2).minus(this).div(guess.copy().multiply(new Real(2)));
            if (residual.isZero())
                break;
            guess.minus(residual);
        }
        if (i==MAX_NEWTON_ITERATIONS){
            Logger.log(Logger.LL_ERROR, "Surpassed the max iterations and could not find sqrt %s -> %s", this,guess);
        }
        return set(guess.getBigDecimal());
    }
    
    
    /*
    public Real ln(){
        return set(Math.log(this.getPrimitive()));
    }
    */
    
    public Real inv(){
        assert getPrimitive()!=0 : String.format("Error: Real division by zero");
        return set(BigDecimal.ONE.divide(_get(),getMathContext()));
    }
    
    public Real getInv(){
        return this.copy().inv();
    }
    
    public Real negate(){
        set(_get().negate());
        return this;
    }

    public Real getNegative(){
        return this.copy().negate();
    }
    
    public Real minus(Real that){
        return diff(that);
    }

    public Real times(Real that){
        return multiply(that);
    }
    
    public Real round(){
        return round(getMathContext());
    }

    public Real round(MathContext mc){
        return this.set( _get().round(mc) );
    }

    /*
    public Real floor(){
        return this.set(Math.floor(this.getPrimitive()));
    }
    */
    
    /*
    public Real ceil(){
        return this.set(Math.ceil(this.getPrimitive()));
    }
    */
    
    /*
    public Real decimal(){
        return this.set(Math.ceil(this.getPrimitive()) - Math.floor(this.getPrimitive()));
    }
    */
    
    public static void unittest(){
        Real x = new Real(1);
        MathContext mc1 = new MathContext(15);
        MathContext mc2 = new MathContext(2);
        x.setMathContext(mc1);
        x.show();
        x.div(new Real(3));
        x.multiply(3);
        x.setMathContext(mc2);
        x.show();
        //x.sqrt().show();
    }
    
    public static void main(String[] args){
        
        Real x = new Real(3);
        
        
        x.show();
        x.multiply(5.0).negate();
        x.show();
        //unittest();
    }
}
