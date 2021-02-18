/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math.Operatables;

import Core.Logger;
import Math.Matrix;
import Math.Polynomial;
import Math.Vector;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author kostis
 */
public class GenericMatrix < T extends OperatableAdapter<T> > 
    extends OperatableAdapter < GenericMatrix<T> > 
    implements java.io.Serializable {
    
    private OperatableAdapter<T> _data[][];
    private transient GenericMatrix<T> _augmented;
    
    /**
     * Constructor for the GenericMatrix class
     * Create a new GenericMatrix defining the number of rows and columns
     * 
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     */
    public GenericMatrix(int rows, int columns){
        _data=new OperatableAdapter[rows][columns];
    }

    /**
     * Get the number of columns of this matrix
     * 
     * @returns an integer representing the number of columns in the matrix
     */
    public final int getColumnCount(){
        return _data[0].length;
    }

    /**
     * Get the number of rows of this matrix
     * 
     * @returns an integer representing the number of rows in the matrix
     */
    public final int getRowCount(){
        return _data.length;
    }

    /**
     * Return the Element of the Matrix at the specified index
     * 
     * @param i row index
     * @param j column index
     * @return the value of the matrix at row i, column j
     */
    public T valueAt(int i, int j){
        assertIndexBound(i,j);
        return (T) _data[i][j];
    }
    
    /**
     * Sets the Element of the Matrix at the specified index.
     * 
     * @param i row index
     * @param j column index
     * @param v the value
     * @return this
     */
    public GenericMatrix<T> set(int i, int j, T x){
        assertIndexBound(i,j);
        _data[i][j]=x;
        return this;
    }
    
    /**
     * Create a copy of this matrix.
     * @return a new matrix that is a copy of this one
     */
    public GenericMatrix<T> copy(){
        GenericMatrix<T> result = new GenericMatrix<T> (this.getRowCount(),this.getColumnCount());
        for (int i=0;i<result.getRowCount();i++)
            for (int j=0;j<result.getColumnCount();j++)
                result.set(i,j,this.valueAt(i,j).copy());
        return result;
    }
    
    /**
     * Get the augmented matrix.
     * Highly specialized function, this one returns the matrix which will
     * undergo the same row operations as this one whenever rowOperation 
     * is invoked
     * TODO: See if I can get rid of this shit
     * 
     * @returns the augmented matrix
     */
    public GenericMatrix<T> getAugmented(){
        return _augmented;
    }
    
    /**
     * Sets a matrix to be augmented by this one.
     * Highly specialized function, this one sets the matrix which will
     * undergo the same row operations as this one whenever rowOperation
     * is called
     * 
     * @param that the augmented matrix
     * @return this
     */
    public GenericMatrix<T> setAugmented(GenericMatrix<T> that){
        _augmented = that;
        return this;
    }
    
    public GenericMatrix<T> add(GenericMatrix<T> that){
        assertSizeAlignment(that);
        for (int i=0;i<this.getRowCount();i++)
            for (int j=0;j<this.getColumnCount();j++)
                this.valueAt(i,j).add(that.valueAt(i, j));
        return this;
    } 
    
    public GenericMatrix<T> diff(GenericMatrix<T> that){
        assertSizeAlignment(that);
        for (int i=0;i<this.getRowCount();i++)
            for (int j=0;j<this.getColumnCount();j++)
                this.valueAt(i,j).add(that.valueAt(i, j).getMultiply(-1));
        return this;
    } 
    
    

    public GenericMatrix<T> multiply(Real that){
        for (int i=0;i<this.getRowCount();i++)
            for (int j=0;j<this.getColumnCount();j++)
                this.valueAt(i,j).multiply(that);
                //this.set(i,j,this.get(i,j).add(that.get(i, j)));
        return this;
    } 

    public GenericMatrix<T> getProduct(GenericMatrix<T> that){
        assert this.getColumnCount()==that.getRowCount() : String.format("Incompatible matrices to multiply ( [%d %d] / [%d %d] )",this.getRowCount(),this.getColumnCount(),that.getRowCount(),that.getColumnCount());
        GenericMatrix<T> result = new GenericMatrix<T> (this.getRowCount(),that.getColumnCount());
        for (int i=0;i<result.getRowCount();i++)
            for (int j=0;j<result.getColumnCount();j++){
                // TODO: this is hideous. Have the Operatable interface have a getZero()
                T sum = (T) this.valueAt(0,0).getMultiply(0.0);
                for (int k=0;k<this.getColumnCount();k++)
                    sum.add(this.valueAt(i,k).getProduct(that.valueAt(k,j)
                        )
                    );
                result.set(i,j,sum);
            }
        return result;
    } 
    
    /**
     * Get the size of the GenericMatrix
     * 
     * @return a vector that holds rows and columns
     */
    public Vector getSize(){
        return new Vector((double) this.getRowCount(), (double) this.getColumnCount());
    }
    
    
    /**
     * Assertion whether the arguments are within the matrice's bounds
     * 
     * @param i the row index
     * @param j the column index
     */
    public void assertIndexBound(int i,int j){
        assert i<getRowCount() && i>=0: String.format("Trying to access row %3d (/%3d)",i,getRowCount());
        assert j<getColumnCount() && j>=0 : String.format("Trying to access column %3d ( /%3d )",j,getColumnCount());        
    }
    
    
    /**
     * Assertion whether this matrix and another matrix have the same size
     * @param that the matrix to be compared to
     */
    public void assertSizeAlignment(GenericMatrix<T> that){
        assert  this.getRowCount()==that.getRowCount() && this.getColumnCount() == that.getColumnCount(): String.format("Incompatible Matrices %s / %s",this.getSize(),that.getSize());
    }
    

    /**
     * Assertion whether this matrix and another matrix have the same size
     */
    public void assertSquare(){
        assert this.getColumnCount()==this.getRowCount() : String.format("This matrix is not square, %s",this.getSize());
    }
    
    /**
     * Transpose this matrix
     * 
     * @return the transposed matrix
     */
    public GenericMatrix<T> getTransposed(){
        GenericMatrix<T> result = new GenericMatrix<T>(this.getColumnCount(),this.getRowCount());
        for (int i=0;i<result.getRowCount();i++)
            for (int j=0;j<result.getColumnCount();j++)
                result.set(i, j, this.valueAt(j,i));
        return result;   
    }
    
    /**
     * Compare the two matrices
     * 
     * @return boolean result of the comparison
     */
    public boolean equals(GenericMatrix<T> that){
        // TODO: make the matrix class implement comparable
        assertSizeAlignment(that);
        for (int i=0;i<getRowCount();i++)
            for (int j=0;j<getColumnCount();j++)
                if (this.valueAt(i,j).equals(that.valueAt(i, j)))
                    return false;
        return true;
    }
    
    public int hashCode(){
        int prime=31;
        int result=1;
        for (int i=0;i<getRowCount();i++)
            for (int j=0;j<getColumnCount();j++){
                result*=prime;
                result+=(_data[i][j]==null)?(0):(_data.hashCode());
            }
        return result;
        //return Objects.hash(_data);
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
    public GenericMatrix<T> _rowOperation(int lhs, T l_factor, int rhs, T r_factor){
        for (int i=0;i<this.getColumnCount();i++){
            T res = this.valueAt(lhs,i).getProduct(l_factor)
                .add(this.valueAt(rhs,i).getProduct(r_factor) );
            /*
            Logger.log("Doing the operation %s = %s * %s + %s * %s",res,
                this.get(lhs,i),l_factor,
                this.get(rhs,i),r_factor
            );
            */
            this.set(lhs,i, res );
        }
        if (getAugmented()!=null)
            getAugmented()._rowOperation(lhs,l_factor,rhs,r_factor);
        
        return this;
    }

    /**
     * Perform an elementary swap of two rows
     * 
     * @param a the first row
     * @param b the second row
     * @return This matrix
     */
    public GenericMatrix<T> _rowSwap(int a, int b){
        for (int i=0;i<this.getColumnCount();i++){
            T temp=this.valueAt(a,i);
            this.set(a,i,this.valueAt(b,i));
            this.set(b,i,temp);
        }
        if (getAugmented()!=null)
            getAugmented()._rowSwap(a, b);
        return this;
    }

    /**
     * Transform this matrix to an upper triangular using elementary row 
     * operations
     * 
     * @return This matrix after the operation
     */
    public GenericMatrix<T> _utrig(){
        // For all the rows in the Matrix
        for (int row=0; row<this.getRowCount();row++){
            int col=0;
            //
            while (this.valueAt(row,col).isZero()){
                boolean found=false;
                //
                for (int row2=row+1;row2<this.getRowCount();row2++){
                    if (!this.valueAt(row2,col).isZero()){
                        this._rowSwap(row, row2);
                        found=true;
                        break;
                    }
                }
                if(found)
                    break;
                col++;
                if (col>=this.getColumnCount())
                    return this;
            }
            for (int i=row+1;i<this.getRowCount();i++)
                this._rowOperation(i, this.valueAt(row,col), 
                    row, (T) this.valueAt(i,col).getMultiply(-1.0)
                );
        }
        return this;
    }
    

    /**
     * Transform this matrix to a lower triangular using elementary row 
     * operations
     * 
     * @return This matrix after the operation
     */
    public GenericMatrix<T> _ltrig(){
        for (int row=this.getRowCount()-1; row>=0;row--){
            int col=this.getRowCount()-1;
            while (this.valueAt(row,col).isZero()){
                boolean found=false;
                for (int row2=row-1;row2>=0;row2--){
                    if (!this.valueAt(row2,col).isZero()){
                        this._rowSwap(row, row2);
                        found=true;
                        break;
                    }
                }
                if(found)
                    break;
                col--;
                if (col>=this.getColumnCount())
                    return this;
            }
            //this._rowOperation(row, 1f/this.get(row,col), row, 0f);
            for (int i=row-1;i>=0;i--)
                this._rowOperation(i, this.valueAt(row,col), 
                    row, (T) this.valueAt(i,col).getMultiply(-1.0)
                );
        }
        return this;
    }
    
    /**
     * Get the Adjunct matrix.
     * Adjunct matrix is a matrix with the i-th row and j-th column removed.
     * Note that the elements are references to the original matrix elements
     * 
     * @param x the row to remove
     * @param y the column to remove
     * @return the Adjunct(x,y) matrix
     */
    public GenericMatrix<T> adjunct(int x,int y){
        assert this.getColumnCount()>1 && this.getRowCount()>1 : String.format("Cannot reduce matrix (%d %d) any more",getRowCount(),getColumnCount());
        
        GenericMatrix<T> result = new GenericMatrix<T>(this.getRowCount()-1,this.getColumnCount()-1);
        int o=0;
        for (int i=0;i<result.getRowCount();i++){
            if (o==x)
                o++;
            int k=0;
            for (int j=0;j<result.getColumnCount();j++){
                if (k==y)
                    k++;
                result.set(i, j, this.valueAt(o,k));
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
    public T det(){
        assert this.getColumnCount()==this.getRowCount() : String.format("Undefined det for non-square matrix (%d %d)",getRowCount(),getColumnCount());
        
        if (this.getColumnCount()==1)
            return this.valueAt(0,0).copy();
        
        T result = (T) this.valueAt(0, 0).getMultiply(0.0);
        double sign=1;
        for (int i=0;i<this.getRowCount();i++){
            result.add(this.adjunct(i,0)
                .det()
                .multiply(sign)
                .getProduct(this.valueAt(i,0)));
            sign*=-1;
        }
        return result;
    }
    
    public boolean isUnit(){
        for (int i=0;i<this.getRowCount();i++)
            for (int j=0;j<this.getColumnCount();j++)
                if (i==j && !this.valueAt(i,j).isUnit())
                    return false;
                else if (i!=j && !this.valueAt(i,j).isZero())
                    return false;
        return true;        
    }
    
    public boolean isZero(){
        for (int i=0;i<this.getRowCount();i++)
            for (int j=0;j<this.getColumnCount();j++)
                if (!this.valueAt(i,j).isZero())
                    return false;
        return true;
    }
    
    
    /**
     * Present the data of the matrix in the console
     * 
     */
    public void show(String name){
        Logger.log("\"%s\":\t[%d %d]",name,getRowCount(),getColumnCount());
        String str;
        for (int i=0;i<getRowCount();i++){
            str="";
            for (int j=0;j<getColumnCount();j++)
                str+=String.format("%s ",valueAt(i,j)!=null?valueAt(i,j).toString():"Null");
            Logger.log(str);
        }
    }

    /**
     * Present the data of the matrix in the console
     * 
     */
    public void show(){
        this.show("");
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
    public static GenericMatrix load(String filename){
        GenericMatrix result;
        try {
         FileInputStream fileIn = new FileInputStream(filename);
         ObjectInputStream in = new ObjectInputStream(fileIn);
         result = (GenericMatrix) in.readObject();
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
    
    //everything below and everything to the left of the element should be zero
    public boolean isPivot(int row, int col){
        assertIndexBound(row, col);
        
        // Elements below
        for (int i=row+1;i<this.getRowCount();i++)
            if (!valueAt(i,col).isZero())
                return false;
        
        // Elements to the left
        for(int j=col-1;j>=0;j--)
            if (!valueAt(row,j).isZero())
                return false;
        
        return !valueAt(row,col).isZero();
    }
    
    public static void main(String...args){
        boolean tests[]={false,true};
        if (tests[0]){
            GenericMatrix<Real> x = new GenericMatrix<Real>(3,3);
            double[][] data = {{6,1,1},{4,-2,5},{2,8,7}};
            //double[][] data = {{1,0,0},{2,0,0},{1,1,1}};
            //double[][] data = {{1,0,0},{0,1,0},{0,0,1}};
            for (int i=0;i<x.getRowCount();i++)
                for (int j=0;j<x.getColumnCount();j++)
                    x.set(i,j,new Real(data[i][j]));

            x.show();

            //x._ltrig();
            //x._rowOperation(1, new Real(3.0), 0, new Real(5.0));
            //x.show();
            x.det().show();

            x.show();
        }else if (tests[1]){
            double[][] data = {{7,0,3},{-3,2,-3},{-3,0,-1}};
            GenericMatrix<Polynomial> x = new GenericMatrix<Polynomial>(data.length,data[0].length);
            GenericMatrix<Polynomial> l = new GenericMatrix<Polynomial>(data.length,data[0].length);
            for (int i=0;i<x.getRowCount();i++)
                for (int j=0;j<x.getColumnCount();j++){
                    x.set(i,j,new Polynomial(data[i][j]));
                    if (i==j)
                        l.set(i, j, new Polynomial(0.0,1.0));
                    else
                        l.set(i, j, Polynomial.zero());
                }
            
            x.add((GenericMatrix<Polynomial>)l.multiply(-1.0)).det().getRoots().show();
        }
    }
    
    /** 
     * Multiply matrices element by element
     * 
     * @param that the other matrix
     * @return 
     */
    public GenericMatrix multiplyElements(GenericMatrix<T> that){
        assertSizeAlignment(that);
        for (int i=0;i<this.getRowCount();i++)
            for (int j=0;j<this.getColumnCount();j++)
                this.set(i, j, 
                    this.valueAt(i,j).getProduct(that.valueAt(i, j)
                    )
                );
        return this;
    }
    
    /** 
     * Add matrices element by element
     * 
     * @param that the other matrix
     * @return 
     */
    public GenericMatrix addElements(GenericMatrix<T> that){
        assertSizeAlignment(that);
        for (int i=0;i<this.getRowCount();i++)
            for (int j=0;j<this.getColumnCount();j++)
                this.valueAt(i, j).add(that.valueAt(i, j)
                );
        return this;
    }
    
    public GenericMatrix getMergeLR(GenericMatrix<T> that){
        assert this.getRowCount()==that.getRowCount():String.format("Matriced should have "
                + "the same row count to mergeLR [%s]\t[%s]",this.getSize(),that.getSize());
        GenericMatrix result = new Matrix(this.getRowCount(),this.getColumnCount()+that.getColumnCount());
        for (int i=0;i<result.getRowCount();i++){
            for (int j=0;j<this.getColumnCount();j++)
                result.set(i, j, this.valueAt(i,j));
            for (int j=0;j<that.getColumnCount();j++)
                result.set(i,this.getColumnCount()+j, that.valueAt(i,j));
        }
        return result;
    }
            
    // diag
    // is trig,diag whatevs
    
}

