package Math;

import Core.Logger;
import Math.Operatables.GenericMatrix;
import Math.Operatables.Real;
import java.util.ArrayList;
import java.util.Random;

public class Matrix extends GenericMatrix<Real> {
    
    /**
     * Constructor for the Matrix class
     * Create a new Matrix defining the number of rows and columns
     * 
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     */
    public Matrix(int rows,int columns){
        super(rows,columns);
        for(int i=0;i<getRowCount();i++)
            for(int j=0;j<getColumnCount();j++)
                set(i,j,new Real(0.0));                
    }
    
    /**
     * Constructor for the Matrix class
     * Create a new matrix defining the data array. Note that it creates a new
     * array for that purpose
     * 
     * @param data the 2D array that contains the data of the new matrix
     */
    public Matrix(double[][] data){
        super(data.length,data[0].length);
        for(int i=1;i<data.length;i++)
            assert data[i-1].length==data[i].length : String.format("Can't create matrix out of array. Inconsistent dimensions");
        for(int i=0;i<data.length;i++){
            for(int j=0;j<data[0].length;j++){
                this.set(i,j,new Real(data[i][j]));
            }
        }
    }
    
    /**
     * Set the value of the specified element. This function takes a double and
     * sets it as Real
     * 
     * @param i the row of the element to set
     * @param j the column of the element to set
     * @param v the value of the element to set
     * @return this
     */
    public Matrix set(int i, int j, double v){
        return (Matrix) this.set(i, j, new Real(v));
    }
    
    /**
     * Set the values of the specified row according to the given Vector
     * 
     * @param row the row to be changed
     * @param x the Vector that contains the values
     * @return this
     */
    public Matrix setRow(int row, Vector x){
        assert this.getColumnCount()==x.getLength() : 
            String.format("The vector should have %d columns, not %d",
            this.getColumnCount(), x.getLength());
        for(int col=0; col<this.getColumnCount();col++)
            this.set(row, col, x.get(col).getPrimitive());
        return this;
    }
    
