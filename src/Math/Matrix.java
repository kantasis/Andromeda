package Math;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

public class Matrix implements java.io.Serializable {
    
    private double[][] _data;
    private Matrix _mirror;
  
    /**
     * Constructor for the matrix class
     * Create a new matrix defining the number of rows and columns
     * 
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     */
    public Matrix(int rows,int columns){
        _data=new double[rows][columns];
    }
    
    /**
     * Constructor for the matrix class
     * Create a new matrix defining the data array. Note that it creates a new
     * array for that purpose
     * 
     * @param data the 2D array that contains the data of the new matrix
     */
    public Matrix(double[][] data){
        this(data.length,data[0].length);
        for(int i=1;i<data.length;i++)
            assert data[i-1].length==data[i].length : String.format("Can't create matrix out of array. Inconsistent dimensions");
        for(int i=0;i<data.length;i++){
            for(int j=0;j<data.length;j++){
                _data[i][j]=data[i][j];
            }
        }
    }
        
    /**
     * Get the number of columns of this matrix
     * 
     * @returns an integer representing the number of columns in the matrix
     */
    public final int getColumns(){
        return _data[0].length;
    }
    
    /**
     * Get the number of rows of this matrix
     * 
     * @returns an integer representing the number of rows in the matrix
     */
    public final int getRows(){
        return _data.length;
    }
    
    /**
     * Get the mirrored matrix.
     * Highly specialized function, this one returns the matrix which will
     * undergo the same row operations as this one whenever rowOperation 
     * is called
     * 
     * @returns the mirror matrix
     */
    public Matrix getMirror(){
        return _mirror;
    }
    
    /**
     * Sets the mirrored matrix.
     * Highly specialized function, this one sets the matrix which will
     * undergo the same row operations as this one whenever rowOperation
     * is called
     * 
     * @param that the mirrored matrix
     * @return this
     */
    public Matrix setMirror(Matrix that){
        _mirror = that;
        return this;
    }
    
    
    /**
     * Return the value of the matrix at the specified index
     * 
     * @param i row index
     * @param j column index
     * @return the value of the matrix at row i, column j
     */
    public double get(int i, int j){
        assertIndexBound(i,j);
        return _data[i][j];
    }
    
    /**
     * Sets the value of the matrix at the specified index.
     * 
     * @param i row index
     * @param j column index
     * @param v the value
     * @return this
     */
    public Matrix set(int i, int j, double v){
        assertIndexBound(i,j);
        _data[i][j]=v;
        return this;
    }
    
    /**
     * Present the data of the matrix in the console
     * 
     */
    public void show(){
        System.out.printf("[%d %d]\n",getRows(),getColumns());
        for (int i=0;i<getRows();i++){
            for (int j=0;j<getColumns();j++)
                System.out.printf("%5.2f ",get(i,j));
            System.out.printf("\n");
        }
    }
    
    /**
     * Assign to this matrix the weighted sum of this and another matrix
     * this = this*this_v + that*that_v
     * 
     * @param this_v the factor this matrix will be multiplied by
     * @param that the other matrix
     * @param that_v the factor that matrix will be multiplied by
     * @return this
     */
    public Matrix add(double this_v, Matrix that, double that_v){
        assertSizeAlignment(that);
        for (int i=0;i<getRows();i++)
            for (int j=0;j<getColumns();j++)
                this.set(i,j,this.get(i, j)*this_v+that.get(i, j)*that_v);
        return this;
    }
    
    /**
     * Add a vector to each row.
     * this function helps with normalization, i.e. subtract by the mean of a 
     * signal
     * 
     * @param values the vector to add
     * @return this
     */
    public Matrix sum(Vector values){
        assert values.getLength()==this.getColumns() : String.format("Incompatible sizes %s / %s",this.getColumns(),values.getLength());
    
        for (int i=0;i<getRows();i++)
            for (int j=0;j<getColumns();j++)
                this.set(i,j,this.get(i, j)+values.get(j));
        return this;
    }
    
    /**
     * Add a value to each element.
     * 
     * @param value the value to add
     * @return this
     */
    public Matrix add(double value){
        for (int i=0;i<getRows();i++)
            for (int j=0;j<getColumns();j++)
                this.set(i,j,this.get(i, j)+value);
        return this;
    }
    
    /**
     * Multiply each element by a value.
     * 
     * @param factor the value to multiply by
     * @return this
     */
    public Matrix multiply(double factor){
        for (int i=0;i<getRows();i++)
            for (int j=0;j<getColumns();j++)
                this.set(i,j,this.get(i, j)*factor);
        return this;
    }
    
