package MachineLearning;

import Math.Vector;
import java.util.Random;

public class Neuron extends Classifier{
    private final Vector _weights;
    private double _bias;
    private double _a=1;
    private Double _trainingRate=1d;
    private ActivationFunction _activation;
 
    public final double getWeight(int i){
        return _weights.get(i);
    }
    
    public final void setWeight(int i, double v){
        _weights.set(i,v);
    }
    
    public final int getInputCount(){
        return _weights.getLength();
    }
    
    public final int getOutputCount(){
        return 1;
    }
    
    public final double getBias(){
        return _bias;
    }
    
    public final void setBias(double v){
        _bias=v;
    }
    
    public void setTrainingRate(double v){
        _trainingRate=v;
    }
    
    public double getTrainingRate(){
        return _trainingRate;
    }
    
    public Double _getTrainingRate(){
        return _trainingRate;
    }
    
    public Neuron(int inputCount){
        _weights = new Vector(inputCount);
        _activation = new ActivationFunctions.Sigmoid(1);
        Random rnd = new Random();
        setBias(rnd.nextDouble()*2-1.0);
        for( int i=0;i< getInputCount();i++)
            setWeight(i, rnd.nextDouble()*2-1.0);
    }
    
    public Vector classify(Vector pattern){
        assert pattern.getLength()==getInputCount(): String.format("Incompatible input size (%d, %d)",pattern.getLength(),getInputCount());
        Vector result = new Vector(1);
        double temp=getBias();
        temp+=pattern.dot(_weights);
        result.set(0,this._activation.evaluate(temp));
        return result;
    }
    
    public void train(Vector pattern, Vector target){
        assert target.getLength()!=1 : "Target vector must have only one element";
        
        _train(pattern,target.get(0));
    }
    
    public double _train(Vector pattern, double target){
        assert pattern.getLength()==getInputCount(): String.format("Incompatible input size (%d, %d)",pattern.getLength(),getInputCount());
        
        double error = target - classify(pattern).get(0);
        double morsel = getBias() + pattern.dot(_weights);
        morsel=error*_activation.derivative(morsel);
        setBias(getBias()+morsel*_trainingRate);
        Vector delta=pattern.multiply(morsel);
        _weights.add(1, delta, _trainingRate);
        return delta.norm();
    }
    
    public void show(){
        System.out.printf("Neuron: %.2f / ", getBias());
        for (int i=0;i<getInputCount();i++){
            System.out.printf("%.2f, ",getWeight(i));
        }
        System.out.println();
    }
    
    public static void main(String[] args){
        Neuron neu = new Neuron(2);
        Vector[] inputs = {
            new Vector(0,0),
            new Vector(0,1),
            new Vector(1,0),
            new Vector(1,1)
        };
        Vector[] outputs = {
            new Vector(0d),
            new Vector(0d),
            new Vector(0d),
            new Vector(1d)
        };
        double d=10;
        int count=0;
        while (d/count>1e-3 || count<10){
            int idx=count%inputs.length;
            double delta = neu._train(inputs[idx],outputs[idx].get(0));
            d+=delta;
            count++;
            System.out.printf("%f\t%f\n",d,d/count);
        }
        System.out.printf("%d\t%.5f\n",count,d);
        for(int i=0;i<inputs.length;i++)
            System.out.printf("In: %.1f, %.1f\ttgt: %.1f\tout: %.1f\n",inputs[i].get(0),inputs[i].get(1),outputs[i].get(0),neu.classify(inputs[i]).get(0));
    }
    
}