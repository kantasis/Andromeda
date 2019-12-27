/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math;

import Core.Logger;
import java.util.Random;
import Math.Operatables.Real;
import Math.Operatables.Complex;
import Math.Operatables.OperatableAdapter;
import java.math.MathContext;
import java.math.RoundingMode;


/**
 *
 * @author kostis
 */
public class Polynomial extends OperatableAdapter<Polynomial> {
    
    private Vector _data;
    public static final int MAX_NEUTON_ITERATIONS=10000;
    
    public Polynomial(int order){
        _data = new Vector(order+1);
    }
    
    public Polynomial(double...x){
        this(new Vector(x));
    }
    
    public Polynomial(Real...x){
        this(x.length-1);
        for(int i=0;i<x.length;i++)
            setFactor(i,x[i]);
    }
    
    public Polynomial(Vector factors){
        _data = factors;
    }
    
    private Vector _getData(){
        return _data;
    }
    
    public int getOrder(){
        int result = getFactorCount()-1;
        for (int i=result;i>0;i--) {
            if (!factor(i).isZero())
                break;
            result--;
        }
        return result;
    }
    
    public int getFactorCount(){
        return _getData().getLength();
    }
    
    public Real factor(int i){
        if (i>this.getFactorCount())
            return Real.zero();
        return _getData().get(i);
    }
    
    public Polynomial setFactor(int i, Real v){
        _getData().set(i, v);
        return this;
    }
    
    public Real evaluate(Real x){
        return evaluate(x.getComplex());
    }
    
    public Real evaluate(double x){
        return evaluate(new Complex(x,0.0));
    }        
        
    public Real evaluate(Complex x){
        // TODO: This may become faster if you use Horner's evaluation
        MathContext mc = new MathContext(x.getMathContext().getPrecision()*2,RoundingMode.HALF_EVEN);
        Complex result = this.factor(0).getComplex();
        Complex x2 = Complex.ONE;
        result.setMathContext(mc);
        x2.setMathContext(mc);
        for (int i = 1; i<getFactorCount(); i++){
            x2=x2.getProduct(x);
//            Logger.log("x^i = %s^%d=%s",x,i,x2);
            if (factor(i).isZero())
                continue;
            Complex sum=x2.getProduct(factor(i).getComplex());
//            Logger.log("This term: %s * %s = %s",x2,factor(i),sum);
            result.add(sum);
            //Logger.log("Running sum:%s",result.getReal());
            
        }
        assert(result.isReal()):String.format("Can't properly evaluate Complex results yet (%s).\nPoly: %s\nX: %s",result,this,x);
        Real final_result = result.getReal();
//        result.show("result");
//        final_result.show("origi");
        final_result.setMathContext(null);
//        final_result.show("final");
        //Logger.log("final_result: %s\t",final_result);
        return final_result;
    }
    
    public Polynomial getDerivative(){
        Polynomial result = new Polynomial(this.getOrder()-1);
        for (int i = 0; i<result.getFactorCount(); i++)
            result.setFactor(i, (Real) this.factor(i+1).getMultiply(i+1.0));
        return result;
    }
    
    public Polynomial add(Polynomial that){
        // TODO: the two polynomials may not be of the same size
//        _getData().add(that._getData());
        
        Vector new_factors = new Vector(Math.max(this.getFactorCount(),that.getFactorCount()));
        for (int i=0;i<new_factors.getLength();i++){
            Real sum = (i<this.getFactorCount())?factor(i):Real.zero();
            sum.add((i<that.getFactorCount())?that.factor(i):Real.zero());
            new_factors.set(i, sum);
        }
        _data=new_factors;
        return this;
    }
    
    public Polynomial multiply(Real x){
        _getData().multiply(x);
        return this;
    }
    
    public Polynomial getProduct(Polynomial that){
        Vector factors = _getData().getConv(that._getData());
        return new Polynomial(factors);
    }
    
