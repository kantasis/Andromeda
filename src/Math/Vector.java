package Math;

import Core.Logger;
import Math.Operatables.Real;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author GeorgeKantasis
 * Note: This class is considered a Column Matrix
 */
public class Vector extends Matrix  {
    
    // TODO: toArray
    // TODO: conv
    // TODO: Find Eigenvalues
    
    public static final int CACHE_MAX=1;
    public static final int CACHE_MIN=2;
    public static final int CACHE_SUM=3;
    public static final int CACHE_NORM=5;
    public static final int CACHE_PRODUCT=7;
    
    
    /**
     * Simple constructor.
     * Creates a new Vector object with size n
     * 
     * @param n the size of the vector
     */
    public Vector(int n){
        super(n,1);
        assert n>0 : String.format("Cannot Initialize an empty Vector");
        
    }
    
    /**
     * Vector constructor given an array of doubles.
     * Creates a new Vector instance with the elements of the given array
     * 
     * @param x the array of the elements in the vector
     */
    public Vector(double...x){
        this(x.length);
        for (int i=0;i<this.getLength();i++)
            this.set(i, x[i]);
    }
    
    /**
     * Vector constructor given an array of Reals.
     * Creates a new Vector instance with the elements of the given array
     * 
     * @param x the array of the elements in the vector
     */
    public Vector(Real...x){
        this(x.length);
        for (int i=0;i<this.getLength();i++)
            this.set(i, x[i]);
    }
    
    /**
     * Vector constructor given an array of doubles.
     * Creates a new Vector instance with the elements of the given ArrayList
     * 
     * @param x the ArrayList of the elements in the vector
     */
    public Vector (ArrayList <Double> x){
        this(x.size());
        for (int i=0;i<this.getLength();i++)
            this.set(i, x.get(i));     
    }
    
    /**
     * Demonstrate the Vector as a String.
     * Provides a string representation of the vector object for presentation 
     * purposes
     * 
     * @return a string object that represents the vector
     */
    public String toString(){
        String result=String.format("(%3d)[ %s",this.getLength(),this.get(0));
        for (int i=1;i<this.getLength();i++){
            result+=String.format(", %s",this.get(i));
        }
        return result+" ]";
    }
    
    /**
     * Present the data of the matrix in the console
     * 
     */
    public void show(){
        show("");
    }
    
    /**
     * Present the data of the matrix in the console along with its name
     * 
     * @param title a descriptive name for the vector
     */
    public void show(String title){
        Logger.log("%s %s",title,this);
        
    }

    /**
     * Get the size of the vector
     * 
     * @return the element count of the vector
     */
    public int getLength(){
        return this.getRowCount();
    }
    
    /**
     * Return the value of the vector at the specified index
     * 
     * @param i element index
     * @return the value of the vector at index i
     */    
    public Real get(int i){
        return get(i,0);
    }

    /**
     * Set the value of the vector at the specified index
     * 
     * @param i element index
     */
    public Vector set(int i, Real v){
        return (Vector) set(i,0,v);
    } 
    
    /**
     * Set the value of the vector at the specified index
     * 
     * @param i element index
     */
    public Vector set(int i, double v){
        return (Vector) set(i,0,v);
    } 
    
    /**
     * Calculate the dot product of this vector and the argument vector
     * 
     * @param that the other vector
     * @return the real value of the dot product
     */    
    public Real getDot(Vector that){
        assertSizeAlignment(that);
        Real result = Real.zero();
        for (int i=0;i<this.getLength();i++)
            result.add((Real)this.get(i).getMultiply(that.get(i)));
        return result;
    }
    
    /**
     * Return the minimum value of the vector
     * 
     * @return the min value of the vector
     */    
    public Real min(){
        Real result=this.getMinVector().get(0);
        return result;    
    }

    /**
     * Return the maximum value of the vector
     * 
     * @return the max value of the vector
     */    
    public Real max(){
        Real result=this.getMaxVector().get(0);
        return result;
    }
    
    /**
     * Determine the index of the maximum value of the vector
     * 
     * @return the indec of the maximum value of the vector
     */    
    public int argMax(){
        int result = 0;
        Real max=get(result);        
        for (int i=1;i<getLength();i++)
            if (this.get(i).getPrimitive()>max.getPrimitive()){
                result=i;
                max=get(result);
            }
        return result;
    }
    
    /**
     * Calculate the sum of all the elements in the vector
     * 
     * @return the element-wise sum of the vector
     */
    public Real sum(){
        Real result=this.getSumVector().get(0);
        return result;
    }
    
