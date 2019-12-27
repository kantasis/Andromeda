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
//import static Math.Operatables.Real.PRECISSION;
import java.util.Objects;

/**
 *
 * @author kostis
 */
public class Complex extends OperatableAdapter<Complex>{
    
    private MathContext _mc;
    public static final MathContext DEFAULT_MATHCONTEXT = Real.DEFAULT_MATHCONTEXT;
    public static final Complex ZERO = new Complex(0,0);
    public static final Complex ONE = new Complex(1,0);

    private BigDecimal _real;
    private BigDecimal _imag;
    
    public Complex(double a, double b){
        _mc=DEFAULT_MATHCONTEXT;
        _real=BigDecimal.valueOf(a);
        _imag=BigDecimal.valueOf(b);
    }
    
    public Complex(BigDecimal a, BigDecimal b){
        _mc=DEFAULT_MATHCONTEXT;
        _real=new BigDecimal(a.toString());
        _imag=new BigDecimal(b.toString());
    }

    public Complex(Real a, Real b){
        _mc=DEFAULT_MATHCONTEXT;
        _real=a.getBigDecimal();
        _imag=b.getBigDecimal();
    }
        
    public Complex setMathContext(MathContext x){
        if (x==null)
            _mc=DEFAULT_MATHCONTEXT;
        else
            _mc=x;
        this._setReal(this._getReal().round(_mc));
        this._setImag(this._getImag().round(_mc));
        //Logger.log("we've set the mathcontext to %d",_mc.getPrecision());
        return this;
    }
    
    public MathContext getMathContext(){
        return _mc;
    }

    
    public Complex copy(){
        return new Complex(getReal(),getImag()).setMathContext(getMathContext());
    }

    public Real getReal(){
        return new Real(_real).setMathContext(getMathContext());
    }
    
    public Real getImag(){
        return new Real(_imag).setMathContext(getMathContext());
    }
    
    private BigDecimal _getReal(){
        return _real;
    } 

    private BigDecimal _getImag(){
        return _imag;
    } 

    public Real getMag(){
        return getReal().sqr().add(getImag().sqr()).sqrt();
    }
    
    /*
    public Real getPhi(){
        return new Real(Math.atan2(_getImag().doubleValue(), _getReal().doubleValue()));
    }
    */
    
    public Complex getConjugate(){
        return this.copy().conjugate();
    }
    
    public Complex conjugate(){
        return _setImag(_getImag().negate());
    }
    
    /*
    public Complex setMag(Double mag){
        double phi = this.getPhi().getPrimitive();
        this.setReal(Math.cos(phi)*mag);
        this.setImag(Math.sin(phi)*mag);
        return this;
    }
    */
    
    /*
    public Complex setPhi(Double phi){
        double mag = this.getMag().getPrimitive();
        this.setReal(Math.cos(phi)*mag);
        this.setImag(Math.sin(phi)*mag);
        return this;
    }
    */
    
    public Complex power(int x){
        Complex result = Complex.ONE.copy();
        for (int i=0;i<x;i++)
            result=result.getProduct(this);
        this.setImag(result.getImag());
        this.setReal(result.getReal());
        
        
        /*
        setMag( getMag().power(x).getPrimitive());
        if (! isReal() )
            setPhi( getPhi().multiply(x).getPrimitive() );
        //System.out.printf("\t phi:%f\n", this.getPhi());
        */
        return this;
    }
    
    /*
    public Complex power(Real x){
        return this.power(x.getPrimitive());
    }
    */
    
    private Complex _setImag(BigDecimal x){
        _imag=x;
        return this;
    }

    public Complex setImag(Real x){
        return _setImag(x.getBigDecimal());
    }    
    
    private Complex _setReal(BigDecimal x){
        _real=x;
        return this;
    }

    public Complex setReal(Real x){
        return _setReal(x.getBigDecimal());
    }
    
    public Complex add(Complex that){
        this._setReal(_getReal().add(that._getReal(), getMathContext()));
        this._setImag(_getImag().add(that._getImag(), getMathContext()));
        return this;
    }
    
    public Complex multiply(Real that){
        this._setReal( this._getReal().multiply(that.getBigDecimal())); 
        this._setImag( this._getImag().multiply(that.getBigDecimal())); 
        return this;
    }
    
    public Complex getProduct(Complex that){
        BigDecimal result_real = this._getReal().multiply(that._getReal());
        result_real = result_real.subtract(this._getImag().multiply(that._getImag()));
        BigDecimal result_imag = this._getReal().multiply(that._getImag());
        result_imag = result_imag.add(   this._getImag().multiply(that._getReal()));
        return new Complex(result_real,result_imag).setMathContext(getMathContext());
    }
    
    /*
    public static Complex euler(Real angle){
        Complex result = Complex.unit();
        result.setPhi(angle.getPrimitive());
        return result;
    }
    */
    
    public boolean equals(Object that){
        if (this==that)
            return true;
        if (that==null)
            return false;
        if (!(that instanceof Complex))
            return false;
        Complex temp = (Complex) that;
        return this.equals(temp);
    }
    
    public int hashCode(){
        return Objects.hash(_real,_imag);
    }

    
    public boolean equals(Complex that){
        return this._getReal().compareTo(that._getReal())==0
            && this._getImag().compareTo(that._getImag())==0;
    }

    
    public boolean isReal(){
        return _getImag().compareTo(BigDecimal.ZERO)==0;
    }
    
    public boolean isZero(){
        return this.equals(this.ZERO);
    }
    
    public boolean isUnit(){
        return this.equals(this.ONE);
    }
    
    /*
    public static Complex unit(){
        return new Complex(Real.unit(),Real.zero());
    }
    
    public static Complex zero(){
        return new Complex(0,0);
    }
    */
    
    public String toCartesianString(){
        int x = getMathContext().getPrecision()+3;
        return String.format("[ (%"+x+"s) + i(%"+x+"s) ]{%s}", _getReal().toEngineeringString(),_getImag().toEngineeringString(),getMathContext().getPrecision());
    }
    
    /*
    public String toPolarString(){
        return String.format("[ |%5.2f | L:%7.2f]", getMag().getPrimitive(),getPhi().getPrimitive()/Math.PI*180);
    }
    */
    
    public String toString(){
        return this.toCartesianString();
        //return this.toPolarString();
        //return this.toCartesianString()+"\t"+this.toPolarString();
    }
    
    public void show(String title){
        Logger.log("%s %s",title,toString());
    }
    
    public void show(){
        this.show("");
    }
    
    public static void main(String[] args){
        Complex r = new Complex(1.0,0.0);
        Complex i = new Complex(0.0,-0.0);
        Complex rr = new Complex(-1.0,0.0);
        Complex ii = new Complex(0.0,-1.0);
        
        System.out.println(r.isReal());
        System.out.println(i.isReal());
        //i.power(2.0).show();
        //r.getProduct(i).show();
        
    }
    
}
