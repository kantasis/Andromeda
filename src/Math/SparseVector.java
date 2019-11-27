/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math;

import DataStructures.Dictionary;
import java.util.ArrayList;


/**
 *
 * @author GeorgeKantasis
 */
public class SparseVector extends Vector {
    private double _defaultValue;
    private int _length;
    private Dictionary <Integer,Double> _data;
    
    public SparseVector(int x){
        super(1);
        _defaultValue=0;
        _length=x;
        _data=new Dictionary();
    }
    
    public int getPopcount(){
        return _data.getLength();
    }
    
    public int getLength(){
        return _length;
    }
    
    public int getNobcount(){
        return getLength() - getPopcount();
    }
    
    public double getFillPct(){
        return getPopcount()/getLength();
    }
    
    public void set(int i, double value){
        // TODO: Remove pair if v==default
        // Comment 06Apr19: Why?
        if (value==_defaultValue){
            int idx = _data.indexOf(i);
            _data.removeIdx(idx);
        }else{
            _data.set(i, value);
        }
    }
    
    public double get(int i){
        int searchResult = _data.indexOf(i);
        if (searchResult==-1){
            return _defaultValue;
        }else
            return _data.getPair(searchResult).getValue();
    }
    
    public double getDefaultValue(){
        return _defaultValue;
    }
    
    public void setDefaultValue(double v){
        _defaultValue=v;
    }
    
    public ArrayList <Integer> getKeys(){
        return _data.getKeys();
    }
    
    public ArrayList <Integer> getKeysUnion(SparseVector that){
        // TODO: Utilize the 4 cases
        ArrayList<Integer> theseKeys = this.getKeys();
        ArrayList<Integer> thoseKeys = that.getKeys();
        for (Integer thatKey : thoseKeys){
            int idx = this._data.indexOf(thatKey);
            if (idx==-1){
                theseKeys.add(thatKey);
            }
        }
        return theseKeys;
    }
    
    public SparseVector _add(double this_v, SparseVector that, double that_v){
        assertSizeAlignment(that);
        ArrayList<Integer> keys = this.getKeysUnion(that);
        
        for (Integer key : keys){
            this.set(key, this.get(key)*this_v+that.get(key)*that_v);
        }
        
        setDefaultValue( this.getDefaultValue()*this_v + that.getDefaultValue()*that_v);
        return this;
    }
    
    public Vector add(double value){
        ArrayList<Integer> keys = this._data.getKeys();
        for (Integer key : keys){
            this.set(key, this.get(key)+value);
        }
        setDefaultValue( getDefaultValue()+value);
        return this;
    }
    
    public Vector times(double factor){
        ArrayList<Integer> keys = this._data.getKeys();
        for (Integer key : keys){
            this.set(key, this.get(key)*factor);
        }
        setDefaultValue( getDefaultValue()*factor);
        return this;
    }
    
    public double dot(SparseVector that){
        assertSizeAlignment(that);
        double result = 0;
        ArrayList<Integer> keys = this.getKeysUnion(that);
        
        for (Integer key : keys){
            result+=this.get(key)*that.get(key);
        }
        result+=this.getDefaultValue()*that.getDefaultValue()*(this.getLength()-keys.size());
        return result;
    }    
    
    public static void main(String[] args){
        //SparseVector x = new SparseVector(100);
        //SparseVector y = new SparseVector(100);
        
        SparseVector x = new SparseVector(30);
        SparseVector y = new SparseVector(30);
        
        y.set(2,50);
        //System.out.println(x.getLength());
        for (int i=0;i<5;i++)
            x._add(1, y, 1);
        
        
        
        x.show();
        // Vectors: 1.50
        // SparseVectors: 
    }
    
    
}
