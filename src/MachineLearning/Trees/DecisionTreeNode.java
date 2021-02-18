package MachineLearning.Trees;

import Core.Logger;
import MachineLearning.LabelArray;
import Math.Matrix;
import Math.Vector;

public abstract class DecisionTreeNode extends MachineLearning.Classifier{

    protected final DecisionTreeNode _parent;
    
    private int _input_cnt;
    private int _output_cnt;
    
    public abstract Vector classify(Vector pattern);
    public abstract void fit(Matrix dataset, LabelArray labels);
    
    public DecisionTreeNode(int inputs, int outputs){
        _parent = null;
        _input_cnt = inputs;
        _output_cnt = outputs;
    }
    
    public DecisionTreeNode(DecisionTreeNode parent){
        _parent = parent;
        _input_cnt = _parent.getInputCount();
        _output_cnt = _parent.getOutputCount();
    }
    
    @Override
    public int getInputCount() {
        return _input_cnt;
    }

    @Override
    public int getOutputCount() {
        return _output_cnt;
    } 

    @Override
    public Matrix classify(Matrix x_matNM) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        int N = x_matNM.getRowCount();
        int M = x_matNM.getColumnCount();
        int L = getOutputCount();
        
        Matrix result_matNL = new Matrix(N,L);
        for (int i=0;i<N;i++){
            Vector pattern = x_matNM.getRow(i);
            result_matNL.setRow(i, this.classify(pattern));
        }
        return result_matNL;
    }

}
