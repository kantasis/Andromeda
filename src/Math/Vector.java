package Math;

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
    
    private transient DataStructures.Dictionary<Integer,Double> _cache;

    /**
     * Simple constructor.
     * Creates a new Vector object with size n
     * 
     * @param n the size of the vector
     */
    public Vector(int n){
        super(n,1);
        assert n>0 : String.format("Cannot Initialize an empty Vector");
        
        _cache=new DataStructures.Dictionary();
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
        String result=String.format("(%3d)[ %6.2f",this.getLength(),this.get(0));
        for (int i=1;i<this.getLength();i++){
            result+=String.format(", %.2f",this.get(i));
        }
        return result+" ]";
    }
    
    /**
     * Present the data of the matrix in the console
     * 
     */
    public void show(){
        System.out.println(this.toString());
    }
    
    /**
     * Present the data of the matrix in the console along with its name
     * 
     * @param title a descriptive name for the vector
     */
    public void show(String title){
        System.out.print(title+":\t");
        show();
    }

    /**
     * Get the size of the vector
     * 
     * @return the element count of the vector
     */
    public int getLength(){
        return this.getRows();
    }
    
    /**
     * Return the value of the vector at the specified index
     * 
     * @param i element index
     * @return the value of the vector at index i
     */    
    public double get(int i){
        return get(i,0);
    }
    
    /**
     * Set the value of the vector at the specified index
     * 
     * @param i element index
     */
    public void set(int i, double v){
        this.assertIndexBound(i);
        set(i,0,v);
        //  here we can do a more smart updateCache, something like "if (newVal>max) update max;"
        _cache.clear();
    } 
    
    /**
     * Calculate the dot product of this vector and the argument vector
     * 
     * @param that the other vector
     * @return the real value of the dot product
     */    
    public double dot(Vector that){
        assertSizeAlignment(that);
        double result = 0;
        for (int i=0;i<this.getLength();i++)
            result+=this.get(i)*that.get(i);
        return result;
    }
    
    /**
     * Return the minimum value of the vector
     * 
     * @return the min value of the vector
     */    
    public double min(){
        int idx = _cache.indexOf(CACHE_MIN);
        if (idx!=-1)
            return _cache.getPair(idx).getValue();
        
        double result=this.getMinVector().get(0);
        _cache.set(CACHE_MIN, result);
        return result;    
    }

    /**
     * Return the maximum value of the vector
     * 
     * @return the max value of the vector
     */    
    public double max(){
        int idx = _cache.indexOf(CACHE_MAX);
        if (idx!=-1)
            return _cache.getPair(idx).getValue();
        
        double result=this.getMaxVector().get(0);
        _cache.set(CACHE_MAX, result);
        return result;
    }
    
    /**
     * Determine the index of the maximum value of the vector
     * 
     * @return the indec of the maximum value of the vector
     */    
    public int argMax(){
        int result = 0;
        double max=get(result);        
        for (int i=1;i<getLength();i++)
            if (get(i)>max){
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
    public double sum(){
        int idx = _cache.indexOf(CACHE_SUM);
        if (idx!=-1)
            return _cache.getPair(idx).getValue();
        
        double result=this.getSumVector().get(0);
        _cache.set(CACHE_SUM, result);
        return result;
    }
    
    /**
     * Calculate the sum of all the elements in the vector
     * 
     * @return the element-wise sum of the vector
     */
    public double product(){
        int idx = _cache.indexOf(CACHE_PRODUCT);
        if (idx!=-1)
            return _cache.getPair(idx).getValue();
        
        double result=this.getProductVector().get(0);
        _cache.set(CACHE_SUM, result);
        return result;
    }
    
    /**
     * Calculate the norm-2 of the vector
     * 
     * @return the Eucledian norm of the vector
     */
    public double norm(){
        int idx = _cache.indexOf(CACHE_NORM);
        if (idx!=-1)
            return _cache.getPair(idx).getValue();
        double result=0;
        for (int i=0;i<getLength();i++)
            result+=get(i)*get(i);
        _cache.set(CACHE_NORM, result);
        return Math.sqrt(result);
    }
    
    /**
     * Calculate the average of the elements in the vector
     * 
     * @return the average value of the elements in the vector
     */
    public double average(){
        return getSumVector().get(0)/getLength();
    }
    
    /**
     * Calculate the standard deviation of the elements in the vector
     * 
     * @return the std-dev of the vector
     */
    public double std(){
        double result = Math.sqrt(Math.pow(norm(),2)/getLength() - Math.pow(average(),2) );
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
    public Vector add(double this_v, Vector that, double that_v){
        assertSizeAlignment(that);
        for (int i=0;i<getLength();i++)
            this.set(i, this.get(i)*this_v+that.get(i)*that_v);
        return this;
    }
    
    /**
     * Return a nwe object with the 
     * weighted sum of this vector and the argument vector.
     * Multiplies the values of this vector by this_v and adds the values of
     * the argument vector multiplied by that_v. The result is stored in a new vector
     * 
     * @param this_v LHS factor
     * @param that the argument vector
     * @param that_v RHS factor
     * @return a new vector with the sum
     */
    public Vector getAdd(double this_v, Vector that, double that_v){
        return this.copy().add(this_v, that, that_v);
    }
    
    /** 
     * Add a real value to all the values of the vector
     * 
     * @param value the velue to add
     * @return this matrix
     */
    public Vector add(double value){
        for (int i=0;i<getLength();i++)
            this.set(i, this.get(i)+value);
        return this;
    }
        
    /**
     * Multiply all the values of the vector by a factor
     * 
     * @param factor the factor by which to multiply
     * @return this vector
     */
    public Vector times(double factor){
        for (int i=0;i<getLength();i++)
            this.set(i, this.get(i)*factor);
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
            this.set(i, this.get(i)*that.get(i));
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
     * Return a new vector with the product of this vector by the factor
     * 
     * @param factor the factor of the multiplication
     * @return the product matrix
     */
    public Vector multiply(double factor){
        return this.copy().times(factor);
    }
    
    /**
     * Return a copy of this matrix
     * 
     * @return a matrix with the same elements as this one
     */
    public Vector copy(){
        Vector result = new Vector(this.getLength());
        for (int i=0;i<getLength();i++)
            result.set(i, this.get(i));
        return result;
    }
    
    /**
     * Assertion whether this vector and another vector have the same size
     */
    public void assertSizeAlignment(Vector that){
        assert this.getLength()==that.getLength() : String.format("Incompatible Vectors (%d, %d)",this.getLength(),that.getLength());
    }
    
    /**
     * Assertion whether the index is within the length of the vector
     * 
     * @param i the element index
     */
    public void assertIndexBound(int i){
        assert i<getLength() && i>=0 : String.format("Trying to Access element %3d ( / %3d )",i,getLength());
    }
    
    /**
     * Compare this vector with another one
     * 
     * @param that the other vector
     * @return boolean value of the comparison
     */
    public boolean equals(Vector that){
        assertSizeAlignment(that);
        for (int i=0;i<getLength();i++)
            if (this.get(i)!=that.get(i))
                return false;
        return true;
    }
    
    /**
     * Calculate the difference of this vector with another one
     * 
     * @param that the other vector
     * @return this vector
     */
    public Vector diff(Vector that){
        assertSizeAlignment(that);
        return this.add(1, that, -1);
    }
    
    /**
     * Calculate the difference of this vector with another one and return a new
     * vector with the result
     * 
     * @param that the other matrix
     * @return this matrix
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
        for (int i=0; i<result.getLength(); i++)
            result.set(i, this.get(i+1)-this.get(i) );
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
            result.set(i, this.get(i-1)+this.get(i) );
        return result;
    }
    
    /**
     * Calculate the power of each element
     * 
     * @param p the exponent
     * @return this matrix
     */
    public Vector power(float p){
        for (int i=0;i<this.getLength();i++)
            this.set(i, Math.pow(this.get(i),p));
        return this;
    }
    
    /**
     * Calculate the absolute value of the vector
     * @return this vector
     */
    public Vector abs(){
        for (int i=0;i<this.getLength();i++)
            this.set(i, Math.abs(this.get(i)));
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
    public double[] toArray(){
        double[] result = new double[this.getLength()];
        for (int i=0;i<result.length;i++)
            result[i]=this.get(i);
        return result;
    }
    
    
    // TODO: Maybe these two functions should go away
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
    
    // TODO: I need to review this and see if 
    /**
     * Apply a function to all elements of the vector
     * @param f the function
     * @return this vector
     */
    public Vector _apply(Function f){
        for (int i=0;i<this.getLength();i++)
            this.set(i, f.run(this.get(i)));
        return this;
    }
    
    public static interface Function{
        public double run(double v);
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
            double sum=0;
            int start = Math.max(0, i-that.getLength()+1);
            int stop = Math.min(this.getLength()-1,i);
            for (int j=start;j<=stop;j++)
                sum+=this.get(j)*that.get(i-j);
            result.set(i,sum);

        }
        return result;
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
        return new Polynomial(this.toArray());
    }
        
    public static void main(String[] args){
        
    }
}
