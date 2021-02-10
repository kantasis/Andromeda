package MachineLearning;

import Core.Logger;
import Math.Matrix;
import Math.Operatables.Real;
import Math.Vector;
import java.util.Random;

public class KMeansClusterer {
    
    private Matrix _centroids;
    public static int MAX_ITERATIONS=300;

    public KMeansClusterer(Matrix centroids){
        _centroids = centroids;
    }

    public KMeansClusterer(int k, int m){
        _centroids = Matrix.random(k, m);
    }

    public KMeansClusterer(int k, Matrix dataset){
        this(k,dataset.getRowCount());
        Random rnd = new Random();
        Integer[] classes = new Integer[dataset.getRowCount()];
        for (int i=0;i<classes.length;i++)
            classes[i]=rnd.nextInt(k);
        _updateCentroids(dataset, classes);
    }

    public int getK(){
        return _centroids.getRowCount();
    }

    public int getFeatures(){
        return _centroids.getColumnCount();
    }
    
    public Matrix getCentroids(){
        return _centroids.copy();
    }
    
    public void assertFeatureAlignment(Matrix dataset){
        assert dataset.getColumnCount()==this.getFeatures() : 
            String.format("The dataset should have %d columns, not %d",
            this.getFeatures(), dataset.getColumnCount());
    }
    
    public Integer[] classify(Matrix dataset){
        assertFeatureAlignment(dataset);
        Integer[] result = new Integer[dataset.getRowCount()];
        for (int i=0;i<dataset.getRowCount();i++){
            Vector pattern = dataset.getRow(i);
            int minArg = 0;
            Real minDistance = pattern.getDiff(_centroids.getRow(minArg)).getNorm();
            
            for (int j=1;j<this.getK();j++){
                Vector centroid = _centroids.getRow(j);
                Real dist = pattern.getDiff(centroid).getNorm();
                if (dist.isLess(minDistance)){
                    minDistance=dist;
                    minArg=j;
                }
            }
            result[i]=minArg;
        }
        return result;
    }
    
    private void _updateCentroids(Matrix dataset, Integer[] classes){
        int[] classCounts = new int[getK()];
        for (int i=0;i<classes.length;i++)
            classCounts[classes[i]]++;

        for(int k=0;k<getK();k++){
            Matrix temp = new Matrix(classCounts[k], getFeatures());
            int tempRow_idx=0;
            for(int row=0;row<dataset.getRowCount();row++){
                if (classes[row]!=k)
                    continue;
                for(int col=0;col<dataset.getColumnCount();col++)
                    temp.setRow(tempRow_idx, dataset.getRow(row));
                tempRow_idx++;
            }
            _centroids.setRow(k, temp.getAverageVector());
        }
    }
    
    public Integer[] cluster(Matrix dataset){
        assertFeatureAlignment(dataset);
        Real e;
        int iteration_counter = 0;
        Integer[] classes;
        do{
            Matrix temp = _centroids.copy();
            classes = classify(dataset);
            _updateCentroids(dataset,classes);
            temp.diff(_centroids);
            e = temp.getNormVector().getNorm();
            iteration_counter++;
            if (iteration_counter > MAX_ITERATIONS)
                break;
        }while ( !e.isZero() );
        return classes;
    }
    
    public void show(String str){
        _centroids.show(str);
    }
    
    public static void main(String[] args){
        
        Matrix dataset = new Matrix(150,3);
        
        for (int i=0;i<100;i++)
            dataset.setRow(i, dataset.getRow(i).add(10));
        
        int N=dataset.getRowCount();
        int M=dataset.getColumnCount();
        int K=2;
        
        dataset.add(Matrix.random(N, M));
        
        KMeansClusterer kmeans = new KMeansClusterer(K,M);
        kmeans.show("Pre");
        kmeans.cluster(dataset);
        kmeans.show("Post");
        

    }
    
}

