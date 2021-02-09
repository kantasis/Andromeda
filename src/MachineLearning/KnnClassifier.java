package MachineLearning;

import Core.Logger;
import Math.Matrix;
import Math.Operatables.Real;
import Math.Vector;

public class KnnClassifier {
    
    private Matrix _originalDataset_LxM;
    private Integer[] _originalClasses_L;
    private int _k;
    
    public KnnClassifier(Matrix originalDataset, Integer[] classes, int k){
        assert originalDataset.getRowCount()==classes.length: 
            String.format("originalDataset(%d) and classes(%d) do not have the same dimensions",
            originalDataset.getRowCount(), classes.length);
        
        _originalDataset_LxM = originalDataset;
        _originalClasses_L = classes;
        _k=k;
    }
    
    public Integer[] classify(Matrix dataset_NxM){
        assert _originalDataset_LxM.getColumnCount()==dataset_NxM.getColumnCount(): 
            String.format("originalDataset(%d) and dataset(%d) do not have the same column count",
            _originalDataset_LxM.getColumnCount(), dataset_NxM.getColumnCount());
        
        Integer[] classes_N = new Integer[dataset_NxM.getRowCount()];
        for(int row=0;row<dataset_NxM.getRowCount();row++){
            Vector pattern = dataset_NxM.getRow(row);
            Vector distances_K = new Vector(_k);
            Integer[] distances_idx = new Integer[_k];
                    
            // Initialize
            int origRow=0;
            Vector centroid = _originalDataset_LxM.getRow(origRow);
            Real distance = pattern.getDiff(centroid).getNorm();
            
            for (int i=0;i<distances_K.getLength();i++){
                distances_K.set(i, distance);
                distances_idx[i]=origRow;
            }
            
            for(origRow=1;origRow<_originalDataset_LxM.getRowCount();origRow++){
                centroid = _originalDataset_LxM.getRow(origRow);
                distance = pattern.getDiff(centroid).getNorm();
                int distance_idx=origRow;
                
                for (int i=0;i<distances_K.getLength();i++){
                    if (distance.isLess(distances_K.get(i))){
                        Real tempDistance = distance;
                        distance=distances_K.get(i);
                        distances_K.set(i, tempDistance);
                        
                        int tempIdx=distance_idx;
                        distance_idx=distances_idx[i];
                        distances_idx[i]=tempIdx;
                    }
                }
            }
            
            int[] votes_count = new int[_originalDataset_LxM.getRowCount()];
            for (int i=0;i<distances_K.getLength();i++){
                int iThNearest_idx = distances_idx[i];
                int iThClass = _originalClasses_L[iThNearest_idx];
                votes_count[iThClass]++;
            }
            
            int argMax = 0;
            int max = votes_count[argMax];
            for (int i=1;i<votes_count.length;i++)
                if (votes_count[i]>max){
                    max=votes_count[i];
                    argMax=i;
                }
            classes_N[row]=argMax;
        }
        return classes_N;
    }
    
    public static void main(String[] args){
        
        int N=100;
        int M=3;
        int K=2;
        
        Matrix dataset = new Matrix(N,M);
        
        for (int i=0;i<N/2;i++)
            dataset.setRow(i, dataset.getRow(i).add(10));
        dataset.add(Matrix.random(N, M));
        KMeansClusterer kmeans = new KMeansClusterer(K,M);
        Integer[] classes = kmeans.cluster(dataset);
        
        Matrix pattern = new Matrix(new double[][]{
            {10,10,10},
            {0,0,0},
            {1,1,1},
        });
        
        KnnClassifier knn = new KnnClassifier(dataset,classes,3);
        Integer[] result = knn.classify(pattern);
        Logger.log("Result: %d",result[0]);

    }
    
}