    /**
     * Calculate the sum of all the elements in the vector
     * 
     * @return the element-wise sum of the vector
     */
    public Real product(){
        Real result=this.getRowProduct().get(0);
        return result;
    }
    
    /**
     * Calculate the norm-2 of the vector
     * 
     * @return the Eucledian norm of the vector
     */
    public Real getSumofSquares(){
        return this.copy().power(2).sum();
    }
    
    /**
     * Calculate the norm-2 of the vector
     * 
     * @return the Eucledian norm of the vector
     */
    public Real getNorm(){
        return getSumofSquares().sqrt();
    }

    
    /**
     * Calculate the average of the elements in the vector
     * 
     * @return the average value of the elements in the vector
     */
    public Real average(){
        return getSumVector().get(0).multiply(1.0/getLength());
    }
    
    /**
     * Calculate the standard deviation of the elements in the vector
     * 
     * @return the std-dev of the vector
     */
    public Real std(){
        Real result = this.getSumofSquares().add(this.average().power(2).multiply(-1.0));
        return result;
    }
    
    /**
     * Weighted sum of this vector and the argument vector.
     * multiplies the values of this vector by this_v and adds the values of
     * the argument vector multiplied by that_v
     * 
     * @param this_v LHS factor
     * @param that the argument vector
     * @param that_v RHS factor
     * @return this vector
     */
    public Vector weightedSum(Real this_v, Vector that, Real that_v){
        assertSizeAlignment(that);
        Vector thus = (Vector) that.getMultiply(that_v);
        this.multiply(this_v).add(thus);
        return this;
    }
    
    /** 
     * Add a real value to all the values of the vector
     * 
     * @param value the velue to add
     * @return this matrix
     */
    public Vector add(double value){
        return (Vector) add(new Real(value));
    }
    
    /**
     * Add a value to each element.
     * 
     * @param value the value to add
     * @return this
     */
    public Vector add(Vector that){
        for (int i=0;i<getLength();i++)
            this.get(i).add(that.get(i));
        return this;
    }
    
    /**
     * Multiply the elements of this matrix by the argument vector
     * 
     * @param that the argument vector with the factors
     * @return this vector
     */
    public Vector multiplyElements(Vector that){
        this.assertSizeAlignment(that);
        for (int i=0;i<getLength();i++)
            this.get(i).multiply(that.get(i));
        return this;
    }
    
    /**
     * Divide all elements of this matrix by a Real number
     * 
     * @return the average value of the elements in the vector
     */
    public Vector div(Real factor){
        for (int i=0;i<getLength();i++)
            this.get(i).div(factor);
        return this;
    }
    
    
    /**
     * Return a new vector with the product of this and the argument vector
     * 
     * @param that the argument vector with the factors
     * @return the product vector
     */
    public Vector getMultipliedElements(Vector that){
        Vector result = this.copy();
        return result.multiplyElements(that);
    }
        
    /**
     * Return a copy of this Vector
     * 
     * @return a vector with the same elements as this one
     */
    public Vector copy(){
        Vector result = new Vector(this.getRowCount());
        for (int i=0;i<result.getRowCount();i++)
            result.set(i,this.get(i).copy());
        return result;
    }
    
    /**
     * Calculate the difference of this vector with another one
     * 
     * @param that the other vector
     * @return this vector
     */
    public Vector diff(Vector that){
        assertSizeAlignment(that);
        for (int i=0;i<getLength();i++)
            this.get(i).diff(that.get(i));
        return this;
    }
    
    /**
     * Subtract a Real number from all elements
     * 
     * @param that the other vector
     * @return this vector
     */
    public Vector diff(Real that){
        for (int i=0;i<getLength();i++)
            this.get(i).diff(that);
        return this;
    }
    
    /**
     * Calculate the difference of this vector with another one and return a new
     * vector with the result
     * 
     * @param that the other vector
     * @return this vector
     */
    public Vector getDiff(Vector that){
        return this.copy().diff(that);
    }
    
    /**
     * Calculate the differential of this vector
     * @return a new vector where each element is subtracted by the previous
     */
    public Vector getDiff(){
        Vector result = new Vector(this.getLength()-1);
        for (int i=0; i<result.getLength(); i++){
            Real curr = this.get(i+1);
            Real minus_prev = this.get(i).copy().multiply(-1.0);
            result.get(i).add(curr).add(minus_prev);
        }
        return result;
    }     
    
