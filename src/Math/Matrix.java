package Math;

import Core.Logger;
import Math.Operatables.GenericMatrix;
import Math.Operatables.Real;
import java.util.ArrayList;
import java.util.Random;

public class Matrix extends GenericMatrix<Real> {
    
    private transient Matrix _augmented;
    
    /**
     * Constructor for the Matrix class
     * Create a new Matrix defining the number of rows and columns
     * 
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     */
    public Matrix(int rows,int columns){
        super(rows,columns);
        for(int i=0;i<getRows();i++)
            for(int j=0;j<getColumns();j++)
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
            for(int j=0;j<data.length;j++){
                this.set(i,j,new Real(data[i][j]));
            }
        }
    }
    
    public Matrix set(int i, int j, double v){
        return (Matrix) this.set(i, j, new Real(v));
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
    public Matrix getAugmented(){
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
    public Matrix setAugmented(Matrix that){
        _augmented = that;
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
        assert values.getLength()==this.getColumns() : String.format("Incompatible sizes %s / %s",this.getColumns(),values.getLength());
        for (int i=0;i<getRows();i++)
            for (int j=0;j<getColumns();j++)
                this.get(i, j).add(values.get(j));
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
        for (int i=0;i<getRows();i++)
            for (int j=0;j<getColumns();j++)
                this.get(i,j).add(value);
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
            Real sum=Real.zero();
            for (int i=0;i<getRows();i++)
                sum.add(this.get(i,j));
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
        Vector result = new Vector(this.getColumns());
        for (int j=0;j<this.getColumns();j++){
            Real sum= Real.unit();
            for (int i=0;i<getRows();i++)
                sum.multiply(this.get(i,j));
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
        return (Vector) this.getSumVector().multiply(1.0/this.getRows());
    }
      
    /**
     * Get the max of each column
     * 
     * @return the vector max of this matrix
     */
    public Vector getMaxVector(){
        Vector result = new Vector(this.getColumns());
        for (int j=0;j<this.getColumns();j++){
            Real max=this.get(0,j);
            for (int i=1;i<getRows();i++)
                if (this.get(i,j).getPrimitive()>max.getPrimitive())
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
            Real min=this.get(0,j);
            for (int i=1;i<getRows();i++)
                if (this.get(i,j).getPrimitive()<min.getPrimitive())
                    min=this.get(i,j);
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
            .multiply(new Real(getRows()).inv());
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
        Matrix result = new Matrix(cov.getRows(),cov.getColumns());
        for (int i=0;i<result.getRows();i++)
            for (int j=0;j<result.getColumns();j++)
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
        int x = this.getRows();
        if (x<this.getColumns())
            x=this.getColumns();
        Vector result = new Vector(x);
        for (int i=0;i<x;i++)
            result.set(i,this.get(i,i));
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
        assert this.getColumns()==this.getRows() : String.format("For now I can not calculate pseudo-inverted matrix",this.getSize());
        Real det = this.det();
        assert !det.isZero() : String.format("The matrix is not invertible",this.getSize());
        
        Matrix result = Matrix.eye(this.getRows());
        Matrix temp = (Matrix) this.copy();
        
        temp.setAugmented(result);
            temp._utrig()._ltrig();
        temp.setAugmented(null);
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
     * Create a copy of this matrix.
     * 
     * @return a new matrix that is a copy of this one
     */
    public Matrix copy(){
        Matrix result = new Matrix (this.getRows(),this.getColumns());
        for (int i=0;i<result.getRows();i++)
            for (int j=0;j<result.getColumns();j++)
                result.set(i,j,this.get(i,j).copy());
        return result;
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
        Vector result = new Vector(this.getRows());
        //System.out.println(this.getRows());
        for(int row=0;row<this.getRows();row++){
            result.set(row, this.get(row, col).getPrimitive());
        }
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
        Vector result = new Vector(this.getColumns());
        //System.out.println(this.getRows());
        for(int col=0;col<this.getColumns();col++){
            result.set(col, this.get(row, col).getPrimitive());
        }
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
        this.show("Solving Linear System");
        ArrayList<Vector> result = new ArrayList<Vector>();
        result.add(new Vector(this.getRows()));
        Matrix echelon = this.copy();
        boolean homogenous = b==null || b.isZero();
        Vector y;
        if (b==null)
            y = new Vector(this.getRows());
        else
            y = b.copy();
        echelon.setAugmented(y);
            echelon.gaussianElemination();
        echelon.setAugmented(null);
        
        echelon.show("Echelon");
        for (int col=0;col<echelon.getColumns();col++){
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
                temp.set(col, Real.unit());
                result.add(temp);
            }
        }
        return result;
    }
    
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
            
            Logger.log("------------------------------------------ Eig: %s (%s)", eigenValue, eigenValue.getPrimitive());
            // Reduce each diagonal element by the eigenvalue
            for (int j=0;j<temp.getColumns();j++)
                temp.get(j, j).add(eigenValue.getNegative());
            
            ArrayList<Vector> solutions = temp.solveLinearSystem(null);
            if (solutions.size()!=2){
                for (Vector x : solutions)
                    x.show("Eig: "+eigenValue);
            }
            int geometricMultiplicity = solutions.size()-1;
            //assert solutions.size()>=2 : String.format("something went wrong trying to find the eigenvectors. Solution size:%d", solutions.size());
            if (geometricMultiplicity != algebraicMultiplicity)
                Logger.log(Logger.LL_WARNING,"geometricMultiplicity != algebraicMultiplicity (%d / %d)",geometricMultiplicity, algebraicMultiplicity);
            Logger.log("Eigenvectors for %s",eigenValue);
            for (Vector sol : solutions)
                sol.show();
            for (int j=1;j<solutions.size();j++)
                eigenVectors.add(solutions.get(j));
        }
        return eigenVectors;
    }
    
    /**
     * Turn this Matrix to reduced-row-echelon form
     * using Gaussian elemination
     * 
     * @return a reference to this matrix
     */
    public Matrix gaussianElemination(){
        // For all the rows in the Matrix
        Logger.log("Gaussian Elemination of");
        this.show("System matrix");
        Logger.indent();
        for (int row=0; row<this.getRows();row++){
            int col=0;
            // Find a row that starts with a non-zero pivot
            Logger.log("Current iteration row %d:",row);
            this.getRow(row).show();
            while (this.get(row,col).isZero()){
                Logger.log("Is Zero %s (%d %d): %s",this.get(row, col),row,col,this.get(row, col).isZero());
                boolean found=false;
                // If this row starts with a non-zero pivot
                for (int row2=row+1;row2<this.getRows();row2++){
                    if (!this.get(row2,col).isZero()){
                        this._rowSwap(row, row2);
                        found=true;
                        Logger.log("Found");
                        break;
                    }
                }
                if(found){
                    System.out.printf("Found!!");
                    break;
                }
                col++;
                if (col>=this.getColumns()){
                    Logger.log("Returning");
                    return this;
                }
                Logger.log("::Is Zero %s (%d %d): %s",this.get(row, col),row,col,this.get(row, col).isZero());
            }
            Logger.log(" Pivot is %s\t%e,%s",this.get(row,col).toStringE(),this.get(row,col).copy().getPrimitive(),this.get(row,col).isZero());
            
            if (!this.get(row,col).isUnit()){
                this.getRow(row).show("Normalizing");
                this._rowOperation(
                    row, this.get(row,col).copy().inv(), 
                    0, Real.zero()
                );
                this.getRow(row).show("Normalized");
            }
            else this.getRow(row).show("No normalization needed");
            
            Logger.log("\t Current: --- ");
            for (int i=0;i<this.getColumns();i++){
                Logger.log("%s ",this.get(row,i));
            }
            Logger.log("");
            Logger.log("Checking rows above and below Col %d:",col);
            for (int i=0;i<this.getRows();i++){
                Logger.log("Row: %d",i);
                if (i==row){
                    Logger.log("Skip diagonal Row: %d / %d",i,row);
                    continue;
                }
                if (!this.get(i, col).isZero())
                    this._rowOperation(
                        i, Real.unit(), 
                        row, this.get(i,col).getMultiply(-1.0).div(this.get(row,col))
                    );
                else this.getRow(i).show("Skipping row: "+i );
            }
            Logger.log("\t/---");
        }
        Logger.dedent    ();
        return this;
    }

    /**
     * Returns the characteristic polynomial of this Matrix
     * 
     * @return the characteristic polynomial of this Matrix
     */
    public Polynomial getCharacteristicPolynomial(){
        assertSquare();
        GenericMatrix<Polynomial> lamda = new GenericMatrix<Polynomial>(this.getRows(),this.getColumns());
        for (int i=0;i<lamda.getRows();i++)
            for (int j=0;j<lamda.getColumns();j++)
                if (i==j)
                    lamda.set(i, j, new Polynomial(this.get(i,j).getPrimitive(),-1.0));
                else
                    lamda.set(i, j, new Polynomial(this.get(i,j).getPrimitive(),0.0));
        return lamda.det();
    }
    
    public Vector getEigenvalues(){
        return getCharacteristicPolynomial().getRoots();
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
        Matrix c = (Matrix) a.getProduct(b);
    }
    
    
    /**
     * The main executable function.
     * Demonstrates the matrix functionality
     * 
     * @param args Commmand line arguments
     */
    public static void main(String[] args){
        //double[][] data = {{0,1,4,1,2},{-1,-2,0,9,-1},{1,2,0,-6,1},{2,5,4,-10,4},{0,0,0,0,0}};
        //double[][] data = {{3,6,-8},{0,0,6},{0,0,2}};
        double[][] data = {{1,-3,3},{3,-5,3},{6,-6,4}};
        //double[][] data = {{2,0,0},{0,3,4},{0,4,9}};
        //double[][] data = {{0,1},{-2,-3}};
        
        Matrix x = new Matrix(data);
        x.show("Original Matrix");
        Logger.indent();
        //x.getInverse().show();
        x.getCharacteristicPolynomial().show("Char poly");
        x.getCharacteristicPolynomial().getRoots().show("Eigenvalues");
        
        
        ArrayList<Vector> results = x.getEigenVectors();
        for (Vector i:results) i.show("EigVec"+results.size());
        
        /*
        Polynomial poly = x.getCharacteristicPolynomial();
        poly.show();
        Vector lamda = poly.getRoots();
        for (int i=0;i<lamda.getLength();i++){
            System.out.printf("%f - %e\n", lamda.get(i).get(),lamda.get(i).get() - Math.round(lamda.get(i).get()));
        }
        */
        Logger.dedent();
        x.show("Final Matrix");
        
    }
}
