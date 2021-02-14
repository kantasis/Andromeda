/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MachineLearning;

import Core.Logger;
import DataStructures.CSVLoader;
import static DataStructures.CSVLoader.getColumns;
import static DataStructures.CSVLoader.readTextFile;
import static DataStructures.CSVLoader.toMatrix;
import Math.Matrix;
import Math.Operatables.Real;
import Math.Vector;
import java.util.ArrayList;


public class BayesClassifier extends Classifier {

    private Vector[] _means_vecLstL;
    private Matrix[] _cov_matLstL;
    private int[] _classPattern_cntLstL;
    private Vector _classProbabilities_vecL;
    
    @Override
    public Matrix classify(Matrix x_matNM) {
        int N = x_matNM.getRowCount();
        int L = getOutputCount();
        Matrix result_matNL = new Matrix(N,L);
        
        for (int i=0; i<L; i++){
            Vector probabilities_vecN = this.evaluate(x_matNM, i);
            result_matNL.setColumn(i, probabilities_vecN);
        }
        
        return result_matNL;
    }

    @Override
    public int getInputCount() {
        return _means_vecLstL[0].getLength();
    }

    @Override
    public int getOutputCount() {
        return _classPattern_cntLstL.length;
    }

    public void train(Matrix x_matNM, LabelArray labelArray){
        int L = labelArray.getL();
        _means_vecLstL = new Vector[L];
        _cov_matLstL = new Matrix[L];
        _classPattern_cntLstL = new int[L];
        _classProbabilities_vecL = new Vector(L);
        int _totalPatterns_cnt=0;
        for (int l=0;l<L;l++){
            boolean[] classMask = labelArray.getMask(l);
            Matrix classPatterns_mat = x_matNM.pickRows(classMask);
            Matrix classLabels_mat = labelArray.toOneHotMatrix(l);
            _means_vecLstL[l] = classPatterns_mat.getAverageVector();
            _cov_matLstL[l] = classPatterns_mat.getCovariance();
            _classProbabilities_vecL.set(l, classPatterns_mat.getRowCount());
            _classPattern_cntLstL[l] = classPatterns_mat.getRowCount();
            _totalPatterns_cnt+=_classPattern_cntLstL[l];
        }
        _classProbabilities_vecL.div(new Real(_totalPatterns_cnt));
    }
    
    public static Vector multivariatePDF(Matrix data_matNM, Vector mean_vecM, Matrix cov_matMM){
        int N = data_matNM.getRowCount();
        int M = data_matNM.getColumnCount();
        
        Matrix covInv = cov_matMM.getInverse();
        Real factor = new Real(Math.PI).multiply(2).power(M).multiply(cov_matMM.det()).sqrt();
        Vector result_vec = new Vector(N);
        
        for (int i=0;i<N;i++){
            Vector temp_vec = data_matNM.getRow(i);
            temp_vec.diff(mean_vecM);
            Matrix temp_mat = temp_vec.getAsRowMatrix().getProduct(covInv).getProduct(temp_vec.getAsColMatrix());
            Real result_r = temp_mat.multiply(-1.0/2.0).valueAt(0, 0).getExp();
            result_vec.set(i, result_r.multiply(factor));
        }
        return result_vec;
    }
    
    public Vector evaluate(Matrix data, int class_idx){
        assert class_idx < this.getOutputCount(): 
                String.format("Error: BayesClassifier.evaluate: class_idx(%d) bigger than the max %d", class_idx, this.getOutputCount());
        Vector probX_vecN = new Vector(data.getRowCount());
        Vector probX_givenC_currentClass = null;
        for (int l=0; l<this.getOutputCount();l++){
            Vector probX_givenC_vecL = BayesClassifier.multivariatePDF(data, _means_vecLstL[l], _cov_matLstL[l]);
            probX_givenC_vecL.multiply(_classProbabilities_vecL.get(l));
            probX_vecN.add(probX_givenC_vecL);
            probX_givenC_vecL.getSize().show("probX_givenC_vecL");
            if (l == class_idx)
                probX_givenC_currentClass = probX_givenC_vecL;
        }
        probX_givenC_currentClass.getSize().show("probX_givenC_currentClass");
        return (Vector) probX_givenC_currentClass.multiplyElements(probX_vecN.invertElements());
    }
    
    public void show(String name){
        Logger.log("Bayesian Model:"+name);
        for (int l=0; l<this.getOutputCount();l++){
            Logger.log("Class #%d:",l);
            Logger.indent();
            _means_vecLstL[l].show("Mean Vector");
            _cov_matLstL[l].show("Cov Matrix");
            Logger.dedent();
        }
    }
    
    public static void main(String[] args){
        
        String filename="C:\\Users\\kostis\\Dropbox\\iris.csv";

        ArrayList <String> data = CSVLoader.readTextFile(filename);
        data.remove(0);     //remove header
        ArrayList <String> outputs = getColumns(data,",",true,4);
        ArrayList <String> inputs = getColumns(data,",",true,0,1,2,3);
        
        Matrix x_mat = toMatrix(inputs,",",true);
        LabelArray labelArray = new LabelArray(outputs);
        
        BayesClassifier model = new BayesClassifier();
        
        model.train(x_mat, labelArray);
        
        model.show("Model");
        Matrix result = model.classify(x_mat);
        result.show("Result");
        
        for (int i=0;i<result.getRowCount();i++){
            int max_idx = result.getRow(i).argMax();
            Logger.log("Class:"+max_idx);
        }
    }
    
}
