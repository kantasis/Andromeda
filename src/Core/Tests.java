package Core;

import DataStructures.CSVLoader;
import MachineLearning.Classifier;
import MachineLearning.KMeansClusterer;
import MachineLearning.KnnClassifier;
import MachineLearning.LinearRegressor;
import MachineLearning.LogisticRegressor;
import MachineLearning.Trees.DecissionTree;
import Math.Matrix;
import Math.Vector;
import java.util.ArrayList;
import java.util.Random;


public class Tests {
    public static void diagonalization_test(){
        //double[][] data = {{0,1,4,1,2},{-1,-2,0,9,-1},{1,2,0,-6,1},{2,5,4,-10,4},{0,0,0,0,0}};
        //double[][] data = {{3,6,-8},{0,0,6},{0,0,2}};
        //double[][] data = {{1,-3,3},{3,-5,3},{6,-6,4}};
        double[][] data = {{1,2,0},{0,3,0},{2,-4,2}};   // Wikipedia diagonalization example
        //double[][] data = {{2,0,0},{0,3,4},{0,4,9}};
        //double[][] data = {{0,1},{-2,-3}};
        
        Matrix x = new Matrix(data);
        x.show("Original Matrix");
        //x.getInverse().show();
        
        x.getInverse().show("Inverse Matrix");
        x.getProduct(x.getInverse()).show("Product Matrix");
        
        Logger.log("------------- Test Diagonalization --------------------");
        Logger.indent();
        x.getCharacteristicPolynomial().show("Char poly");
        Vector eigenvalues = x.getCharacteristicPolynomial().getRoots();
        eigenvalues.show("Eigenvalues");
        
        ArrayList<Vector> results = x.getEigenVectors();
        for (Vector i:results) i.show("EigVec");
        
        Matrix D = Matrix.diag(eigenvalues);
        
        Matrix P = new Matrix(x.getRowCount(),x.getColumnCount());
        for (int i=0;i<P.getRowCount();i++)
            for (int j=0;j<P.getColumnCount();j++)
                P.set(i, j, results.get(j).get(i));

        
        D.show("Diagonal");
        P.show("P");
        P.getInverse().show("P^");
        Logger.log("---");
        
        P.getProduct(D).getProduct(P.getInverse()).show("PDP'");
        x.show("A");
        
        P.getInverse().getProduct(x).getProduct(P).show("P'AP");
        D.show("D");
        
        Logger.dedent();
        
    }
    
    public static void inversionAndMultiplication_test(){
        Logger.log("------------- Test Inversion & Multiplication --------------------");
        Logger.indent();
        
        Matrix a1 = new Matrix(new double[][] { // Wikipedia diagonalization example
            {1,2,0},
            {0,3,0},
            {2,-4,2}
        });
        
        Matrix p = new Matrix(new double[][]{
            {-1,     0,      -1  },
            {-1,     0,      0   },
            {2,      1,      2   }
        });
        Matrix p1 = new Matrix(new double[][]{
            {0,     -1,      0  },
            {2,      0,      1  },
            {-1,     1,      0  }
        });
        
        p.show("P");
        p.getInverse().show("P`");
        p1.show("P1");
        p1.getInverse().show("P1`");
        
        p1.getProduct(p).show("P1 * P");
        p.getInverse().getProduct(p).show("P` * P");
        
        //p1.getProduct(a1).getProduct(p).show("Result");
        Logger.dedent();
        
    }
    
    public static void massiveMatrixOperations_test(){
        // Unit Test
        
        Matrix a = Matrix.random(1000,1000);
        Logger.log("Centering a massive matrix");
        a.center();
        Logger.log("Scaling a massive matrix");
        a.scale();
        Logger.log("Done");
        
        Matrix b = Matrix.random(1000,1000);
        b.center();
        b.scale();
        
        Logger.log("Multiplying massive matrices");
        a.getProduct(b);
        Logger.log("Done");
        
    }
    
    public static void pickRowsColumns_test(){
        Matrix x = new Matrix(5,7);
        for (int i=0;i<x.getRowCount();i++)
            for (int j=0;j<x.getColumnCount();j++)
                x.set(i, j, i*10+j);
        x.show("Original Matrix");
        
        x.pickRows(1).show("Picked a row (1)");
        x.pickRows(1,3,4).show("Picked some rows (1,3,4)");
        x.pickRows(1,4,3).show("Picked some rows out of order (1,4,3)");
        x.pickRows(1,4,3,0,1,1,1).show("Picked rows multiple times");
        
        boolean[] idx1 = {true, false,true,true,false};
        x.pickRows(idx1).show("Picked some rows with boolean");
        
        x.pickColumns(1).show("Picked a Column (1)");
        x.pickColumns(1,3,4).show("Picked some Columns (1,3,4)");
        x.pickColumns(1,4,3).show("Picked some Columns out of order (1,4,3)");
        x.pickColumns(1,4,3,0,1,1,1).show("Picked Columns multiple times");
        
        boolean[] idx2 = {true, false,true,true,false,false,true};
        x.pickColumns(idx2).show("Picked some Columns with boolean");
        
    }
    