    /**
     * Set the values of the specified Column according to the given Vector
     * 
     * @param col the col to be changed
     * @param x the Vector that contains the values
     * @return this
     */
    public Matrix setColumn(int col, Vector x){
        assert this.getRowCount()==x.getLength() : 
            String.format("The vector should have %d elements, not %d",
            this.getColumnCount(), x.getLength());
        for(int row=0; row<this.getRowCount();row++)
            this.set(row, col, x.get(row).getPrimitive());
        return this;
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
    public Matrix weightedSum(Real this_v, Matrix that, Real that_v){
        assertSizeAlignment(that);
        return (Matrix) this.multiply(this_v).add(that.getMultiply(that_v));
    }
    
    
    /**
     * Add a vector to each row.
     * this function helps with normalization, i.e. subtract by the mean of a 
     * signal
     * 
     * @param values the vector to add
     * @return this
     */
    public Matrix add(Vector values){
        assert values.getLength()==this.getColumnCount() : String.format("Incompatible sizes %s / %s",this.getColumnCount(),values.getLength());
        for (int i=0;i<getRowCount();i++)
            for (int j=0;j<getColumnCount();j++)
                this.valueAt(i, j).add(values.get(j));
        return this;
    }
    
    /**
     * Add a value to each element.
     * 
     * @param value the value to add
     * @return this
     */
    public Matrix add(double value){
        return this.add(new Real(value));
    }

    /**
     * Add a value to each element.
     * 
     * @param value the value to add
     * @return this
     */
    public Matrix add(Real value){
        for (int i=0;i<getRowCount();i++)
            for (int j=0;j<getColumnCount();j++)
                this.valueAt(i,j).add(value);
        return this;
    }
    
    /**
     * Sum all columns into a vector
     * 
     * @return the vector sum of this matrix
     */
    public Vector getSumVector(){
        Vector result = new Vector(this.getColumnCount());
        for (int j=0;j<this.getColumnCount();j++){
            Real sum=Real.zero();
            for (int i=0;i<getRowCount();i++)
                sum.add(this.valueAt(i,j));
            result.set(j,sum);
        }
        return result;
    }
    
    /**
     * Multiply all columns into a vector
     * 
     * @return the vector product of this matrix
     */
    public Vector getRowProduct(){
        Vector result = new Vector(this.getColumnCount());
        for (int j=0;j<this.getColumnCount();j++){
            Real sum= Real.unit();
            for (int i=0;i<getRowCount();i++)
                sum.multiply(this.valueAt(i,j));
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
        return (Vector) this.getSumVector().multiply(1.0/this.getRowCount());
    }
    
    /**
     * Get the Standard Deviation of each column
     * 
     * @return a row-vector of the STDs
     */
    public Vector getStdVector(){
        Vector sumOfSquaresAvg_vec = (Vector) this.getSumofSquaresVector().multiply(1.0/this.getRowCount());
        Vector squaredAvg_vec = this.getAverageVector().power(2);
        Vector result = (Vector) sumOfSquaresAvg_vec.copy().diff(squaredAvg_vec);
        return result.sqrt();
    }
   
    /**
     * Get the sum of squares of each column
     * 
     * @return a row-vector of the sum of squares
     */
    public Vector getSumofSquaresVector(){
        Vector result = new Vector(this.getColumnCount());
        for (int i=0;i<result.getLength();i++)
            result.set(i, this.getColumn(i).getSumofSquares());
        return result;
    }
    
    /**
     * Get the norm of each column
     * 
     * @return a row-vector of the norms
     */
    public Vector getNormVector(){
        return this.getSumofSquaresVector().sqrt();
    }
    
    /**
     * Get the index of the max value for each column
     * 
     * @return an integer array with the indices of the max value of each column
     */
    public Integer[] getArgMaxList(){
        Integer[] result = new Integer[this.getColumnCount()];
        Vector max_vector = this.getMaxVector();
        for (int j=0;j<this.getColumnCount();j++)
            for (int i=0;i<getRowCount();i++)
                if (this.valueAt(i,j).equals(max_vector.get(j))){
                    result[j]=i;
                    break;
                }
        return result;
    }
    
    /**
     * Get the index of the min value for each column
     * 
     * @return an integer array with the indices of the min value of each column
     */
    public Integer[] getArgMinList(){
        Integer[] result = new Integer[this.getColumnCount()];
        Vector min_vector = this.getMinVector();
        for (int j=0;j<this.getColumnCount();j++)
            for (int i=0;i<getRowCount();i++)
                if (this.valueAt(i,j).equals(min_vector.get(j))){
                    result[j]=i;
                    break;
                }
        return result;
    }
    
    /**
     * Get the max of each column
     * 
     * @return the vector max of this matrix
     */
    public Vector getMaxVector(){
        Vector result = new Vector(this.getColumnCount());
        for (int j=0;j<this.getColumnCount();j++){
            Real max=this.valueAt(0,j);
            for (int i=1;i<getRowCount();i++)
                if (this.valueAt(i,j).getPrimitive()>max.getPrimitive())
                    max=this.valueAt(i,j);
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
        Vector result = new Vector(this.getColumnCount());
        for (int j=0;j<this.getColumnCount();j++){
            Real min=this.valueAt(0,j);
            for (int i=1;i<getRowCount();i++)
                if (this.valueAt(i,j).getPrimitive()<min.getPrimitive())
                    min=this.valueAt(i,j);
            result.set(j,min);
        }
        return result;
    }
    
    /**
     * Get the covarianve matrix of this one.
     * 
     * @return The covariance matrix
     */
    public Matrix getCovariance(){
        Matrix result;
        Matrix data;
        data = this.copy();
        Vector neg_average = (Vector) data.getAverageVector().multiply(-1.0);
        data.add(neg_average);
        result = (Matrix) data.getTransposed()
            .getProduct(data)
            .multiply(new Real(getRowCount()).inv());
        return result;
    }
    
    /**
     * Override class for GenericMatrix.getProduct() so it returns a Matrix
     * @param that the other matrix
     * @return the result of the matrix multiplication
     */
    public Matrix getProduct(Matrix that){
        GenericMatrix temp = super.getProduct(that);
        Matrix result = new Matrix(temp.getRowCount(),temp.getColumnCount());
        for (int i=0;i<result.getRowCount();i++)
            for (int j=0;j<result.getColumnCount();j++)
                result.set(i, j, (Real)temp.valueAt(i, j));
        return result;
    }
    
    public Matrix getTransposed(){
        Matrix result = new Matrix(this.getColumnCount(),this.getRowCount());
        for (int i=0;i<result.getRowCount();i++)
            for (int j=0;j<result.getColumnCount();j++)
                result.set(i, j, this.valueAt(j,i));
        return result;   
    }
    
    /**
     * Perform PCA analysis on this dataset
     * 
     * @return An array of matrices. 
     * The first is the transformation matrix (eigenvectors as columns)
     * the second matrix zero with eigenvalues in the diagonal
     */
    public Matrix[] PCA(){
        // TODO: prime chance to cache 
        Vector average = this.getAverageVector();
        if (!average.isZero())
            Logger.log(Logger.LL_WARNING, "Trying to do PCA to a non-zero-average matrix");
        Matrix cov = this.getCovariance();
        ArrayList<Vector> eigenVectors = cov.getEigenVectors();
        Matrix result = new Matrix(cov.getRowCount(),cov.getColumnCount());
        for (int i=0;i<result.getRowCount();i++)
            for (int j=0;j<result.getColumnCount();j++)
                result.set(i, j, eigenVectors.get(i).get(j));
        Vector eigenValues = this.getEigenvalues();
        return new Matrix[]{result,Matrix.diag(eigenValues)};
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
        return zeros(x,y).add(Real.unit());
    }
    
    /**
     * Get a diagonal matrix with the specified values.
     * 
     * @param vals the values in the diagonal of the matrix
     * @return the diagonal matrix
     */
    public static Matrix diag(Real...vals){
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
        Matrix result = new Matrix(vals.getLength(),vals.getLength());
        for (int i=0;i<vals.getLength();i++)
            result.set(i, i, vals.get(i));
        return result;
    }
    
    /**
     * Get a vector of the values in the diagonal of the matrix
     * 
     * @return The diagonal of the matrix
     */
    public Vector diag(){
        int x = this.getRowCount();
        if (x<this.getColumnCount())
            x=this.getColumnCount();
        Vector result = new Vector(x);
        for (int i=0;i<x;i++)
            result.set(i,this.valueAt(i,i));
        return result;
    }
    
    /**
     * Get a unit matrix of size x by x
     * 
     * @param x the size of the matrix
     * @return The unity x by x matrix
     */
    public static Matrix eye(int x){
        return diag(new Vector(x).add(1f));
    }
    
    /**
     * Return the Inverse Matrix using Gauss-Jordan method
     * Only works for square matrices
     * @return a new Matrix such that A*B=I
     */
    public Matrix getInverse(){
        assert this.getColumnCount()==this.getRowCount() : String.format("For now I can not calculate pseudo-inverted matrix",this.getSize());
        Real det = this.det();
        assert !det.isZero() : String.format("The matrix is not invertible",this.getSize());
        
        Matrix result = Matrix.eye(this.getRowCount());
        Matrix temp = (Matrix) this.copy();
        
        temp.setAugmented(result);
            temp.gaussianElimination();
        temp.setAugmented(null);
        //temp.show("Husk");
        return result;
    }
    
    /**
     * If this is a row-matrix of a column-matrix then turn it into a Vector
     * 
     * @return A new vector-representation of this matrix
     */
    public Vector getVector(){
        //returns by value matrix as a vector
        assert this.getColumnCount()==1 || this.getRowCount()==1 : String.format("Cannot vectorize (%s) matrix ",this.getSize());
        Vector result;
        if (this.getColumnCount()==1){
            result = new Vector(this.getRowCount());
            for (int i=0;i<result.getLength();i++)
                result.set(i,this.valueAt(i,0));
        }else{
            result = new Vector(this.getColumnCount());
            for (int j=0;j<result.getLength();j++)
               result.set(j,this.valueAt(0,j));
        }
        return result;
    }
    
    /**
     * Create a copy of this matrix.
     * 
     * @return a new matrix that is a copy of this one
     */
    public Matrix copy(){
        Matrix result = new Matrix (this.getRowCount(),this.getColumnCount());
        for (int i=0;i<result.getRowCount();i++)
            for (int j=0;j<result.getColumnCount();j++)
                result.set(i,j,this.valueAt(i,j).copy());
        return result;
    }
    
    /**
     * Raise all elements to the power
     * 
     * @param exponent the exponent to be raised to
     * @return this
     */
    public Matrix power(int exponent){
        for (int i=0;i<this.getRowCount();i++)
            for (int j=0;j<this.getColumnCount();j++)
                this.valueAt(i, j).power(exponent);
        return this;
    }
    
    /**
     * Exponentiate all elements
     * @return this
     */
    public Matrix exp(){
        for (int i=0;i<this.getRowCount();i++)
            for (int j=0;j<this.getColumnCount();j++)
                this.set(i, j, this.valueAt(i, j).getExp());
        return this;
    }

    /**
     * Log all elements
     * @return this
     */
    public Matrix log(){
        for (int i=0;i<this.getRowCount();i++)
            for (int j=0;j<this.getColumnCount();j++){
                Real value = this.valueAt(i, j).getLn();
                this.set(i, j, value);
            }
        return this;
    }    

    /**
     * Log all elements
     * @return this
     */
    public Matrix round(){
        for (int i=0;i<this.getRowCount();i++)
            for (int j=0;j<this.getColumnCount();j++){
                this.set(i, j, this.valueAt(i, j).getRound());
            }
        return this;
    }    

    /**
     * Replace all elements with their inverse value
     * 
     * @return this matrix
     */
    public Matrix invertElements(){
        for (int i=0;i<getRowCount();i++)
            for (int j=0;j<getColumnCount();j++)
                this.valueAt(i, j).inv();
        return this;
    }

    /**
     * Replace all elements with their negative value
     * 
     * @return this matrix
     */
    public Matrix negateElements(){
        for (int i=0;i<getRowCount();i++)
            for (int j=0;j<getColumnCount();j++)
                this.valueAt(i, j).negate();
        return this;
    }

    /**
     * Get a column of the matrix as a Vector
     * Creates a new Vector with the values of the specified column of the matrix
     * 
     * @param col the index of the desired column
     * @return the column of the matrix as Vector
     */    
    public Vector getColumn(int col){
        this.assertIndexBound(0, col);
        Vector result = new Vector(this.getRowCount());
        //System.out.println(this.getRows());
        for(int row=0;row<this.getRowCount();row++)
            result.set(row, this.valueAt(row, col).getPrimitive());
        return result;
    }
    
    /**
     * Get a row of the matrix as a Vector
     * Creates a new Vector with the values of the specified row of the matrix
     * 
     * @param row the index of the desired row
     * @return the row of the matrix as Vector
     */    
    public Vector getRow(int row){
        this.assertIndexBound(row , 0);
        Vector result = new Vector(this.getColumnCount());
        for(int col=0;col<this.getColumnCount();col++)
            result.set(col, this.valueAt(row, col).getPrimitive());
        return result;
    }
    
    /**
     * Calculates the solution of the linear system
     * Returns a list of Vectors that represent the solutions of the system
     * 
     * @param b the constant terms
     * @return A list of Vectors. The first represents the constant terms
     * of the solutions and the rest are the basis Vector of the solution space
     */
    public ArrayList<Vector> solveLinearSystem(Vector b){
        ArrayList<Vector> result = new ArrayList<Vector>();
        result.add(new Vector(this.getRowCount()));
        Matrix echelon = this.copy();
        boolean homogenous = b==null || b.isZero();
        Vector y;
        if (b==null)
            y = new Vector(this.getRowCount());
        else
            y = b.copy();
        echelon.setAugmented(y);
            echelon.gaussianElimination();
        echelon.setAugmented(null);
        
        for (int col=0;col<echelon.getColumnCount();col++){
            boolean isPivot=false;
            boolean pivotColumn=false;
            // This could go up to echelon.getRows() but since this is an 
            // echelon Matrix, the rest of the elements are 0
            for (int row=0;row<=col;row++){
                isPivot=echelon.isPivot(row, col);
                if (isPivot){
                    pivotColumn=true;
                    if (y!=null)
                        result.get(0).set(col, y.get(row).copy());
                }
            }
            if (!pivotColumn){
                Vector temp = echelon.getColumn(col);
                temp.set(col, Real.unit().getNegative());
                result.add(temp);
            }
        }
        return result;
    }
    
    
    /**
     * Calculate the eigenvectors of this matrix
     * @return a list of Vectors that represent the eigenvectors of the matrix
     */
    public ArrayList<Vector> getEigenVectors(){
        assertSquare();
        Vector eigenValues=this.getEigenvalues();
        ArrayList <Vector> eigenVectors = new ArrayList();
        for (int i=0;i<eigenValues.getLength();i++){
            
            Real eigenValue = eigenValues.get(i);
            
            // Figure out the algrbraic multiplicity of this eigenvalue
            // also get the first occurence of this lambda
            int algebraicMultiplicity=0;
            int firstOccurence=-1;
            for (int j=0;j<eigenValues.getLength();j++){
                if (eigenValue.equals(eigenValues.get(j))){
                    algebraicMultiplicity++;
                    if (firstOccurence==-1)
                        firstOccurence=j;
                }
            }
            // If this is not the first occurence of the eigenvalue then it has already been accounted for
            if (firstOccurence!=i)
                continue;
            
            Matrix temp = this.copy();
            
            // Reduce each diagonal element by the eigenvalue
            for (int j=0;j<temp.getColumnCount();j++)
                temp.valueAt(j, j).add(eigenValue.getNegative());
            
            ArrayList<Vector> solutions = temp.solveLinearSystem(null);
            int geometricMultiplicity = solutions.size()-1;
            //assert solutions.size()>=2 : String.format("something went wrong trying to find the eigenvectors. Solution size:%d", solutions.size());
            if (geometricMultiplicity != algebraicMultiplicity)
                Logger.log(Logger.LL_WARNING,"geometricMultiplicity != algebraicMultiplicity (%d / %d)",geometricMultiplicity, algebraicMultiplicity);
            for (int j=1;j<solutions.size();j++)
                eigenVectors.add(solutions.get(j));
        }
        return eigenVectors;
    }
    
    /**
     * Turn this Matrix to reduced-row-echelon form
     * using Gaussian elimination
     * 
     * @return a reference to this matrix
     */
    public Matrix gaussianElimination(){
        // For all the rows in the Matrix
        for (int core_row=0; core_row<this.getRowCount();core_row++){
            int col=core_row;
            // Find the proper column of the pivot element
            for (;col<this.getColumnCount();col++){
                
                // in case the element is zero ...
                if (this.valueAt(core_row, col).isZero()){
                    // ... find a non-zero in the same column ...
                    for (int row=col+1; row<this.getRowCount(); row++){
                        if (!this.valueAt(row, col).isZero()){
                            // ... and swap rows
                            this._rowSwap(core_row, row);
                            break;
                        }
                    }
                }
                
                // If the non-zero element is found, stop searching
                if (! this.valueAt(core_row, col).isZero())
                    break;
            } 
            
            // This will trigger if *all* elements of the row are 0
            if (col==this.getColumnCount())
                continue;
            
            // for each row in the matrix ...
            for (int row=0; row<this.getRowCount(); row++){
                
                // ... except the current one ..
                if (row==core_row)
                    continue;
                
                // .. and if the element is 0 ...
                if (this.valueAt(row, col).isZero())
                    continue;
                
                // .. perform a row operation to zero that element
                this._rowOperation(
                    row, this.valueAt(core_row,col), 
                    core_row, this.valueAt(row,col).getNegative()
                );
            }
        }
        
        // Now do a second pass of rows ...
        for (int row=0; row<this.getRowCount();row++){
            int col = row;
            boolean pivot_found=false;
            
            // ... find the pivot element ...
            for (;col<this.getColumnCount();col++){
                if (! this.valueAt(row, col).isZero()){
                    pivot_found=true;
                    break;
                }
            }
            
            // ... if there is no pivor (the row is zero) continue ...
            if (!pivot_found)
                continue;
            
            // ... make the element unity
            this._rowDivide( row, this.valueAt(row,col).copy() );
        }
        
        return this;
    }
    
    /**
     * Divides an entire row by a Real number.
     * To multiply a row by a Real you can use _rowOperation
     * This function has been implemented to facilitate the divide() function
     * of BigDecimal which is more accurate than inverting a Real and 
     * multiplying
     * 
     * Like all rowOperations, this one supports an augmented matrix
     * 
     * @param row The row index to be operated
     * @param divisor the Real which the row will be divided by
     */
    public void _rowDivide(int row, Real divisor){
        for (int col=0; col<this.getColumnCount();col++)
            this.valueAt(row, col).div(divisor);
        if (getAugmented()!=null)
            ((Matrix) getAugmented())._rowDivide(row, divisor);
    }

    /**
     * Returns the characteristic polynomial of this Matrix
     * 
     * @return the characteristic polynomial of this Matrix
     */
    public Polynomial getCharacteristicPolynomial(){
        assertSquare();
        GenericMatrix<Polynomial> lamda = new GenericMatrix<Polynomial>(this.getRowCount(),this.getColumnCount());
        for (int i=0;i<lamda.getRowCount();i++)
            for (int j=0;j<lamda.getColumnCount();j++)
                if (i==j)
                    lamda.set(i, j, new Polynomial(this.valueAt(i,j).getPrimitive(),-1.0));
                else
                    lamda.set(i, j, new Polynomial(this.valueAt(i,j).getPrimitive(),0.0));
        return lamda.det();
    }
    
    public Vector getEigenvalues(){
        return getCharacteristicPolynomial().getRoots();
    }
    
    /**
     * Returns a matrix with random values from 0 to 1
     * 
     * @param n the resulting matrix rows
     * @param m the resulting matrix columns
     * @return a matrix with random values 0-1
     */
    public static Matrix random(int n, int m){
        Matrix result = new Matrix(n,m);
        Random rnd = new Random();
        for (int i=0;i<result.getRowCount();i++)
            for (int j=0;j<result.getColumnCount();j++)
                result.set(i, j, rnd.nextDouble());
        return result;
    }
    
    /**
     * Temporary: Perform some benchmarkings for the matrix configuration
     * 
     * @param sz the size of the matrices involved
     */
    public static void unittest(int sz){
        Matrix a = Matrix.random(sz, sz);
        Matrix b = Matrix.random(sz, sz);
        GenericMatrix c = a.getProduct(b);
    }
    
    /**
     * Subtract the minimum Vector from each row
     * 
     * @return this matrix
     */
    public Matrix ground(){
        int N = this.getRowCount();
        Matrix min_vector = Matrix.ones(N,1).getProduct(getMinVector().getAsRowMatrix());
        add(min_vector.negateElements());
        return this;
    }

    /**
     * Subtract the average Vector from each row
     * 
     * @return this matrix
     */
    public Matrix center(){
        int N = this.getRowCount();
        Matrix avg_vector = Matrix.ones(N,1).getProduct(getAverageVector().getAsRowMatrix());
        add(avg_vector.negateElements());
        return this;
    }

    /**
     * Divide each row by the max Vector
     * 
     * @return this matrix
     */
    public Matrix scale(){
        int N = this.getRowCount();
        Matrix max_vector = Matrix.ones(N,1).getProduct(getMaxVector().getAsRowMatrix());
        multiplyElements(max_vector.invertElements());
        return this;
    }

    /**
     * Divide each row by the std Vector
     * 
     * @return this matrix
     */    
    public Matrix standarize(){
        int N = this.getRowCount();
        Matrix std_mat = Matrix.ones(N,1).getProduct(getStdVector().getAsRowMatrix());
        multiplyElements(std_mat.invertElements());
        return this;
    }
    
    /**
     * Divide each row by the norm Vector
     * 
     * @return this matrix
     */
    public Matrix normalize(){
        int N = this.getRowCount();
        Matrix norm_vector = Matrix.ones(N,1).getProduct(this.getNormVector().getAsRowMatrix());
        multiplyElements(norm_vector.invertElements());
        return this;
    }
    
    /**
     * Create a new matrix with only the rows specified by the indices argument
     * 
     * @param indices the indices of this matrix to be copied to the output matrix
     * @return the resulting matrix
     */
    public Matrix pickRows(Integer...indices){
        Matrix result = new Matrix(indices.length,this.getColumnCount());
        for (int i=0;i<result.getRowCount();i++)
            result.setRow(i, this.getRow(indices[i]));
        return result;
    }
    
    /**
     * Create a new matrix with only the rows specified by the indices argument
     * 
     * @param indices an boolean array which specifies if the row should be 
     * copied or not
     * @return the resulting matrix
     */
    public Matrix pickRows(boolean[] indices){
        assert this.getRowCount()==indices.length: 
            String.format("Matrix rows (%d) and indices length(%d) do not have the same size",
            this.getRowCount(), indices.length);
        
        int true_cnt = 0;
        for (boolean index : indices)
            if (index)
                true_cnt++;
        
        Matrix result = new Matrix(true_cnt,this.getColumnCount());
        int result_idx=0;
        for (int i=0;i<this.getRowCount();i++)
            if (indices[i])
                result.setRow(result_idx++, this.getRow(i));
        return result;
    }
    
    /**
     * Create a new matrix with only the columns specified by the indices argument
     * 
     * @param indices the indices of this matrix to be copied to the output matrix
     * @return the resulting matrix
     */
    public Matrix pickColumns(Integer...indices){
        Matrix result = new Matrix(this.getRowCount(),indices.length);
        for (int i=0;i<result.getColumnCount();i++)
            result.setColumn(i, this.getColumn(indices[i]));
        return result;
    }
    
    /**
     * Create a new matrix with only the columns specified by the indices argument
     * 
     * @param indices an boolean array which specifies if the column should be 
     * copied or not
     * @return the resulting matrix
     */
    public Matrix pickColumns(boolean[] indices){
        assert this.getColumnCount()==indices.length: 
            String.format("Matrix columns (%d) and indices length(%d) do not have the same size",
            this.getColumnCount(), indices.length);
        
        int true_cnt = 0;
        for (boolean index : indices)
            if (index)
                true_cnt++;
        
        Matrix result = new Matrix(this.getRowCount(),true_cnt);
        int result_idx=0;
        for (int i=0;i<this.getColumnCount();i++)
            if (indices[i])
                result.setColumn(result_idx++, this.getColumn(i));
        return result;
    }
    
    
    
    /**
     * The main executable function.
     * Demonstrates the matrix functionality
     * 
     * @param args Commmand line arguments
     */
    public static void main(String[] args){
        
        Core.Tests.diagonalization_test();
        
    }
    
}