    public Polynomial copy(){
        Polynomial result = new Polynomial(this.getOrder());
        for (int i = 0; i<result.getFactorCount(); i++)
            result.setFactor(i, this.factor(i).copy());
        return result;
    }
    
    public boolean isZero(){
        return _getData().isZero();
    }

    public boolean isUnit(){
        return this.getOrder()==1 && this.factor(0).isUnit();
    }
  
    public Polynomial getHornerDiv(Polynomial that){
        assert that.getOrder()==1 : String.format("Can only do division with a monomial for now...");
        assert !that.factor(1).isZero() : String.format("Divisor should be a monomial, not just a constant");
        Polynomial result = new Polynomial(this.getOrder()-1);
        Real temp=Real.zero();
        Real r = that.factor(0).copy().div(that.factor(1));
        for (int i=result.getFactorCount()-1;i>=0;i--){
            result.setFactor(i, this.factor(i+1).getAdd(temp));
            temp=result.factor(i).getMultiply(r).negate();
        }
        return result;
    }
    
    public Polynomial getHornerDiv(Real root){
        return getHornerDiv(Polynomial.monomial(root));
    }
    
    // One day I will explain the thinking behind this reasoning
    public Real getNeutonRoot(){
        int ord = getOrder();
        assert ord >=2 ;
        Real estimate = factor(ord-1).copy().negate().div(factor(ord).copy().multiply(ord));
        return getNeutonRoot(estimate);
    }
    
    public Real getNeutonRoot(Real r){
        Logger.log("Getting newton root of: %s",this);
        Logger.log("Initial estimation: %s",r);

        Polynomial derivative = getDerivative();
        Real estimate=r.copy();
        Real previous;
        Real f = evaluate(estimate);
        Real f_;
        Random rnd = new Random(); // just in case
        int maxIterations=MAX_NEUTON_ITERATIONS;
        //System.out.printf("Init: %f\n",r);
        while (!f.isZero()){
            //Logger.log("%d f:  %s\t%s",MAX_NEUTON_ITERATIONS-maxIterations  ,f,estimate);
            f_ = derivative.evaluate(estimate);
            if (f_.isZero()){
                Logger.log(Logger.LL_WARNING, "Iteration %d\t Encountered a zero-derivative point at %s -> %s",MAX_NEUTON_ITERATIONS-maxIterations , estimate,f);
                Logger.log(Logger.LL_WARNING, "Will handle by wiggling out a bit");
                f_.add((rnd.nextDouble()-0.5)).multiply(f);
            }
            previous=estimate.copy();
            estimate.minus(f.copy().div(f_));   // estimate-=f/f_
            if (maxIterations--<=0){
                System.out.printf("I give up!\n");
                //System.out.printf("Error: %e\n",estimate-previous);
                System.out.printf("Value: %e\n",f);
                System.out.printf("Deriv: %e\n",f_);
                System.out.printf("Estim: %f\n",estimate);
                return null;
            }
            f = evaluate(estimate);
        }
        Logger.indent();
            Logger.log("Found root at %s, Iteration %d",estimate,MAX_NEUTON_ITERATIONS-maxIterations);
            Logger.log("f:  %s",f);
        Logger.dedent();
        return estimate;
    }
    
        public Vector getRoots(){
        //System.out.println("Get the roots of:");
        //this.show();
        Vector result = new Vector(getOrder());
        Polynomial subnomial = this.copy();
        Real estimation = Real.zero();
        for (int i=0;i<getOrder();i++){
            Real root;
            if (subnomial.getOrder()<=2){
                //System.out.println("Going for the quadratic solution of");
                subnomial.show("Subnomial");
                Vector roots = subnomial.getQuadraticRoots();
                for(int duo=0;duo<roots.getLength();duo++){
                    root=roots.get(duo);
                    result.set(i+duo, root);
                    subnomial=subnomial.getHornerDiv(root);
                    estimation=root;
                }
                // This is cheating...
                i++;
            }else{
                root = subnomial.getNeutonRoot();
                Real val = Real.zero();
                // Remove the root from the polynomial as many times as it takes
                while (val.isZero()){
                    result.set(i, root);
                    i++;
                    subnomial=subnomial.getHornerDiv(root);
                    val = subnomial.evaluate(root);
                }
                i--;
                estimation=root;
            }
        }
        return result;
    }
    
