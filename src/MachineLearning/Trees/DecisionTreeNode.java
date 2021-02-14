/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MachineLearning.Trees;

import Math.Vector;

public abstract class DecisionTreeNode extends MachineLearning.Classifier{

    public final DecisionTreeNode _parent;
    
    public DecisionTreeNode(DecisionTreeNode parent){
        _parent = parent;
    }
    
    public abstract Vector classify(Vector pattern);
}