    /**
     * Sum all columns into a vector
     * 
     * @return the vector sum of this matrix
     */
    public Vector getSumVector(){
        Vector result = new Vector(this.getColumns());
        for (int j=0;j<this.getColumns();j++){
            double sum=0;
            for (int i=0;i<getRows();i++)
                sum+=this.get(i,j);
            result.set(j,sum);
        }
        return result;
    }
    
    /**
     * Multiply all columns into a vector
     * 
     * @return the vector product of this matrix
     */
    public Vector getProductVector(){
        Vector result = new Vector(this.getColumns());
        for (int j=0;j<this.getColumns();j++){
            double sum=1;
            for (int i=0;i<getRows();i++)
                sum*=this.get(i,j);
            result.set(j,sum);
        }
        return result;
    }
    
    /**
     * Average all columns into a vector
     * 
     * @return the vector average of this matrix
     */
    public Vector getAverageVector(){
        return this.getSumVector().multiply(1f/this.getRows());
    }
      
    /**
     * Get the max of each column
     * 
     * @return the vector max of this matrix
     */
    public Vector getMaxVector(){
        Vector result = new Vector(this.getColumns());
        for (int j=0;j<this.getColumns();j++){
            double max=this.get(0,j);
            for (int i=1;i<getRows();i++)
                if (this.get(i,j)>max)
                    max=this.get(i,j);
            result.set(j,max);
        }
        return result;
    }
    
    /**
     * Get the min of each column
     * 
     * @return the vector min of this matrix
     */
    public Vector getMinVector(){
        Vector result = new Vector(this.getColumns());
        for (int j=0;j<this.getColumns();j++){
            double min=this.get(0,j);
            for (int i=1;i<getRows();i++)
                if (this.get(i,j)<min)
                    min=this.get(i,j);
            result.set(j,min);
        }
        return result;
    }
    
    /**
     * Assertion whether this matrix and another matrix have the same size
     */
    public void assertSizeAlignment(Matrix that){
        assert  this.getSize().equals( that.getSize()) : String.format("Incompatible Matrices %s / %s",this.getSize(),that.getSize());
    }
    
    /**
     * Compare the two matrices
     * 
     * @return boolean result of the comparison
     */
    public boolean equals(Matrix that){
        // TODO: make the matrix class implement comparable
        assertSizeAlignment(that);
        for (int i=0;i<getRows();i++)
            for (int j=0;j<getColumns();j++)
                if (this.get(i,j)!=that.get(i, j))
                    return false;
        return true;
    }
    
    /**
     * Get the size of the matrix
     * 
     * @return a vector that holds rows and columns
     */
    public Vector getSize(){
        return new Vector((double) this.getRows(), (double) this.getColumns());
    }
    
    /**
     * Assertion whether the arguments are within the matrice's bounds
     * 
     * @param i the row index
     * @param j the column index
     */
    public void assertIndexBound(int i,int j){
        assert i<getRows() && i>=0: String.format("Trying to access row %3d (/%3d)",i,getRows());
        assert j<getColumns() && j>=0 : String.format("Trying to access column %3d ( /%3d )",i,getColumns());        
    }
    
    /**
     * Perform matrix multiplication
     * 
     * @param that the other matrix
     * @return the result of the multiplication
     */
    public Matrix getMult(Matrix that){
        assert this.getColumns()==that.getRows() : String.format("Incompatible matrices to multiply ( %s / %s )",this.getSize(),that.getSize());
        Matrix result = new Matrix(this.getRows(),that.getColumns());
        for (int i=0;i<result.getRows();i++)
            for (int j=0;j<result.getColumns();j++){
                double sum=0;
                for (int k=0;k<this.getColumns();k++)
                    sum+=this.get(i,k)*that.get(k,j);
                result.set(i,j,sum);
            }
        return result;
    }
    
    /**
     * Transpose this matrix
     * 
     * @return the transposed matrix
     */
    public Matrix getTransposed(){
        Matrix result = new Matrix(this.getColumns(),this.getRows());
        for (int i=0;i<result.getRows();i++)
            for (int j=0;j<result.getColumns();j++)
                result.set(i, j, this.get(j,i));
        return result;   
    }
    
    /**
     * Get the Adjunct matrix.
     * Adjunct matrix is a matrix with the i-th row and j-th column removed
     * 
     * @param x the row to remove
     * @param y the column to remove
     * @return the Adjunct(x,y) matrix
     */
    public Matrix getAdjunct(int x,int y){
        assert this.getColumns()>1 && this.getRows()>1 : String.format("Cannot reduce matrix %s any more",this.getSize());
        
        Matrix result = new Matrix(this.getRows()-1,this.getColumns()-1);
        int o=0;
        for (int i=0;i<result.getRows();i++){
            if (o==x)
                o++;
            int k=0;
            for (int j=0;j<result.getColumns();j++){
                if (k==y)
                    k++;
                result.set(i, j, this.get(o,k));
                k++;
            }
            o++;
        }
        return result;   
    }
    
