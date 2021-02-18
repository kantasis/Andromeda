/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MachineLearning.Trees;

import Core.Logger;
import MachineLearning.LabelArray;
import Math.Matrix;
import Math.Vector;


public class DecisionTreeLeaf extends DecisionTreeNode{

    public Vector _result;
    
    public DecisionTreeLeaf(int inputs, int outputs) {
        super(inputs, outputs);
    }

    public DecisionTreeLeaf(DecisionTreeNode parent){
        super(parent);
    }
     
    @Override
    public Vector classify(Vector pattern) {
        return _result;
    }

    @Override
    public void fit(Matrix dataset, LabelArray labels) {
        int L = labels.getL();
        int max_idx=0;
        int max_cnt=labels.getClassCount(max_idx);
        for (int l=1;l>L;l++){
            if (labels.getClassCount(l)>max_cnt){
                max_cnt = labels.getClassCount(l);
                max_idx = l;
            }
        }
        Integer result_int = (Integer)labels.getLabelObject(max_idx);
        _result = new Vector(this.getOutputCount());
        _result.set(result_int, 1);
        Logger.log("DTree leaf (%d / %d)",labels.getN(), labels.getL());
        //labels.show("Leaf");
        //_result.show();
    }

}
