package MachineLearning;

import Math.Vector;
import java.util.Random;
import java.util.ArrayList;

public abstract class Classifier {
    
    public abstract Vector classify(Vector pattern);
    //public abstract void train(Vector pattern, Vector target);
    public abstract int getInputCount();
    public abstract int getOutputCount();

    /*
    public void train(double[][] patterns, double[][] targets){
        assert patterns.length==targets.length: "In batch training, the patterns must be as much as the targets";
        for(int i=0;i<patterns.length;i++)
            train(patterns[i],targets[i]);
    }
    */
    /*
    public static void crossValidate(double[][] dataset, double[][] targetset){
        //scrap tis function, its an abomination
        assert dataset.length==targetset.length: String.format("Incompatible dataset v targets (%d, %d)",dataset.length,targetset.length);
        int batchSize=100*dataset.length;
        Random rnd = new Random();
        for (int theOne=dataset.length-1;theOne>=0;theOne--){
            //TODO: Make this create copies;
            MultiLayerNetwork net = new MultiLayerNetwork(5,5,1);
            
            double[][] patterns = new double[dataset.length-1][];
            double[][] targets = new double[targetset.length-1][];
            double[] onePattern = dataset[theOne];
            double[] oneTarget = targetset[theOne];
            int j=0;
            
            
            for (int i=0;i<dataset.length;i++){
                if (i==theOne)
                    continue;
                patterns[j]=dataset[i];
                targets[j]=targetset[i];
                //System.out.printf("%d <- %d\n",j,i);
                j++;
                
            }
            
            ArrayList errorLog = new ArrayList();
            double error=0;
            double prevError=1;
            double errMax = 0;
            while (Math.abs(error-prevError)>1.0/100){
                prevError=error;
                error=0;
                for (int i=0;i<batchSize;i++){
                    int idx = rnd.nextInt(patterns.length);
                    double[] pattern = patterns[idx];
                    double[] target = targets[idx];
                    //System.out.printf("%d %s )%d/%d)\n",idx,patterns[idx],i,patterns.length);
                    net.train(pattern, target);
                }
                
                for (int i=0;i<patterns.length;i++){
                    double[] output = net.classify(patterns[i]);
                    double[] errors = Core.arrayDiff(targets[i], output);
                    error+=Core.arrayNorm(errors);
                }
                error/=patterns.length;
                error=Math.sqrt(error);
                errorLog.add(error);
                if (error>errMax)
                    errMax=error;
                //System.out.printf("Error=%5.2f\t (%.5f)\n",error,Math.abs(error-prevError));
            }
            double[] y = new double[errorLog.size()];
            
            for(int i=0;i<y.length;i++)
                y[i]=((double)errorLog.get(i))/errMax;
            //Core.plot(y);
            double oneErr = net.classify(onePattern)[0]-oneTarget[0];
            System.out.printf("Cross-validated %3d/%3d (err: %.5f\t%.5f) %d\n", theOne, dataset.length,error,oneErr,oneTarget.length);
        }
    }

    public int[][] confusionMatrix(double[][] patterns, double[][] targets){
        int N=patterns.length;
        int L=0;
        double max=targets[0][L];
        for(int i=1;i<N;i++){
            if (max<targets[i][L])
                max=targets[i][L];
        }
        double[][] outputs=new double[(int)Math.round(max)][(int)Math.round(max)];
        int[][] matrix = new int[1][];
        for(int i=0;i<N;i++){
            outputs[i]=classify(patterns[i]);
            int x = (int)Math.round(outputs[i][L]);
            int y = (int)Math.round(targets[i][L]);
            matrix[x][y]++;
        }
        
        return matrix;
    }
*/
}