    /**
     * Returns the determinant of the matrix.
     * 
     * @return the real valued determinant of the matrix
     */
    public double det(){
        assert this.getColumns()==this.getRows() : String.format("Undefined det for non-square matrix (%s)",this.getSize());
        
        double result=0;
        if (this.getColumns()==1)
            return this.get(0,0);
        
        double sign=1;
        for (int i=0;i<this.getRows();i++){
            result+= sign*this.get(i,0)*this.getAdjunct(i,0).det();
            sign*=-1;
        }
        return result;
    }
    
    /**
     * Create a copy of this matrix.
     * 
     * @return a new matrix that is a copy of this one
     */
    public Matrix copy(){
        Matrix result = new Matrix(this.getRows(),this.getColumns());
        for (int i=0;i<result.getRows();i++)
            for (int j=0;j<result.getColumns();j++)
                result.set(i,j,this.get(i,j));
        return result;
    }
    
    /**
     * Get the covarianve matrix of this one.
     * 
     * @return The covariance matrix
     */
    public Matrix getCovariance(){
        Matrix result;
        Matrix data = this.copy();
        Vector neg_average = data.getAverageVector().multiply(-1);
        data.sum(neg_average);
        result = data.getTransposed().getMult(data).multiply(1f/getRows());
        return result;
    }
    
    /**
     * Get an x by y matrix with zeros.
     * 
     * @param x the rows of the matrix
     * @param y the columns of the matrix
     * @return A zero-valued matrix
     */
    public static Matrix zeros(int x,int y){
        return new Matrix(x,y);
    }
    
    /**
     * Get an x by y matrix with ones.
     * 
     * @param x the rows of the matrix
     * @param y the columns of the matrix
     * @return A one-valued matrix
     */
    public static Matrix ones(int x,int y){
        return zeros(x,y).add(1);
    }
    
    /**
     * Get a diagonal matrix with the specified values.
     * 
     * @param vals the values in the diagonal of the matrix
     * @return the diagonal matrix
     */
    public static Matrix diag(double...vals){
        Matrix result = new Matrix(vals.length,vals.length);
        for (int i=0;i<vals.length;i++)
            result.set(i,i,vals[i]);
        return result;
    }
    
    /**
     * Get a diagonal matrix with the specified values.
     * 
     * @param vals the values in the diagonal of the matrix
     * @return the diagonal matrix
     */
    public static Matrix diag(Vector vals){
        return diag(vals.toArray());
    }
    
    /**
     * Get a vector of the values in the diagonal of the matrix
     * 
     * @return The diagonal of the matrix
     */
    public Vector diag(){
        int x = this.getRows();
        if (x<this.getColumns())
            x=this.getColumns();
        Vector result = new Vector(x);
        for (int i=0;i<x;i++)
            result.set(i,this.get(i,i));
        return result;
    }
    
    /**
     * Get a unity matrix of size x by x
     * 
     * @param x the size of the matrix
     * @return The unity x by x matrix
     */
    public static Matrix eye(int x){
        return diag(new Vector(x).add(1f));
    }
    
    /**
     * Perform an elementary row operation on this matrix
     * 
     * @param lhs Left-hand-side of the operation. The row that will be changed
     * @param l_factor the factor by which lhs will be multiplied
     * @param rhs Right-hand-side of the operation. The row that will be added 
     * to lhs
     * @param r_factor the factor by which rhs will be multiplied     * 
     * @return This matrix
     */
    public Matrix _rowOperation(int lhs, double l_factor, int rhs, double r_factor){
        for (int i=0;i<this.getColumns();i++)
            this.set(lhs,i,this.get(lhs,i)*l_factor+this.get(rhs,i)*r_factor);
        if (_mirror!=null)
            _mirror._rowOperation(lhs, l_factor, rhs, r_factor);
        return this;
    }
    
    /**
     * Perform an elementary swap of two rows
     * 
     * @param a the first row
     * @param b the second row
     * @return This matrix
     */
    public Matrix _rowSwap(int a, int b){
        for (int i=0;i<this.getColumns();i++){
            double temp=this.get(a,i);
            this.set(a,i,this.get(b,i));
            this.set(b,i,temp);
        }
        if (_mirror!=null)
            _mirror._rowSwap(a, b);
        return this;
    }
    
