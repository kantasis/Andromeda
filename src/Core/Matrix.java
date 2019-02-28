package Core;

public class Matrix {
    private final Vector[] _data;
  
    public Matrix(int rows,int columns){
        _data=new Vector[columns];
        for (int i=0;i<_data.length;i++)
            _data[i]=new Vector(rows);
    }
    
    public Matrix(double[][] data){
        _data=new Vector[data.length];
        _data[0] = new Vector(data[0]);
        for(int i=1;i<data.length;i++){
            assert data[i-1].length==data[i].length : String.format("Can't create matrix out of array. Inconsistent dimensions");
            _data[i] = new Vector(data[i]);
        }
    }
        
    public final int getN(){
        return _data[0].getLength();
    }
    
    public final int getM(){
        return _data.length;
    }
    
    public final int getColumns(){
        return _data.length;
    }
    
    public final int getRows(){
        return _data[0].getLength();
    }
    
    public final double get(int i, int j){
        assertIndexBound(i,j);
        return _data[j].get(i);
    }
    
    public final void set(int i, int j, double v){
        assertIndexBound(i,j);
        _data[j].set(i, v);
    }
    
    public void setColumn(int i, Vector v){
        assertIndexBound(0,i);
        assert v.getLength()==this.getRows() : 
                String.format("The new Vector does not fit ( %d / %d )",v.getLength(),this.getRows());
        _data[i]=v;
    }
    
    public Vector getColumn(int i){
        assertIndexBound(0,i);
        return _data[i];
    }
    
    public void show(){
        System.out.printf("[%d %d]\n",getRows(),getColumns());
        for (int i=0;i<getRows();i++){
            for (int j=0;j<getColumns();j++)
                System.out.printf("%5.2f ",get(i,j));
            System.out.printf("\n");
        }
//        for (Vector v : _data )
//          v.show();
    }
    
    public Matrix _add(double this_v, Matrix that, double that_v){
        assertSizeAlignment(that);
        for (int i=0;i<getColumns();i++){
            getColumn(i)._add(this_v, that.getColumn(i), that_v);
        }
        return this;
    }
    
    public Matrix _times(double factor){
        for (int i=0;i<getColumns();i++){
            getColumn(i)._times(factor);
        }
        return this;
    }
    
    public Matrix add(double this_v, Matrix that, double that_v){
        Matrix result = new Matrix(this.getN(),this.getM());
        return result._add(this_v, that, that_v);
    }
    
    public Matrix _add(Vector value){
        for (int i=0;i<getColumns();i++){
            getColumn(i)._add(value.get(i));
        }
        return this;
    }
    
    public Matrix _add(double value){
        for (int i=0;i<getColumns();i++){
            getColumn(i)._add(value);
        }
        return this;
    }
    
    public Vector sum(){
        Vector result = new Vector(this.getColumns());
        for (int i=0;i<getColumns();i++){
            result.set(i, _data[i].sum());
        }
        return result;
    }
    
    public Vector average(){
        return this.sum().times(1f/this.getN());
    }
        
    public static void main(String[] args){
        double[][] data = {{3,2,-1},{2,-2,4},{-1,0.5,-1}};
        //double[][] data = {{1,2,3},{2,4,5},{0,0,8},{0,8,0}};
        Matrix neo = new Matrix(data).transpose();
        double[][] data2 = {{1f,-2f,0f}};
        Matrix res=new Matrix(data2);
        Matrix comp=neo._merge(eye(3));
        
        comp.show();
        comp._ltrig()._utrig().show();
        //comp._utrig()._ltrig().show();
        
        //neo.adj(0,0).show();
        //System.out.println(neo._utrig()._ltrig().diag().product());
    }
    
    public void assertSizeAlignment(Matrix that){
        assert  (! this.getSize().equals( that.getSize()) ): String.format("Incompatible Matrices %s / %s",this.getSize(),that.getSize());
    }
    
    public boolean equals(Matrix that){
        for(int i=0;i<this.getColumns();i++)
            if (!this.getColumn(i).equals(that.getColumn(i)))
                return false;
        return true;
    }
    
    public Vector getSize(){
        return new Vector((double) this.getRows(), (double) this.getColumns());
    }
    
    public void assertIndexBound(int i,int j){
        assert i<getRows() : String.format("Trying to set elements outside the Matrix ( %d / %d )",i,getRows());
        assert i>=0 : String.format("Trying to set elements outside the Vector ( %d / %d )",i,getRows());
        assert j<getColumns() : String.format("Trying to set elements outside the Matrix ( %d / %d )",i,getColumns());
        assert j>=0 : String.format("Trying to set elements outside the Vector ( %d / %d )",i,getColumns());
    }
    
    public Matrix multiply(Matrix that){
        assert this.getM()==that.getN() : String.format("Incompatible matrices to multiply ( %s / %s )",this.getSize(),that.getSize());
        Matrix result = new Matrix(this.getN(),that.getM());
        for (int i=0;i<result.getN();i++)
            for (int j=0;j<result.getM();j++){
                double sum=0;
                for (int k=0;k<this.getM();k++)
                    sum+=this.get(i,k)*that.get(k,j);
                result.set(i,j,sum);
            }
        return result;
    }
    
    public Matrix transpose(){
        Matrix result = new Matrix(this.getM(),this.getN());
        for (int i=0;i<result.getN();i++)
            for (int j=0;j<result.getM();j++)
                result.set(i, j, this.get(j,i));
        return result;   
    }
    
