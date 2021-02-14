/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MachineLearning;

import Core.Logger;
import Math.Matrix;
import Math.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


public class LabelArray {
    
    private Integer[] _labels_intLstN;
    private ArrayList _labelKeys_strLstL;
    
    /**
     * Contstructor to create a LabelArray from a list of labels
     * @param labels the list of strings
     */
    public LabelArray(List labels){
        
        int N = labels.size();
        
        // Get the distinct label keys
        HashSet<String> distinctLabels_hashSet = new HashSet<String>(labels);
        _labelKeys_strLstL = new ArrayList<String>();
        Iterator iterator = distinctLabels_hashSet.iterator();
        while (iterator.hasNext())
            _labelKeys_strLstL.add(iterator.next());
        
        // store the labels as indices
        _labels_intLstN = new Integer[N];        
        for (int i=0;i<N;i++)
            _labels_intLstN[i] = _labelKeys_strLstL.indexOf(labels.get(i));

    }
    
    /**
     * Contstructor to create a LabelArray from an array keys
     * @param labels 
     */
    public LabelArray(Object[] labels){
       this(Arrays.asList(labels));
    }
    
    /**
     * Display this object to the console
     */
    public void show(String name){
        Logger.log("LabelArray: %s", name);
        Logger.indent();
        Logger.log("Keys:");
        
        for(Object label: _labelKeys_strLstL)
            Logger.log("  "+label.toString());

        Logger.log("Labels:");
        for(int i=0;i<_labels_intLstN.length;i++)
            Logger.log("  %d) %d",i,_labels_intLstN[i]);
        Logger.dedent();
    }
    
    /**
     * Return the count of labeled patterns 
     * @return the count of labeled patterns
     */
    public int getN(){
        return _labels_intLstN.length;
    }
    
    /**
     * Return the key count
     * @return the classes count
     */
    public int getL(){
        return _labelKeys_strLstL.size();
    }
    
    /**
     * Gives the label index of a pattern
     * @param idx_N the index of the pattern
     * @return the label index
     */
    public int getLabel(int idx_N){
        return _labels_intLstN[idx_N];
    }
    
    /**
     * Gives the label key of a pattern
     * @param idx_N the index of the pattern
     * @return the label object
     */
    public Object getLabelObject(int idx_N){
        int label_idx = getLabel(idx_N);
        return _labelKeys_strLstL.get(label_idx);
    }
    
    /**
     * Returns the label key Object specified by the index
     * @param idx_L
     * @return the label key Object
     */
    public Object getKey(int idx_L){
        return _labelKeys_strLstL.get(idx_L);
    }
    
    /**
     * Calculate the boolean mask over all patterns for a specific label index
     * @param idx_L
     * @return the boolean mask
     */
    public boolean[] getMask(int idx_L){
        boolean[] result = new boolean[getN()];
        for(int i=0;i<result.length;i++)
            result[i] = getLabel(i) == idx_L;
        return result;
    }

    /**
     * Return a copy of the label index array
     * @return the array of indices
     */
    public Integer[] getLabels(){
        Integer[] result = new Integer[getN()];
        for(int i=0;i<result.length;i++)
            result[i] = getLabel(i);
        return result;
    }
    
    /**
     * Encode the LabelList as a one-hot matrix of 0-1
     * @param idxLstL the list of indices to be included to the matrix. 
     *      If empty returns all indices in order
     * @return the ont-hot encoding of the label list
     */
    public Matrix toOneHotMatrix(int...idxLstL){
        /*TODO:
            At some point when I implement the sparse matrix class, this function
            should return this type
        */
        if (idxLstL.length==0){
            idxLstL = new int[getL()];
            for (int i=0;i<idxLstL.length;i++)
                idxLstL[i]=i;
        }
        int column_cnt = idxLstL.length;
        Matrix result_matNL = new Matrix(getN(),column_cnt);
        for (int i=0;i<column_cnt;i++){
            Vector column = new Vector(getN());
            boolean[] mask = getMask(idxLstL[i]);
            for (int j=0;j<getN();j++)
                column.set(j, (mask[j])?(1):(0));
            result_matNL.setColumn(i, column);
        }
        return result_matNL;
    }
    
    /* TODO:
    Matrix getLabelMatrix()
        returns the one-hot (0-1) encoded NxL matrix of the labels
    Figure out a way to get this from a CSV
    */
    
    public static void main(String[] args){
        
        String[] labels_strArr = {
            "one",
            "one",
            "one",
            "Two",
            "one",
            "Two",
            "Two",
            "two"
        };
        
        ArrayList<String> labels_arrList = new ArrayList <String>(Arrays.asList(labels_strArr));
        
        //LabelArray labelArray = new LabelArray(labels_arrList);
        Integer[] temp = {1,1,2,1,1,11,2,1,4,1,4,5};
        LabelArray labelArray = new LabelArray(temp);
        
        labelArray.show("Test LabelArray");
        labelArray.toOneHotMatrix().show("LabelArray Matrix");
        
        
    }
    
}