    /**
     * Transform this matrix to an upper triangular using elementary row 
     * operations
     * 
     * @return This matrix after the operation
     */
    public Matrix _utrig(){
        for (int row=0; row<this.getRows();row++){
            int col=0;            
            while (this.get(row,col)==0){
                boolean found=false;
                for (int row2=row+1;row2<this.getRows();row2++){
                    if (this.get(row2,col)!=0){
                        this._rowSwap(row, row2);
                        found=true;
                        break;
                    }
                }
                if(found)
                    break;
                col++;
                if (col>=this.getColumns())
                    return this;
            }
            this._rowOperation(row, 1f/this.get(row,col), row, 0f);
            for (int i=row+1;i<this.getRows();i++)
                this._rowOperation(i, 1f, row, -this.get(i,col));
        }
        return this;
    }

    /**
     * Transform this matrix to a lower triangular using elementary row 
     * operations
     * 
     * @return This matrix after the operation
     */
    public Matrix _ltrig(){
        for (int row=this.getRows()-1; row>=0;row--){
            int col=this.getRows()-1;
            while (this.get(row,col)==0){
                boolean found=false;
                for (int row2=row-1;row2>=0;row2--){
                    if (this.get(row2,col)!=0){
                        this._rowSwap(row, row2);
                        found=true;
                        break;
                    }
                }
                if(found)
                    break;
                col--;
                if (col>=this.getColumns())
                    return this;
            }
            this._rowOperation(row, 1f/this.get(row,col), row, 0f);
            for (int i=row-1;i>=0;i--)
                this._rowOperation(i, 1, row, -this.get(i,col));
        }
        return this;
    }
    
    /**
     * Return the Inverse Matrix using Gauss-Jordan method
     * Only works for square matrices
     * @return a new Matrix such that A*B=I
     */
    public Matrix getInverse(){
        assert this.getColumns()==this.getRows() : String.format("For now I can not calculate pseudo-inverted matrix",this.getSize());
        double det = this.det();
        assert det!=0 : String.format("The matrix is not invertible",this.getSize());
        
        Matrix result = Matrix.eye(this.getRows());
        Matrix temp = this.copy();
        temp.setMirror(result);
            temp._utrig()._ltrig();
        temp.setMirror(null);
        return result;
    }
    
    /**
     * If this is a row-matrix of a column-matrix then turn it into a Vector
     * 
     * @return A new vector-representation of this matrix
     */
    public Vector getVector(){
        //returns by value matrix as a vector
        assert this.getColumns()==1 || this.getRows()==1 : String.format("Cannot vectorize (%s) matrix ",this.getSize());
        Vector result;
        if (this.getColumns()==1){
            result = new Vector(this.getRows());
            for (int i=0;i<result.getLength();i++)
                result.set(i,this.get(i,0));
        }else{
            result = new Vector(this.getColumns());
            for (int j=0;j<result.getLength();j++)
               result.set(j,this.get(0,j));
        }
        return result;
    }
    
    /**
     * Save this matrix into the filesystem
     * 
     * @param filename the filename to which the file will be saved
     */
    public void save(String filename){
        try {
         FileOutputStream fileOut = new FileOutputStream(filename);
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(this);
         out.close();
         fileOut.close();
      } catch (IOException i) {
         i.printStackTrace();
      }
    }
    
    
    /**
     * Load a matrix from the filesystem
     * 
     * @param filename the filename from which the matrix will be read
     */
    public static Matrix load(String filename){
        Matrix result;
        try {
         FileInputStream fileIn = new FileInputStream(filename);
         ObjectInputStream in = new ObjectInputStream(fileIn);
         result = (Matrix) in.readObject();
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
     * Temporary: Perform some benchmarkings for the matrix configuration
     * 
     * @param sz the size of the matrices involved
     */
    public static void benchmark(int sz){
        Matrix a = new Matrix(sz,sz);
        Matrix b = new Matrix(sz,sz);
        Random rnd = new Random();
        for (int i=0;i<sz;i++)
            for (int j=0;j<sz;j++){
                a.set(i, j, rnd.nextDouble());
                b.set(i, j, rnd.nextDouble());
            }

        System.out.println("GO!");
        Matrix c = a.getMult(b);
    }
    
    
    /**
     * The main executable function.
     * Demonstrates the matrix functionality
     * 
     * @param args Commmand line arguments
     */
    public static void main(String[] args){
        double[][] data = {{4,0,0,0},{0,0,2,0},{0,1,2,0},{1,0,0,1}};
        Matrix x = new Matrix(data);
        x.show();
        x.getInverse().show();
        
    }
}
