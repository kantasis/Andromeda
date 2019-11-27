/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning;

import Math.Vector;

/**
 *
 * @author kostis
 */
public abstract class FeatureExtractor <T>{
    
    public abstract Vector data2pattern(T data);
    
    public abstract T pattern2data(Vector pattern);
    
    public Vector[] data2pattern (T[] data_list){
      Vector[] result = new Vector[data_list.length];
      for (int i=0;i<data_list.length;i++)
         result[i]=data2pattern(data_list[i]);
      return result;
    }
    
}
