/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

/**
 *
 * @author GeorgeKantasis
 */
public class Datum <T> implements Comparable{
        private T _value;
        private Comparable _key;
        
        public Datum(Comparable x, T z){
            _key=x;
            _value=z;
        }
        
        public T getValue(){
            return _value;
        }
        
        public Comparable getKey(){
            return _key;
        }
        
        public void setValue(T val){
            _value=val;
        }
        
        public int compareTo(Object that){
            int res = getKey().compareTo(((Datum) that).getKey());
            if (res!=0){
                return res;
            }else
                return getKey().compareTo(((Datum) that).getKey());
        }
    }
