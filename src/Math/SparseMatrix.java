/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math;

import Core.Logger;
import Math.Operatables.Real;
import java.util.ArrayList;

/**
 *
 * @author GeorgeKantasis
 */
public class SparseMatrix extends Matrix {
//    TODO: Figure out if this should extend a matrix or a generic matrix
    
    private DataStructures.Dictionary<Key,Real> _data;
    private Real _defaultValue;
    private final int _rows,_columns;
    
    public SparseMatrix(int rows, int columns){
        super(0,0);
        _data = new DataStructures.Dictionary();
        _defaultValue=Real.zero();
        _rows=rows;
        _columns=columns;
    }
    
    public int getM(){
        return _columns;
    }
    
    public int getN(){
        return _rows;
    }
    
    public Real getDefaultValue(){
        return _defaultValue;
    }
    
    public SparseMatrix setDefaultValue(Real x){
        _defaultValue=x;
        return this;
    }
    
    public Real get(int i,int j){
        int idx = _data.indexOf(new Key(i,j));
        if (idx==-1)
            return getDefaultValue();
        return _data.getPair(idx).getValue();
    }
    
    public SparseMatrix set(int i,int j, Real value){
        Key key = new Key(i,j);
        if (value==getDefaultValue()){
            int idx = _data.indexOf(key);
            _data.removeIdx(idx);
        }else{
            _data.set(key, value);
        }
        return this;
    }
    
    
    public int getPopcount(){
        return _data.getLength();
    }
    
    public int getNobcount(){
        return getColumnCount()*getRowCount() - getPopcount();
    }
    
    public double getFillPct(){
        return getPopcount()/(getColumnCount()*getRowCount());
    }

    
    public ArrayList <Key> getKeys(){
        return _data.getKeys();
    }
    
    public ArrayList <Key> getKeysUnion(SparseMatrix that){
        ArrayList<Key> result = this.getKeys();
        for (Key thatKey :  that.getKeys()){
            int idx = this._data.indexOf(thatKey);
            if (idx==-1){
                result.add(thatKey);
            }
        }
        return result;
    }
    
    public SparseMatrix add(Real value){
        for (Key key : getKeys()){
            int i = key.getI();
            int j = key.getJ();
            this.get(i, j).add(value);
        }
        _defaultValue.add(value);
        return this;
    }

    public SparseMatrix multiply(Real value){
        for (Key key : getKeys()){
            int i = key.getI();
            int j = key.getJ();
            this.get(i, j).multiply(value);
        }
        _defaultValue.multiply(value);
        return this;
    }
    
    public SparseMatrix weightedSum(Real this_v, SparseMatrix that, Real that_v){
        assertSizeAlignment(that);
        
        ArrayList<Key> keys = this.getKeysUnion(that);
        
        for (Key key : keys){
            int i = key.getI();
            int j = key.getJ();
            this.get(i, j).multiply(this_v).add(that.get(i, j).getMultiply(that_v));
        }
        this.getDefaultValue().multiply(this_v).add(that.getDefaultValue().getMultiply(that_v));
        return this;
    }
/*    
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
*/

/*    
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
*/    
    public Matrix getMatrix(){
        if(this.getFillPct()>.5)
            Logger.log(Logger.LL_WARNING,"Warning: Turning a SparseMatrix with fill percentage %5.2f%% into a matrix",this.getFillPct()*100);
        Matrix result = new Matrix(this.getRowCount(),this.getColumnCount());
        for(int i=0; i<result.getRowCount();i++)
            for (int j=0;j<result.getColumnCount();j++)
                result.set(i, j, this.get(i, j));
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
