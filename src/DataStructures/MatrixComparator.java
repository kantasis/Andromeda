/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DataStructures;

import Core.Logger;
import Math.Matrix;
import Math.Operatables.Real;
import Math.Vector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class is used to sort matrix rows by columns
 * @author kostis
 */
public class MatrixComparator implements Comparator<Integer>{

    private Matrix _matrix;
    private int _col_idx=0;
    
    /**
     * Constructor of the class
     * @param matrix the matrix to be sorted
     */
    public MatrixComparator(Matrix matrix){
        _matrix = matrix;
    }
    
    /**
     * Override method to compare the Real values of the specified indices
     * @param idx1
     * @param idx2
     * @return The result of the comparison
     */
    @Override
    public int compare(Integer idx1, Integer idx2) {
        Real one = _matrix.valueAt(idx1, _col_idx);
        Real other = _matrix.valueAt(idx2, _col_idx);
        return one.compareTo(other);
    }
    
    /**
     * Calculate the index array of the rows of the matrix sorted by a column
     * @param col_idx the column to be used as a key for the sort
     * @return the index array of the sort
     */
    public ArrayList<Integer> sortByColumn(int col_idx){
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i=0;i<_matrix.getRowCount();i++)
            result.add(i);
        _col_idx = col_idx;
        Collections.sort(result,this);
        
        return result;
    }

    public static void main(String...args){
        Matrix x = (Matrix) Matrix.random(20,4).multiply(10);
        x = x.round();
        x.show("random matrix");
        
        MatrixComparator matrixComparator = new MatrixComparator(x);
        ArrayList<Integer> indices = matrixComparator.sortByColumn(2);
        
        // Try out functional operator
        indices.stream().forEach((i) -> {
            Logger.log("--: %d",i);
        });
        
        x.pickRows(indices.toArray(new Integer[0])).show("Sorted");
        
        
    }
    
}
