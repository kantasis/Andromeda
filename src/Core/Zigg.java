/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import MachineLearning.MultilayerNetwork;
import Math.Vector;
import Graphics.Renderable;
import java.awt.Graphics2D;

/**
 *
 * @author GeorgeKantasis
 */
public class Zigg implements Renderable{
    private static final double max_speed=3;
    private static final double max_coords=600;
    
    private double x;
    private double y;
    private double speed;
    private double theta;
    private double[] memory;
    private int perception=6;
    private Environment environment;
    private Vector avgMem;
    private int avgIdx=0;
    
    
    public MultilayerNetwork brain;
    
    public Zigg(Environment env){
        x=0;
        y=0;
        speed=0;
        brain=new MultilayerNetwork(perception,4,3,3);
        memory=new double[2];
        environment=env;
        avgMem = new Vector(5);
        avgMem.add(1);
    }
    
    public Zigg(Environment env, int x,int y){
        this(env);
        this.x=x;
        this.y=y;
    }
    
    public void move(){
        x+=speed*Math.sin(theta);
        y+=speed*Math.cos(theta);
        
        if (x<0) x=0;
        if (y<0) y=0;
        if (x>Environment.max_coords) x=Environment.max_coords;
        if (y>Environment.max_coords) y=Environment.max_coords;
               
    }
    
    public void steer(double angle){
        theta+=angle;
        if (theta<0) theta+=2*Math.PI;
        if (theta>2*Math.PI) theta-=2*Math.PI;
    }
    
    public void accelerate(double val){
        speed+=val;
        if (speed<-max_speed) speed=-max_speed;
        if (speed>max_speed) speed=max_speed;
    }
    
    public Vector perceive(){
        Vector result = new Vector (perception);
        int c=0;
        result.set(c++,x/max_coords);
        result.set(c++,y/max_coords);
        result.set(c++,theta/2/Math.PI);
        
        result.set(c++, memory[0]);
        result.set(c++, memory[1]);
        result.set(c++, avgMem.average());
        return result;
    }
    
    public double sniff(){
        return environment.smell(x, y);
    }
    
    public boolean praise(){
        return memory[0]>memory[1];
    }
    
    public Vector pattern, desicion;
    public void update(){
        pattern = perceive();
        desicion = brain.classify(pattern);
        brain.train(pattern, desicion);
        
        double acceleration = desicion.get(0).getPrimitive()*2-1;
        double delta_theta = desicion.get(1).getPrimitive()-desicion.get(2).getPrimitive();
        
        accelerate(acceleration/100);
        steer(delta_theta);
        move();
        
        memory[0]=memory[1];
        memory[1]=sniff();
        
        avgMem.set(avgIdx,memory[0]);
        avgIdx++;
        avgIdx%=avgMem.getLength();
        
        
        double delta=0;
        //desicion.set(0,Math.round(desicion.get(0)));
        //desicion.set(1,Math.round(desicion.get(1)));
        
        Vector target = new Vector(desicion.getLength());
        if (praise())
            for (int i=0;i<desicion.getLength();i++){
                double tgt = desicion.get(i).getPrimitive();
                target.set(i, 1 - Math.round(tgt));
            }
        else
            for (int i=0;i<desicion.getLength();i++){
                double tgt = desicion.get(i).getPrimitive();
                target.set(i, Math.round(tgt));
            }
                
        brain.train(pattern, target);
        
        //System.out.println(delta);
    }
    
    public double getx(){
        return x;
    }
    
    public double gety(){
        return y;
    }
    
    public double gett(){
        return theta;
    }
    
    public void render(Graphics2D g2){
        int x=(int) getx();
        int y=(int) gety();
        double t= gett();
        double cost=Math.cos(t);
        double sint=Math.sin(t);
            
        int length=5;
        g2.drawLine(x, y, (int)(x+length*sint), (int)(y+length*cost));
        if (praise()){
            //g2.drawLine(x, y, x, y-length);
            //g2.drawLine(x, y, x+length, y);
            g2.drawOval(x-4, y-4, 8, 8);
        }
        //g2.drawString(String.format("%s\n%s",pattern,desicion),x,y+20);
    }
    
    public static void main(String[] args){
        Environment env = new Environment();
        Zigg zagg = new Zigg(env);
        
        
        Vector pattern = zagg.perceive();
        Vector desicion = zagg.brain.classify(zagg.perceive());
        pattern.show();
        desicion.show();
        Vector target = new Vector(desicion.getLength());
        if (!zagg.praise())
            for (int i=0;i<desicion.getLength();i++){
                double tgt = desicion.get(i).getPrimitive();
                target.set(i, 1 - Math.round(tgt));
            }
        else
            for (int i=0;i<desicion.getLength();i++){
                double tgt = desicion.get(i).getPrimitive();
                target.set(i, Math.round(tgt));
            }
        
        target.show();
        
        target = new Vector(desicion.getLength());
        if (zagg.praise())
            for (int i=0;i<desicion.getLength();i++){
                double tgt = desicion.get(i).getPrimitive();
                target.set(i, 1 - Math.round(tgt));
            }
        else
            for (int i=0;i<desicion.getLength();i++){
                double tgt = desicion.get(i).getPrimitive();
                target.set(i, Math.round(tgt));
            }
        
        target.show();
    }
}