    /**
     * Calculate the cumulative sum of the vector
     * 
     * @return a new vector where each element is added by the previous 
     */
    public Vector getCumsum(){
        Vector result = new Vector(this.getLength());
        result.set(0,this.get(0));
        for (int i=1; i<result.getLength(); i++)
            result.get(i).add(result.get(i-1)).add(this.get(i));
        return result;
    }
    
    /**
     * Calculate the power of each element
     * 
     * @param p the exponent
     * @return this matrix
     */
    public Vector power(int p){
        for (int i=0;i<this.getLength();i++)
            this.get(i).power(p);
            //this.set(i, Math.pow(this.get(i).get(),p));
        return this;
    }
    
    /**
     * Calculate the square root of each element
     * 
     * @return this
     */
    public Vector sqrt(){
        for (int i=1;i<getLength();i++)
            this.get(i).sqrt();
        return this;
    }
    
    /**
     * Calculate the absolute value of the vector
     * @return this vector
     */
    public Vector abs(){
        for (int i=0;i<this.getLength();i++)
            this.set(i, Math.abs(this.get(i).getPrimitive()));
        return this;
    }
    
    /**
     * Return a new vector with the absolute values of each element
     * @return the result vector
     */
    public Vector getAbs(){
        return this.copy().abs();
    }
    
    /**
     * Get a new array of doubles with the values of this vector
     * @return an array with the elements of the vector
     */
    public double[] getPrimitiveWeights(){
        double[] result = new double[this.getLength()];
        for (int i=0;i<result.length;i++)
            result[i]=this.get(i).getPrimitive();
        return result;
    }
    
    
    /**
     * Create a new row matrix with the vector values
     * @return a new row matrix with the vector values
     */
    public Matrix getAsRowMatrix(){
        Matrix result = new Matrix(1,this.getLength());
        for (int i=0;i<this.getLength();i++)
            result.set(0, i, this.get(i));
        return result;
    }

    /**
     * Create a new row matrix with the vector values
     * @return a new row matrix with the vector values
     */
    public Matrix getAsColMatrix(){
        Matrix result = new Matrix(this.getLength(),1);
        for (int i=0;i<this.getLength();i++)
            result.set(i, 0, this.get(i));
        return result;
    }
   
    /**
     * Concatenate two vectors
     * 
     * @param that the other vector
     * @return a new vector with concatenated with the argument
     */
    public Vector merge(Vector that){
        Vector result = new Vector(this.getLength()+that.getLength());
        int i=0;
        for (int j=0;j<this.getLength();j++)
            result.set(i++, this.get(j));
        for (int j=0;j<that.getLength();j++)
            result.set(i++, that.get(j));
        return result;
    }
    
    /**
     * Get the convolution of this matrix with another one
     * 
     * @param that the other matrix
     * @return the convolution matrix
     */
    public Vector getConv(Vector that){
        Vector result = new Vector(this.getLength()+that.getLength()-1);
        for (int i=0; i<result.getLength(); i++){
            Real sum=Real.zero();
            int start = Math.max(0, i-that.getLength()+1);
            int stop = Math.min(this.getLength()-1,i);
            for (int j=start;j<=stop;j++)
                sum.add((Real)this.get(j).getMultiply(that.get(i-j)));
                //sum+=this.get(j)*that.get(i-j);
            result.set(i,sum);
        }
        return result;
    }

    /**
     * Return whether this is a unit matrix
     * 
     * @return true if this is a unit matrix, false otherwise
     */    
    public boolean isUnit(){
        boolean unit_found = false;
        for(int i=0;i<this.getLength();i++){
            if (get(i).isUnit())
                if (!unit_found)
                    unit_found=true;
                else
                    return false;
            else if (!get(i).isZero())
                return false;
        }
        return unit_found;
    }

    /**
     * Load the vector values from a file
     * 
     * @param filename the filename where the data are stored
     * @return the loaded vector
     */
    public static Vector load(String filename){
        Vector result;
        try {
         FileInputStream fileIn = new FileInputStream(filename);
         ObjectInputStream in = new ObjectInputStream(fileIn);
         result = (Vector) in.readObject();
         in.close();
         fileIn.close();
         return result;
      } catch (IOException i) {
         i.printStackTrace();
         return null;
      } catch (ClassNotFoundException c) {
         System.out.println("Employee class not found");
         c.printStackTrace();
         return null;
      }
    }
    
    /**
     * Convert this vector to a polynomial
     * @return a polynomial with the values of this vector as factor
     */
    public Polynomial toPolynomial(){
        assert getLength()>1 : String.format("Can't convert vector to polynomial ...");
        return new Polynomial(this.getPrimitiveWeights());
    }
        
    public static void main(String[] args){
        
    }
}