    public static void logisticRegressorTest(){
        String filename = "C:\\Users\\kostis\\Dropbox\\iris.vec.csv";
        Matrix dataset = CSVLoader.readCSV(filename,",",true);
        
        int N = dataset.getRowCount();
        Matrix[] dataset_tmp = CSVLoader.splitInputOutput(dataset,4,5,6);
        Matrix x_mat = dataset_tmp[0];
        Matrix y_mat = dataset_tmp[1];
        int M = x_mat.getColumnCount();
        int L=3;

        // Preprocess
        x_mat.center().standarize();
        y_mat.ground().scale();
        
        LogisticRegressor model = new LogisticRegressor(M,L);
        model.train(x_mat, y_mat);
        
        Matrix h_mat = model.classify(x_mat);
        
        model.getMeanSquaredError(x_mat, y_mat).show("MSE");
        model.show("model");
        
    }
    
    public static void linearRegressorTest(){
        
        //x_mat.show();
        int N = 10;
        int M = 3;
        int L = 3;
        Random rnd = new Random();
        double noise = 0.1;
        
        Matrix dataset_mat = new Matrix(N,M);
        Matrix target_mat = new Matrix(N,L);
        
        Matrix coeffs = Matrix.random(M,L);
        Vector biases = (Vector) Vector.random(L).center();
        
        for ( int i=0;i<N;i++){
            Vector pattern = new Vector(M);
            for (int m=0;m<M;m++)
                pattern.set(m, rnd.nextDouble()-0.5);
            dataset_mat.setRow(i, pattern);
            
            double noise_d = (rnd.nextDouble()-0.5)*noise;
            Vector target_vec = pattern.getAsRowMatrix().getProduct(coeffs).add(biases).getRow(0);
            target_mat.setRow(i, target_vec.add(noise_d));
        }
        
        LinearRegressor model = new LinearRegressor(M,L);
        model.train(dataset_mat, target_mat);
        
        Matrix z_mat = model.classify(dataset_mat);
        
        Classifier.R2(target_mat.getColumn(0), z_mat.getColumn(0)).show("R2 coeff");
        model.getMeanSquaredError(dataset_mat, target_mat).show("MSE");
        model.show("model");
        
    }
   
    public static void kmeansTest(){
        String filename = "C:\\Users\\kostis\\Dropbox\\iris.vec.csv";
        Matrix dataset = CSVLoader.readCSV(filename,",",true);
        
        int N = dataset.getRowCount();
        Matrix[] dataset_tmp = CSVLoader.splitInputOutput(dataset,4,5,6);
        Matrix x_mat = dataset_tmp[0];
        Matrix y_mat = dataset_tmp[1];
        int M = x_mat.getColumnCount();
        int L=3;
        int k=3;

        // Preprocess
        x_mat.center().standarize();
        y_mat.ground().scale();
        
        KMeansClusterer model = new KMeansClusterer(k,M);
        model.cluster(x_mat);
        
        model.show("Pre");
        Integer[] labels_intLstN = model.cluster(x_mat);
        model.show("Post");
        
        Vector labels_vec = new Vector(labels_intLstN);
        for (int label : labels_intLstN)
            Logger.log("label: "+label);
    }
    
    public static void knnTest(){
        String filename = "C:\\Users\\kostis\\Dropbox\\iris.vec.csv";
        Matrix dataset = CSVLoader.readCSV(filename,",",true);
        
        int N = dataset.getRowCount();
        Matrix[] dataset_tmp = CSVLoader.splitInputOutput(dataset,4,5,6);
        Matrix x_mat = dataset_tmp[0];
        Matrix y_mat = dataset_tmp[1];
        int M = x_mat.getColumnCount();
        int L=3;
        int k=3;

        // Preprocess
        x_mat.center().standarize();
        y_mat.ground().scale();
        
        KMeansClusterer model = new KMeansClusterer(k,M);
        model.cluster(x_mat);
        
        model.show("Pre");
        Integer[] labels_intLstN = model.cluster(x_mat);
        model.show("Post");
        
    
        KnnClassifier model2 = new KnnClassifier(x_mat,labels_intLstN, 5);
        Integer[] labels2_intLstN = model2.classify(x_mat);

        for (int label : labels2_intLstN)
            Logger.log("label: "+label);

    }
    
    public static void pcaTest(){
        String filename = "C:\\Users\\kostis\\Dropbox\\iris.vec.csv";
        Matrix dataset = CSVLoader.readCSV(filename,",",true);
        
        int N = dataset.getRowCount();
        Matrix[] dataset_tmp = CSVLoader.splitInputOutput(dataset,4,5,6);
        Matrix x_mat = dataset_tmp[0];
        Matrix y_mat = dataset_tmp[1];
        int M = x_mat.getColumnCount();
        int L=3;
        int k=3;

        // Preprocess
        x_mat.center().standarize();
        y_mat.ground().scale();
        
        Matrix cov_mat = x_mat.getCovariance();                
        
        x_mat.show("dataset");
        cov_mat.getEigenvalues().show("Eigenvalues");
    }
    
    public static void main(String[] args){

        DecissionTree.main(args);
    
    }

}
