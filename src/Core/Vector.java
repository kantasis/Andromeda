package Core;

/**
 *
 * @author GeorgeKantasis
 */
public class Vector {
    
    // TODO: toArray
    // TODO: conv
    // TODO: Solve Polynomials
    // TODO: Find Eigenvalues
    
    
    
    private final double[] _data;
    
    public Vector(int n){
        assert n>0 : String.format("Cannot Initialize an empty Vector");
        _data=new double[n];
    }
    
    public Vector(double...x){
        this(x.length);
        for (int i=0;i<this.getLength();i++)
            this.set(i,x[i]);
    }
    
    public String toString(){
        String result=String.format("[ %.2f",this.get(0));
        for (int i=1;i<this.getLength();i++){
            result+=String.format(" ,%.2f",this.get(i));
        }
        return result+" ]";
    }
    
    public void show(){
        System.out.println(this.toString());
    }
    
    public int getLength(){
        return _data.length;
    }
    
    public double get(int i){
        assert i<getLength() : String.format("Trying to get elements outside the Vector ( %d / %d )",i,getLength());
        return _data[i];
    }
    
    public void set(int i, double v){
        assert i<getLength() : String.format("Trying to set elements outside the Vector ( %d / %d )",i,getLength());
        _data[i]=v;
    } 
    
    public double dot(Vector that){
        assertSizeAlignment(that);
        double result = 0;
        for (int i=0;i<this.getLength();i++)
            result+=this.get(i)*that.get(i);
        return result;
    }
    
    public double min(){
        double result=get(0);
        for (int i=1;i<getLength();i++)
            if (get(i)<result)
                result=get(i);
        return result;            
    }
    
    public double max(){
        double result=get(0);
        for (int i=1;i<getLength();i++)
            if (get(i)>result)
                result=get(i);
        return result;
    }
    
    public double sum(){
        double result=0;
        for (int i=0;i<getLength();i++)
            result+=get(i);
        return result;
    }
    
    public double product(){
        double result=1;
        for (int i=0;i<getLength();i++)
            result*=get(i);
        return result;
    }
    
    public double norm(){
        double result=0;
        for (int i=0;i<getLength();i++)
            result+=get(i)*get(i);
        return Math.sqrt(result);
    }
    
    public double average(){
        return sum()/getLength();
    }
    
    public double std(){
        double sum=0,sum_sq=0;
        for (int i=0;i<getLength();i++){
            sum+=get(i);
            sum_sq+=get(i)*get(i);
        }
        return Math.sqrt(sum_sq/getLength()-sum*sum/getLength());
    }
    
    public Vector _add(double this_v, Vector that, double that_v){
        assertSizeAlignment(that);
        for (int i=0;i<getLength();i++)
            this.set(i, this.get(i)*this_v+that.get(i)*that_v);
        return this;
    }
    
    public Vector add(double this_v, Vector that, double that_v){
        Vector result = this.copy();
        return result._add(this_v, that, that_v);
    }
    
    public Vector _add(double value){
        for (int i=0;i<getLength();i++)
            this.set(i, this.get(i)+value);
        return this;
    }
        
    public Vector add(Vector that){
        return this.add(1,that,1);
    }
    
    public Vector add(Vector that,double that_v){
        return this.add(1,that,that_v);
    }
    
    public Vector add(double this_v, Vector that){
        return this.add(this_v,that,1);
    }
    
    public Vector _times(double factor){
        for (int i=0;i<getLength();i++)
            this.set(i, this.get(i)*factor);
        return this;
    }
    
    public Vector times(double factor){
        Vector result = this.copy();
        result._times(factor);
        return result;
    }
    
    public Vector copy(){
        Vector result = new Vector(this.getLength());
        for (int i=0;i<getLength();i++)
            result.set(i, this.get(i));
        return result;
    }
    
    public void assertSizeAlignment(Vector that){
        assert this.getLength()==that.getLength() : String.format("Incompatible Vectors (%d, %d)",this.getLength(),that.getLength());
    }
    
    public void assertIndexBound(int i){
        assert i<getLength() : String.format("Trying to set elements outside the Vector ( %d / %d )",i,getLength());
        assert i>=0 : String.format("Trying to set elements outside the Vector ( %d / %d )",i,getLength());
    }
    
    public boolean equals(Vector that){
        assertSizeAlignment(that);
        for (int i=0;i<getLength();i++)
            if (this.get(i)!=that.get(i))
                return false;
        return true;
    }
    
    public double[] toArray(){
        double[] result = new double[this.getLength()];
        for (int i=0;i<result.length;i++)
            result[i]=this.get(i);
        return result;
    }
    
}
