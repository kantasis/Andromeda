package Core;

public class NeuronLayer extends Classifier{
    
    private Neuron[] _neurons;
    
    public NeuronLayer(int inputs,int outputs){
        _neurons = new Neuron[outputs];
        for (int i=0;i<outputs;i++)
            _neurons[i]=new Neuron(inputs);
    }
    
    public Neuron getNeuron(int i){
        return _neurons[i];
    }
    
    public int getNeuronCount(){
        return _neurons.length;
    }
    
    public int getOutputCount(){
        return getNeuronCount();
    }
    
    public int getInputCount(){
        return getNeuron(0).getInputCount();
    }
    
    public double getTrainingRate(){
        return getNeuron(0).getTrainingRate();
    }
    
    public void setTrainingRate(double v){
        for(int i=0;i<getNeuronCount();i++)
            getNeuron(i).setTrainingRate(v);
    }
    
    public Vector classify(Vector input){
        assert input.getLength()==getInputCount(): String.format("Incompatible input size (%d, %d)",input.getLength(),getInputCount());
        
        Vector output = new Vector(getOutputCount());
        for (int i=0;i<getOutputCount();i++)
            output.set(i,getNeuron(i).classify(input).get(0));
        return output;
    }
    
    public void train(Vector pattern, Vector target){
        this._train(pattern, target);
    }
    
    public double _train(Vector pattern, Vector target){
        assert pattern.getLength()==getInputCount(): String.format("Incompatible input size (%d, %d)",pattern.getLength(),getInputCount());
        assert target.getLength()==getOutputCount(): String.format("Incompatible target size (%d, %d)",target.getLength(),getOutputCount());
        
        double delta=0;
        //delta.show();
        //System.out.printf("getInputCount: %d [%d %d]\n", getInputCount(),delta.getRows(),delta.getColumns());
        for (int i=0;i<getOutputCount();i++){
            double temp = getNeuron(i)._train(pattern,target.get(i));
            //temp.show();
            delta+=Math.pow(temp,2);
        }
        //System.out.printf("/getInputCount:\n\n");
        //MultiLayerNetwork.printArray(error);
        return Math.sqrt(delta);
    }
    
    public Vector reverse(Vector v){
        assert v.getLength()==getOutputCount(): String.format("Incompatible output size (%d, %d)",v.getLength(),getOutputCount());
        Vector result = new Vector(getInputCount());
        for (int i=0;i<result.getLength();i++){
            double temp = 0;
            for (int j=0;j<getOutputCount();j++)
                temp+=v.get(j)*this.getNeuron(j).getWeight(i);
            result.set(i,temp);
        }
        return result;
    }
    
    public void show(){
        System.out.printf("Neuron Layer: %d x %d \n", getInputCount(),getOutputCount());
        for (int i=0;i<getOutputCount();i++){
            System.out.printf("%3d\t",i);
            for (int j=0;j<getInputCount();j++)
                System.out.printf("%5.2f, ",getNeuron(i).getWeight(j));
            System.out.printf(" (%5.2f)\n",getNeuron(i).getBias());
        }
        System.out.println();
    }
    
    public static NeuronLayer identityLayer(int inputs){
        NeuronLayer result = new NeuronLayer(inputs,inputs);
        for (int i=0;i>inputs;i++)
            for(int j=0;j<inputs;j++)
                result.getNeuron(i).setWeight(j, i==j?1:0);
        return result;
    }
    
    public static void main(String[] args){
        NeuronLayer neu = new NeuronLayer(2,2);
        Vector[] inputs = {
            new Vector(0,0),
            new Vector(0,1),
            new Vector(1,0),
            new Vector(1,1)
        };
        Vector[] outputs = {
            new Vector(0d,0d),
            new Vector(0d,1d),
            new Vector(0d,0d),
            new Vector(1d,0d)
        };
        double d=10;
        int count=0;
        while (d/count>1e-4 || count<10){
            int idx=count%inputs.length;
            double delta = neu._train(inputs[idx],outputs[idx]);
            d+=delta;
            count++;
            System.out.printf("%f\t%f\n",d,d/count);
        }
        System.out.printf("%d\t%.5f\n",count,d);
        for(int i=0;i<inputs.length;i++)
            System.out.printf("In: %s\ttgt: %s\tout: %s\n",inputs[i],outputs[i],neu.classify(inputs[i]));
    }
    
}
