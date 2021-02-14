package MachineLearning.Trees;

import Math.Matrix;
import Math.Operatables.Real;
import Math.Vector;

public class DecisionTreeBranch extends DecisionTreeNode {
    
    private DecisionTreeNode leftNode, rightNode;
    private Real _branch_th;
    private int _branch_idx;
    
    public DecisionTreeBranch(DecisionTreeNode parent){
        super(parent);
    }
    
    @Override
    public int getInputCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getOutputCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    } 

    public Vector classify(Vector pattern){
        if (pattern.get(_branch_idx).isLess(_branch_th))
                return leftNode.classify(pattern);
            else
                return rightNode.classify(pattern);
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
