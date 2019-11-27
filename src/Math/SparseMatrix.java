/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math;

import java.util.ArrayList;

/**
 *
 * @author GeorgeKantasis
 */
public class SparseMatrix extends Matrix {
    
    private DataStructures.Dictionary<Key,Double> _data;
    private double _defaultValue;
    private int _rows,_columns;
    
    public SparseMatrix(int rows, int columns){
        super(0,0);
        _data = new DataStructures.Dictionary();
        _defaultValue=0;
        _rows=rows;
        _columns=columns;
    }
    
    public int getM(){
        return _columns;
    }
    
    public int getN(){
        return _rows;
    }
    
    public double getDefaultValue(){
        return _defaultValue;
    }
    
    public void setDefaultValue(double x){
        _defaultValue=x;
    }
    
    public double get(int i,int j){
        int idx = _data.indexOf(new Key(i,j));
        if (idx==-1)
            return getDefaultValue();
        return _data.getPair(idx).getValue();
    }
    
    public Matrix set(int i,int j, double value){
        if (value==getDefaultValue()){
            int idx = _data.indexOf(new Key(i,j));
            _data.removeIdx(idx);
        }else{
            _data.set(new Key(i,j), value);
        }
        return this;
    }
    
    
    public int getPopcount(){
        return _data.getLength();
    }
    
    public int getNobcount(){
        return getColumns()*getRows() - getPopcount();
    }
    
    public double getFillPct(){
        return getPopcount()/(getColumns()*getRows());
    }

    
    public ArrayList <Key> getKeys(){
        return _data.getKeys();
    }
    
    public ArrayList <Key> getKeysUnion(SparseMatrix that){
        // TODO: Utilize the 4 cases
        ArrayList<Key> theseKeys = this.getKeys();
        ArrayList<Key> thoseKeys = that.getKeys();
        for (Key thatKey : thoseKeys){
            int idx = this._data.indexOf(thatKey);
            if (idx==-1){
                theseKeys.add(thatKey);
            }
        }
        return theseKeys;
    }
    
    public SparseMatrix add(double value){
        ArrayList<Key> keys = this._data.getKeys();
        for (Key key : keys){
            int i = key.getI();
            int j = key.getJ();
            this.set(i, j, this.get(i, j)+value);
        }
        setDefaultValue( getDefaultValue()+value);
        
        return this;
    }

    public SparseMatrix _times(double value){
        ArrayList<Key> keys = this._data.getKeys();
        for (Key key : keys){
            int i = key.getI();
            int j = key.getJ();
            this.set(i, j, this.get(i, j)*value);
        }
        setDefaultValue( getDefaultValue()*value);
        
        return this;
    }
    
    public SparseMatrix _add(double this_v, SparseMatrix that, double that_v){
        // TODO: assertSizeAlignment(that);
        
        ArrayList<Key> keys = this.getKeysUnion(that);
        
        for (Key key : keys){
            int i = key.getI();
            int j = key.getJ();
            this.set(i, j, this.get(i, j)*this_v+that.get(i, j)*that_v);
        }
        
        setDefaultValue( this.getDefaultValue()*this_v + that.getDefaultValue()*that_v);
        return this;
    }
    
    public SparseVector getRow(int row){
        // Complexity Notation:
        // X: The entire search space
        // N: The populated search space
        
        // TODO: This can be optimized with binarysearch
        // binary search [logN] for row, fall into the middle of records with row
        // and then move up and down [sqrt(N)] to gather the values
        
        // Right now complexity is:
        // [N] for the getKeys()
        // [N] for the iteration
        
        ArrayList<Key> keys = getKeys();
        SparseVector result = new SparseVector(this.getColumns());
        for (int idx=0;idx<keys.size();idx++){
            if (keys.get(idx).getI()==row){
                int col = keys.get(idx).getJ();
                result.set(col, this.get(row,col));
            }
        }
        return result;
    }
    
    public SparseVector getColumn(int col){        
        // TODO: This can be optimized with binarysearch
        // binary search [logN] for col, fall into the middle of records with row
        // and then move up and down [sqrt(N)] to gather the values
        
        ArrayList<Key> keys = getKeys();
        SparseVector result = new SparseVector(this.getColumns());
        for (int idx=0;idx<keys.size();idx++){
            if (keys.get(idx).getI()==col){
                int row = keys.get(idx).getJ();
                result.set(row, this.get(row,col));
            }
        }
        return result;
    }

    
    /*
    _times(SparseMatrix);
    */       
    
    
    public class Key implements Comparable<Key>{
        
        private Integer _i, _j;
        
        public Key(int i,int j){
            setI(i);
            setJ(j);
        }
        
        public final void setI(int i){
            _i=i;
        }

        public final void setJ(int j){
            _j=j;
        }
        
        public final Integer getI(){
            return _i;
        }
        
        public final Integer getJ(){
            return _j;
        }
        
        public int compareTo(Key that){
            int result = getI().compareTo(that.getI());
            if (result==0)
                return getJ().compareTo(that.getJ());
            else
                return result;
        }
        
        public String toString(){
            return String.format("<%3d,%3d>", getI(), getJ());
        }
    }
    
    public static void main(String... args){
        SparseMatrix matrix = new SparseMatrix(4,4);
        for(int i=0;i<4;i++){
            for (int j=0;j<4;j++){
                matrix.set(i, j, i+j*10);
            }
        }
        matrix.show();
        //matrix._data.show();
    }
    
}
