/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import Core.Logger;
import java.util.ArrayList;

/**
 *
 * @author GeorgeKantasis
 */
public class Dictionary <K extends Comparable <K> ,V> {
    
    // TODO: Sort the array and utilize binary search
    
    private final ArrayList <KVPair<K,V>> _data;
    
    public Dictionary(){
        _data=new ArrayList ();       
    }
    
    public void set(K key, V value){
        // TODO: This is a second implementation of binary search, use only one
        KVPair<K,V> nuevo = new KVPair(key,value);

        int index=-1;
        int start = 0;
        int end = this.getLength()-1;
        
        if (this.getLength()==0){
            _data.add(nuevo);
            return;
        }
        
        int temp = key.compareTo(this.getPair(start).getKey());
        if (temp<0){
            _data.add(0,nuevo);
            return;
        }else if (temp==0){
            _data.get(start).setValue(value);
            return;
        }
        
        temp = key.compareTo(this.getPair(end).getKey());
        if (temp>0){
            _data.add(nuevo);
            return;
        }else if (temp==0){
            _data.get(end).setValue(value);
            return;
        }

        while (start<=end){
            index=(start+end) / 2;
            temp = key.compareTo(this.getPair(index).getKey());
            if (temp>0){
                start=index+1;
            }else if (temp<0){
                end=index-1;
            }else{
                _data.get(index).setValue(value);
                return;
            }
        }
        _data.add(end,nuevo);
    }
    
    public V get(K key){
        int idx = indexOf(key);
        return getPair(idx).getValue();
    }
    
    public KVPair<K,V> getPair(int i){
        return _data.get(i);
    }
    
    public int indexOf(K key){
        int index=-1;
        int start = 0;
        int end = this.getLength()-1;
        
        if (this.getLength()==0){
            return -1;
        }
        
        int temp = key.compareTo(this.getPair(start).getKey());
        if (temp<0){
            return -1;
        }else if (temp==0){
            return start;
        }
        
        temp = key.compareTo(this.getPair(end).getKey());
        if (temp>0){
            return -1;
        }else if (temp==0){
            return end;
        }

        while (start <= end){
            
            index=(start+end) / 2;
            temp = key.compareTo(this.getPair(index).getKey());
            if (temp>0){
                start=index+1;
            }else if (temp<0){
                end=index-1;
            }else{
                return index;
            }
        }

        return -1;
    }
    
    public int getLength(){
        return _data.size();
    }
    
    public void remove(K key){
        int idx = indexOf(key);
        removeIdx(idx);
    }
    
    public void removeIdx(int i){
        if (i!=-1){
            _data.remove(i);   
        }
    }
    
    public void clear(){
        _data.clear();
    }
    
    public ArrayList <K> getKeys(){
        ArrayList<K> result = new ArrayList();
        _data.forEach((pair) -> {
            result.add( pair.getKey() );
        });
        return result;
    }

    public class KVPair<K extends Comparable<K>,V> implements Comparable<KVPair<K,V>>{
        private K _key;
        private V _value;
        
        public KVPair(K k, V v){
            _key=k;
            _value=v;
        }
        
        public K getKey(){
            return _key;
        }
        
        public V getValue(){
            return _value;
        }
        
        public void setKey(K k){
            _key=k;
        }
        
        public void setValue(V v){
            _value=v;
        }
        
        public int compareTo(KVPair<K,V> that){
            K temp = that.getKey();
            return this.getKey().compareTo(temp);
        }
        
        public final String toString(){
            return String.format("<%s> -> <%s>",getKey().toString(),getValue().toString());
        }
        
    }
    
    
    public void show(){
        for (KVPair pair : _data){
            Logger.log(pair.toString());
        }
    }
    
    public static void main(String[] args){

    }
}