    public Matrix adj(int x,int y){
        assert this.getM()>1 && this.getN()>1 : String.format("Cannot reduce matrix %s any more",this.getSize());
        
        Matrix result = new Matrix(this.getM()-1,this.getN()-1);
        int o=0;
        for (int i=0;i<result.getN();i++){
            if (o==x)
                o++;
            int k=0;
            for (int j=0;j<result.getM();j++){
                if (k==y)
                    k++;
                result.set(i, j, this.get(o,k));
                k++;
            }
            o++;
        }
        return result;   
    }
    public double det(){
        assert this.getM()==this.getN() : String.format("Undefined det for non-square matrix (%s)",this.getSize());
        System.out.printf("Deternining\n");
        this.show();
        System.out.printf("/Deternining\n");
        
        double result=0;
        if (this.getM()==1)
            return this.get(0,0);
        
        double sign=1;
        for (int i=0;i<this.getN();i++){
            result+= sign*this.get(i,0)*this.adj(i,0).det();
            sign*=-1;
        }
        return result;
    }
    
    public Matrix copy(){
        Matrix result = new Matrix(this.getN(),this.getM());
        for (int i=0;i<result.getN();i++)
            for (int j=0;j<result.getM();j++)
                result.set(i,j,this.get(i,j));
        return result;
    }
    
    public Matrix cov(){
        Matrix result = new Matrix(this.getM(),this.getM());
        Matrix data = this.copy();
        Vector neg_average = data.average()._times(-1);
        data._add(neg_average);
        result = data.transpose().multiply(data)._times(1f/getN());
        return result;
    }
    
    public Matrix _merge(Matrix that){
        assert this.getRows()==that.getRows(): String.format("Cannot merge matrices with different row counts %s / %s",this.getSize(),that.getSize());
        Matrix result = new Matrix(this.getRows(),this.getColumns()+that.getColumns());
        int idx=0;
        for(int i=0;i<this.getColumns();i++)
            result.setColumn(idx++, this.getColumn(i));
        for(int i=0;i<that.getColumns();i++)
            result.setColumn(idx++, that.getColumn(i));
        return result;
    }
    
    public Matrix _getColumns(int...x){
        Matrix result = new Matrix(this.getRows(),x.length);
        int idx=0;
        for(int i=0;i<x.length;i++)
            result.setColumn(idx++, this.getColumn(x[i]));
        return result;
    }
    
    public Matrix _getFirstColumns(int x){
        int[] idc = new int[x];
        for (int i=0;i<x;i++)
            idc[i]=i;
        return _getColumns(idc);
    }
    
    public Matrix _getLastColumns(int x){
        int[] idc = new int[x];
        for (int i=0;i<x;i++)
            idc[x-1-i]=this.getColumns()-i-1;
        return _getColumns(idc);
    }
    
    public static Matrix zeros(int x,int y){
        return new Matrix(x,y);
    }
    
    public static Matrix ones(int x,int y){
        return new Matrix(x,y)._add(1);
    }
    
    public static Matrix diag(double...vals){
        Matrix result = new Matrix(vals.length,vals.length);
        for (int i=0;i<vals.length;i++)
            result.set(i,i,vals[i]);
        return result;
    }
    
    public static Matrix diag(Vector vals){
        return diag(vals.toArray());
    }
    
    public Vector diag(){
        int x = this.getRows();
        if (x<this.getColumns())
            x=this.getColumns();
        Vector result = new Vector(x);
        for (int i=0;i<x;i++)
            result.set(i,this.get(i,i));
        return result;
    }
    
    public static Matrix eye(int x){
        return diag(new Vector(x)._add(1f));
    }
    
    public Matrix _rowOperation(int lhs, double l_factor, int rhs, double r_factor){
        for (int i=0;i<this.getColumns();i++)
            this.set(lhs,i,this.get(lhs,i)*l_factor+this.get(rhs,i)*r_factor);
        return this;
    }
    
    public Matrix _utrig(){
        for (int row=0; row<this.getRows();row++){
            int col=0;            
            while (this.get(row,col)==0){
                boolean found=false;
                for (int row2=row+1;row2<this.getRows();row2++){
                    if (this.get(row2,col)!=0){
                        for (int i=0;i<this.getColumns();i++){
                            //System.out.println("Swapping");
                            double temp=this.get(row2,i);
                            this.set(row2,i,this.get(row,i));
                            this.set(row,i,temp);
                        }
                        found=true;
                        break;
                    }
                }
                if(found)
                    break;
                col++;
                if (col>=this.getColumns())
                    return this;
            }
            this._rowOperation(row, 1f/this.get(row,col), row, 0f);
            for (int i=row+1;i<this.getRows();i++)
                this._rowOperation(i, 1f, row, -this.get(i,col));
        }
        return this;
    }

    public Matrix _ltrig(){
        for (int row=this.getRows()-1; row>=0;row--){
            int col=this.getRows()-1;
            while (this.get(row,col)==0){
                boolean found=false;
                for (int row2=row-1;row2>=0;row2--){
                    if (this.get(row2,col)!=0){
                        for (int i=0;i<this.getColumns();i++){
                            double temp=this.get(row2,i);
                            this.set(row2,i,this.get(row,i));
                            this.set(row,i,temp);
                        }
                        found=true;
                        break;
                    }
                }
                if(found)
                    break;
                col--;
                if (col>=this.getColumns())
                    return this;
            }
            this._rowOperation(row, 1f/this.get(row,col), row, 0f);
            for (int i=row-1;i>=0;i--)
                this._rowOperation(i, 1, row, -this.get(i,col));
        }
        return this;
    }
    
}
