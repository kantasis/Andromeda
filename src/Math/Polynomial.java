/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math;

import java.util.Random;

/**
 *
 * @author kostis
 */
public class Polynomial extends Vector {
    
    public Polynomial(int length){
        super(length+1);
    }
    
    public Polynomial(double...x){
        super(x);
    }
    
    public int getOrder(){
        return getLength()+1;
    }
    
    public double evaluate(double x){
        double result = get(0);
        for (int i = 1; i<getLength(); i++)
            result+=get(i)*Math.pow(x, i);
        return result;
    }
    
    public Polynomial getDerivative(){
        Polynomial result = new Polynomial(getLength()-1);
        for (int i = 0; i<result.getLength(); i++)
            result.set(i, get(i+1)*(i+1));
        return result;
    }
    
    public Vector getRoots(){
        assert getOrder()>=1 : String.format("Can't find roots of 0 order polynomials");
        assert getLength()<=2 : String.format("Currently can only find roots of first and second order");
        Vector result = new Vector(getOrder());
        if (getOrder()==1)
            return new Vector(-get(0)/get(1));
        if (getOrder()==2){
            double delta = Math.pow(get(1),2)-4*get(0)*get(2);
            if (delta<0)
                return null;
            else
                return new Vector( (-get(1)+Math.sqrt(delta))/(2*get(2)),(-get(1)-Math.sqrt(delta))/(2*get(2)));
        }
        return result;
    }
    
    public Polynomial getHornerDiv(Polynomial that){
        assert that.getOrder()==1 : String.format("Can only do division with a monomial for now...");
        assert that.get(1)!=0 : String.format("Divisor should be a monomial, not just a constant");
        Polynomial result = new Polynomial(this.getOrder()-1);
        double temp=0;
        double r = -that.get(0)/that.get(1);
        //System.out.printf("%f\n",r);
        for (int i=result.getLength()-1;i>=0;i--){
            result.set(i, temp+this.get(i+1));
            temp=result.get(i)*r;
        }
        return result;
    }
    
    public Polynomial getHornerDiv(double root){
        return getHornerDiv(new Polynomial(-root,1));
    }
    
    public Double getNeutonRoot(double r){
        Polynomial derivative = getDerivative();
        //derivative.show("Derivative");
        double estimate=r;
        double previous;
        double f = evaluate(estimate);
        double f_;
        int maxIterations=300;
        //System.out.printf("Init: %f\n",r);
        while (f!=0){
            f = evaluate(estimate);
            f_ = derivative.evaluate(estimate);
            if (f_==0){
                f_=new Random().nextDouble()-0.5;
            }
            previous=estimate;
            estimate=estimate-f/f_;   
            if (estimate==previous || Math.abs(estimate-previous)<1e-10)
                break;
            if (maxIterations--<=0){
                System.out.printf("I give up!\n");
                System.out.printf("Error: %f\n",estimate-previous);
                System.out.printf("Value: %f\n",f);
                System.out.printf("Deriv: %f\n",f_);
                System.out.printf("Estim: %f\n",estimate);
                return null;
            }
        }
        return estimate;
    }
    

    public Polynomial getConv(Polynomial that){
        return super.getConv(that).toPolynomial();
    }
    
    public static Polynomial getByRoots(double... roots){
        Polynomial result = new Polynomial(-roots[0],1);
        for (int i=1;i<roots.length;i++)
            result=result.getConv(new Polynomial(-roots[i],1));
        return result;
    }
    
    public static Polynomial monomial(double r){
        return new Polynomial(-r,1);
    }
    
    public Polynomial getSum(Polynomial that){
        Polynomial result = new Polynomial(Math.max(this.getLength(), that.getLength()));
        for (int i=0;i<result.getOrder();i++){
            double a = (i<this.getLength())?this.get(i):0;
            double b = (i<that.getLength())?that.get(i):0;
            result.set(i, a+b);
        }
        return result;
    }
    
    public static Polynomial fit(Vector x, Vector y){
        assert x.getLength()==y.getLength() : String.format("X and Y sizes must match to fit into a polynomial");
        Polynomial result = new Polynomial(x.getLength());
        for (int i=0;i<result.getLength();i++){
            Polynomial p=new Polynomial();
            for (int j=0;j<result.getLength();j++){
                if (j==i) continue;
                p=p.getConv(Polynomial.monomial(x.get(j)));
            }
            p.times(y.get(i)/p.evaluate(x.get(i)));
            // since p will be strictly larger than the result
            result = result.getSum(p);
        }
        return result;
    }

    
    public static void main(String[] args){
        //Polynomial x = Polynomial.getByRoots(-1,0,1);
        Polynomial x = new Polynomial(-1,1,-1,1);
        //Polynomial x = new Polynomial(0,0,1);
        x.show();
//        x.getRoots().show();
//        x.getHornerDiv(new Vector(-1,1d)).show();
        double r = x.getNeutonRoot(new Random().nextInt(100));
        //double r = x.getNeutonRoot(1);
        System.out.printf("%f\n",r);
        
        x=x.getHornerDiv(r);
        x.show();
        r = x.getNeutonRoot(r);
        System.out.printf("%f\n",r);
        
        x=x.getHornerDiv(r);
        x.show();
        r = x.getNeutonRoot(r);
        System.out.printf("%f\n",r);
        
    }
    
}
