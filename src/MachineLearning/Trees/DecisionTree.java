/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MachineLearning.Trees;

import Core.Logger;
import DataStructures.CSVLoader;
import DataStructures.MatrixComparator;
import MachineLearning.Classifier;
import MachineLearning.LabelArray;
import Math.Matrix;
import Math.Operatables.Real;
import java.util.ArrayList;
import java.util.List;


public class DecissionTree extends Classifier{

    public int maxDepth_cnt;
    public int minSamples_cnt;
    private DecisionTreeNode _root;
    
    @Override
    public Matrix classify(Matrix x_mat) {
        return _root.classify(x_mat);
    }

    @Override
    public int getInputCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getOutputCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void split(Matrix dataset, LabelArray labels){
        Real entropy = labels.getEntropy();
        
        int col_idx = 0;
        ArrayList<Integer> sorted_idxLst = new MatrixComparator(dataset).sortByColumn(col_idx);
        
        for(int row_idx=1;row_idx<dataset.getRowCount();row_idx++){
            Real prev_r = dataset.valueAt(sorted_idxLst.get(row_idx-1),col_idx);
            Real curr_r = dataset.valueAt(sorted_idxLst.get(row_idx),col_idx);
            Real threshold_r = prev_r.getAdd(curr_r).div(new Real(2));
            
            
        }
    }
    
    public static void main(String...args){
        String filename="C:\\Users\\kostis\\Dropbox\\iris.csv";
        //filename = "C:\\Users\\kostis\\Dropbox\\temp.csv";

        ArrayList <String> data = CSVLoader.readTextFile(filename);
        data.remove(0);     //remove header
        ArrayList <String> outputs = CSVLoader.getColumns(data,",",true,4);
        ArrayList <String> inputs = CSVLoader.getColumns(data,",",true,0,1,2,3);
        
        Matrix x_mat = CSVLoader.toMatrix(inputs,",",true);
        LabelArray labelArray = new LabelArray(outputs);
        //labelArray.show("Test LabelArray");
        //labelArray.toOneHotMatrix().show();
        
        DecisionTreeBranch tree = new DecisionTreeBranch(x_mat.getColumnCount(),labelArray.getL());
        
        labelArray.show("Original");
        
        tree.fit(x_mat, labelArray);
        tree.classify(x_mat).show("Result");
        
        
        
        /*
        Real threshold_r = new Real(Double.MAX_VALUE);
        int minCol_idx = -1;
        int split_cnt = 0;
        Real minIG_r = new Real(Double.MAX_VALUE);
        Real fullEntropy = labelArray.getEntropy();

        for (int col_idx=0; col_idx<x_mat.getColumnCount();col_idx++){
            
            ArrayList<Integer> indices_lst = new MatrixComparator(x_mat).sortByColumn(col_idx);
            for (int i=1; i<indices_lst.size();i++){
                
                // Get the range of indices from 0 to i
                Integer[] indices = indices_lst.subList(0, i).toArray(new Integer[0]);
                Integer[] compIndices = indices_lst.subList(i, indices_lst.size()).toArray(new Integer[0]);
                
                // Left Split
                Matrix leftSplit_mat = x_mat.pickRows(indices);
                LabelArray leftSplit_lar = labelArray.pickRows(indices);
                Real leftSplitP_r = new Real((double)indices.length / x_mat.getRowCount());
                Real leftScore_r = leftSplit_lar.getEntropy();

                // Right Split
                Matrix rightSplit_mat = x_mat.pickRows(compIndices);
                LabelArray rightSplit_lar = labelArray.pickRows(compIndices);
                Real rightSplitP_r = new Real((double)compIndices.length / x_mat.getRowCount());
                Real rightScore_r = rightSplit_lar.getEntropy();
                
                // Calculate the Information Gain
                Real informationGain_r = fullEntropy.copy().diff(rightScore_r.getProduct(rightSplitP_r).add(leftScore_r.getProduct(leftSplitP_r)));
                Real curr_thr = x_mat.valueAt(indices_lst.get(i), col_idx).copy();
                curr_thr.add(x_mat.valueAt(indices_lst.get(i-1), col_idx)).multiply(0.5);
                        
                if (informationGain_r.isLess(minIG_r)){
                    //Logger.log("Updating the split %d, %s, %s", minCol_idx, threshold_r, minGini);
                    minCol_idx = col_idx;
                    minIG_r = informationGain_r;
                    threshold_r = curr_thr;
                    split_cnt = indices.length;
                }
            }
        }
        Logger.log("Found the following: %d @ %s\t %s",minCol_idx, threshold_r, minIG_r);
        Logger.log("#: %d", split_cnt);
        */
    }

    
    
}
