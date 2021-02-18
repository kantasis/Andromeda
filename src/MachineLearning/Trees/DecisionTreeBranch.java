package MachineLearning.Trees;

import Core.Logger;
import DataStructures.MatrixComparator;
import MachineLearning.LabelArray;
import Math.Matrix;
import Math.Operatables.Real;
import Math.Vector;
import java.util.ArrayList;

public class DecisionTreeBranch extends DecisionTreeNode {
    
    private DecisionTreeNode leftNode, rightNode;
    private Real _branch_th;
    private int _branch_idx;

    public DecisionTreeBranch(int inputs, int outputs) {
        super(inputs, outputs);
    }

    public DecisionTreeBranch(DecisionTreeNode parent){
        super(parent);
    }
        
    public void fit(Matrix x_mat, LabelArray labelArray){
        _branch_th = new Real(Double.MAX_VALUE);
        _branch_idx = -1;
        
        int split_cnt = 0;
        Real maxIG_r = new Real(Double.MIN_VALUE);
        Real fullEntropy = labelArray.getEntropy();
        Integer[] maxCostIndices_idxArr = null;
        Integer[] maxCostCompIndices_idxArr = null;
        
        for (int col_idx=0; col_idx<x_mat.getColumnCount();col_idx++){
            ArrayList<Integer> indices_lst = new MatrixComparator(x_mat).sortByColumn(col_idx);
            Real prevValue_r = x_mat.valueAt(indices_lst.get(0), col_idx);
            for (int i=1; i<indices_lst.size();i++){
                
                Real currValue_r = x_mat.valueAt(indices_lst.get(i), col_idx);
                
                // Ignore duplicate values (it would have been like not updating the threshold
                if (currValue_r.equals(prevValue_r))
                    continue;

                // Calculate the threshold
                Real curr_thr = currValue_r.copy().add(prevValue_r).multiply(0.5);

                // Get the range of indices from 0 to i
                Integer[] indices = indices_lst.subList(0, i).toArray(new Integer[0]);
                Integer[] compIndices = indices_lst.subList(i, indices_lst.size()).toArray(new Integer[0]);
                
                // Left Split
                LabelArray leftSplit_lar = labelArray.pickRows(indices);
                Real leftSplitP_r = new Real((double)indices.length / x_mat.getRowCount());
                Real leftScore_r = leftSplit_lar.getEntropy();

                // Right Split
                LabelArray rightSplit_lar = labelArray.pickRows(compIndices);
                Real rightSplitP_r = new Real((double)compIndices.length / x_mat.getRowCount());
                Real rightScore_r = rightSplit_lar.getEntropy();
                
                // Calculate the Information Gain
                Real informationGain_r = fullEntropy.copy().diff(rightScore_r.getProduct(rightSplitP_r).add(leftScore_r.getProduct(leftSplitP_r)));
                
                /*
                Logger.log("Iteration (%d %d):%s",i,col_idx,informationGain_r);
                String temp = "";
                for (int idx : indices)
                    temp+=String.format("[%10d] ", idx);
                temp+=String.format("--- ");
                for (int idx : compIndices)
                    temp+=String.format("[%10d] ", idx);
                Logger.log(temp+": "+curr_thr);
                temp="";
                for (int idx : indices)
                    temp+=String.format("%s ", x_mat.valueAt(idx, col_idx));
                temp+=String.format("--- ");
                for (int idx : compIndices)
                    temp+=String.format("%s ", x_mat.valueAt(idx, col_idx));
                Logger.log(temp);
                Logger.log("Score: %s\t-\t%s * %s\t-\t%s * %s", fullEntropy, leftSplitP_r, leftScore_r, rightSplitP_r, rightScore_r);
                Logger.log("%s", informationGain_r);
                Logger.log("");
                */
                // Find the split that maximizes the information gain
                if (informationGain_r.isGreater(maxIG_r)){
                    //Logger.log("Updating the split %d, %s, %s", minCol_idx, threshold_r, minGini);
                    _branch_idx = col_idx;
                    _branch_th = curr_thr;
                    
                    maxIG_r = informationGain_r;
                    split_cnt = indices.length;
                    maxCostIndices_idxArr = indices;
                    maxCostCompIndices_idxArr = compIndices;
                    
                } // if
                
                prevValue_r=currValue_r;
            } // i
            //Logger.log("---");
        } // col_idx
        
        /*
        Logger.indent();
        Logger.log("Optimal Split");
        Logger.log("Cost:\t%s", maxIG_r);
        Logger.log("Column:\t%s", _branch_idx);
        Logger.log("Threshold:\t%s", _branch_th);
        Logger.log("ClassCount:\t%s", split_cnt);
        */
        //Logger.log("%d ", split_cnt);
        
        Matrix leftSplit_mat = x_mat.pickRows(maxCostIndices_idxArr);
        Matrix rightSplit_mat = x_mat.pickRows(maxCostCompIndices_idxArr);
        LabelArray leftSplit_lar = labelArray.pickRows(maxCostIndices_idxArr);
        LabelArray rightSplit_lar = labelArray.pickRows(maxCostCompIndices_idxArr);
        
        if (leftSplit_lar.getL()==1)
            leftNode = new DecisionTreeLeaf(this);
        else 
            leftNode = new DecisionTreeBranch(this);
        leftNode.fit(leftSplit_mat, leftSplit_lar);
        
        if (rightSplit_lar.getL()==1)
            rightNode = new DecisionTreeLeaf(this);
        else 
            rightNode = new DecisionTreeBranch(this);
        rightNode.fit(rightSplit_mat, rightSplit_lar);
        
        Logger.dedent();
        
    }
    
    public Vector classify(Vector pattern){
        assert pattern.getLength()<=this.getInputCount() : String.format("DecisionTreeBranch.classify: pattern is not the proper size");
        if (pattern.get(_branch_idx).isLess(_branch_th))
                return leftNode.classify(pattern);
            else
                return rightNode.classify(pattern);
    }
        
}
