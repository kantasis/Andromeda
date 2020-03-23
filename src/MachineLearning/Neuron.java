package MachineLearning;

import Core.Logger;
import Math.Operatables.Real;
import Math.Vector;
import java.util.Random;

public class Neuron extends Classifier{
    private final Vector _weights;
    private Real _bias;
    private Real _a=new Real(1);
    private Real _trainingRate=new Real (1);
    private ActivationFunction _activation;
 
    public final Real getWeight(int i){
        return _weights.get(i);
    }
    
    public final void setWeight(int i, double v){
        _weights.set(i,v);
    }
    
    public final void setWeight(int i, Real v){
        _weights.set(i,v);
    }
    
    public final int getInputCount(){
        return _weights.getLength();
    }
    
    public final int getOutputCount(){
        return 1;
    }
    
    public final Real bias(){
        return _bias;
    }
    
    public final Neuron setBias(Real v){
        _bias=v;
        return this;
    }
    
    public Neuron setTrainingRate(Real v){
        _trainingRate=v;
        return this;
    }
    
    public Real trainingRate(){
        return _trainingRate;
    }
    
    public Neuron(int inputCount){
        _weights = new Vector(inputCount);
        _activation = new ActivationFunctions.Sigmoid();
        Random rnd = new Random();
        setBias(new Real(rnd.nextDouble()*2-1.0));
        for( int i=0;i< getInputCount();i++)
            setWeight(i, rnd.nextDouble()*2-1.0);
    }
    
    public Vector classify(Vector pattern){
        assert pattern.getLength()==getInputCount(): String.format("Incompatible input size (%d, %d)",pattern.getLength(),getInputCount());
        Vector result = new Vector(1);
        Real temp=pattern.getDot(_weights).add(bias());
        result.set(0,_activation.evaluate(temp));
        return result;
    }
    
    public void train(Vector pattern, Vector target){
        assert target.getLength()!=1 : "Target vector must have only one element";
        train(pattern,target.get(0));
    }
    
    public NeuronTrainingResults train(Vector pattern, Real target){
        assert pattern.getLength()==getInputCount(): String.format("Incompatible input size (%d, %d)",pattern.getLength(),getInputCount());
        Real error = classify(pattern).get(0).diff(target).negate();
        Real weightedSum = pattern.getDot(_weights).add(bias());
        
        weightedSum=_activation.derivative(weightedSum).multiply(error);
        bias().add(weightedSum.getMultiply(trainingRate()));
        Vector delta=(Vector) pattern.getMultiply(weightedSum);
        _weights.add(delta.getMultiply(trainingRate()));
        
        NeuronTrainingResults results = new NeuronTrainingResults();
        results.delta=delta;
        results.error=error;
        
        return results;
    }
    
    public void show(){
        Logger.log("Neuron: %s / ", bias());
        String str ="";
        for (int i=0;i<getInputCount();i++){
            str+=String.format("%s, ",getWeight(i));
        }
        Logger.log(str);
    }
    
    public class NeuronTrainingResults{
        public NeuronTrainingResults(){}
        public Vector delta;
        public Real error;
    }

    public static void unittest(int max){
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
        
        Real error = Real.unit();
        Real errorSoS = Real.zero();
        int count=0;
        
        // Keep only the main exit condition here
        while (error.compareTo(new Real(1e-3))<0 ){
            int idx=count%inputs.length;
            NeuronTrainingResults grades = neu.train(inputs[idx],outputs[idx].get(0));
            error = grades.error.abs();
            errorSoS.add(error);

            // Put the contingency exit condition here
            if (count++>max)
                break;
        }
        Logger.log("%15s\t%s",count,errorSoS.div(new Real(max)));
        for(int i=0;i<inputs.length;i++)
            Logger.log("In: %s, %s\ttgt: %s\tout: %s",inputs[i].get(0),inputs[i].get(1),outputs[i].get(0),neu.classify(inputs[i]).get(0));
    }
    
    public static void main(String[] args){
        for (int i=1;i<1e8;i*=10)
            unittest(i);
    }

        
}