    public Vector getQuadraticRoots(){
        int order_cache = getOrder();
        Logger.log("Getting quadratic roots: %d",order_cache);
        assert order_cache>=1 : String.format("Can't find roots of 0 order polynomials");
        assert order_cache<=2 : String.format("This method can only find roots of first and second order");
        Real c=factor(0);
        Real b=factor(1);
        
        if (order_cache==1)
            return new Vector(c.multiply(-1).div(b));
        Real a=factor(2);
        Real delta = b.getPower(2).diff(a.getMultiply(c).getMultiply(4));
        if (delta.compareTo(Real.zero())<0){
            Logger.log(Logger.LL_ERROR,"No Real root found. This will cause an exception");
            return null;
        }
        Real result1 = b.copy().negate().add(delta.getSqrt()).div(a.getMultiply(2));
        Real result2 = b.copy().negate().diff(delta.getSqrt()).div(a.getMultiply(2));
        this.show();
        a.show("a");
        b.show("b");
        c.show("c");
        delta.getSqrt().show("d");
        result1.show();
        result2.show();
        return new Vector( result1, result2 );
    }
    
    public static Polynomial getByRoots(double... roots){
        Polynomial result = Polynomial.monomial(roots[0]);
        for (int i=1;i<roots.length;i++)
            result=result.getProduct(new Polynomial(-roots[i],1));
        return result;
    }
    
    public static Polynomial monomial(double r){
        
        return new Polynomial(-r,1);
    }
    
    public static Polynomial monomial(Real r){
        return new Polynomial( r.copy().negate() , Real.unit() );
    }
    
    public static Polynomial fit(Vector x, Vector y){
        assert x.getLength()==y.getLength() : String.format("X and Y sizes must match to fit into a polynomial");
        Polynomial result = new Polynomial(x.getLength());
        for (int i=0;i<result.getFactorCount();i++){
            Polynomial p=new Polynomial(0);
            for (int j=0;j<result.getFactorCount();j++){
                if (j==i) continue;
                p=p.getProduct(Polynomial.monomial(x.get(j)));
            }
            p.multiply(y.get(i).getPrimitive()/p.evaluate(x.get(i)).getPrimitive());
            // since p will be strictly larger than the result
            result.add(p);
        }
        return result;
    }
    
    public String toString(){
        //String result = String.format("%6.2f ", this.getFactor(0).getPrimitive());
        String result = String.format("%s ", this.factor(0));
        for (int i=1;i<this.getFactorCount();i++)
            result+=String.format("+ %sx^%d ", this.factor(i),i);
        return result;
    }
    
    public void show(){
        show("");
    }
    public void show(String name){
        Logger.log("%s \t%s",name,this.toString());
//        for (int i=0;i<getFactorCount();i++){
//            System.out.printf("%e, ", getFactor(i).getPrimitive() - Math.round(getFactor(i).getPrimitive()));
//        }
//        System.out.println();
    }
    
    public static Polynomial unit(){
        return new Polynomial(1.0);
    }
    
    public static Polynomial zero(){
        return new Polynomial(0.0);
    }
    
    public static void unittest(){
        Polynomial poly = new Polynomial(16,12,0,-1);
        //Polynomial poly = new Polynomial(-6,1,1);
        poly.show("Polynomial");
        poly.getRoots().show("Roots");
    }
    
    public static void main(String[] args){
        unittest();
        
        Polynomial poly = new Polynomial(16,12,0,-1);
        Real r = new Real("-1.98");
        poly.evaluate(r).show();
        
    }
    
}
