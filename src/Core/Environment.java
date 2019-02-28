package Core;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Environment extends JFrame{

    public Zigg[] population;
    public int size=20;
    public double smellyx,smellyy;
    public boolean visibility = true;
    
    public static final int max_coords=700; 
    private static final int INTERVAL=20; 
    private Container _drawable;
    private GraphCanvas _canvas;
    private double t;
    
    public Environment(){
        super("Figure");
        
        
        Random rnd = new Random();
        population = new Zigg[size];
        smellyx=max_coords/2;
        smellyy=max_coords/2;
        t=0;
        
        for (int i=0;i<size;i++){
            population[i]=new Zigg(this, rnd.nextInt(max_coords),rnd.nextInt(max_coords));
        }
        
        _drawable = getContentPane();
        _canvas = new GraphCanvas(population);
        _drawable.add(_canvas);
        setSize(max_coords, max_coords);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public double smell(double x,double y){
        Random rnd = new Random();
        return (Math.sqrt(Math.pow(x-getSmellyX(), 2)+Math.pow(y-getSmellyY(), 2))+rnd.nextDouble()-0.5)/max_coords;
    }
    
    public void update(){
        t+=1;
        for (int i=0;i<size;i++)
            population[i].update();
         
        if (visibility){
            _canvas.repaint();
            try{
                Thread.sleep(INTERVAL);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public double getSmellyX(){
        return smellyx+max_coords/4*Math.sin(2*Math.PI*1/10000*t);
    }

    public double getSmellyY(){
        return smellyy+max_coords/4*Math.cos(2*Math.PI*1/10000*t);
    }
    
    public class GraphCanvas extends JPanel {
        	
        public Zigg[] population;

        public GraphCanvas(Zigg[] data) {
            super();
            population=data;
        }
        
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            //     g.setColor(color);
            
            for(int i = 0; i < population.length; i++) {
                population[i].render(g2);                
            }
            g2.drawOval((int)getSmellyX()-25, (int)getSmellyY()-25, 50, 50);
        }
    }
    
    public static void main(String[] args) {
        Random rnd = new Random();
        
        Environment env = new Environment();
        env.setVisible(true);
        env.visibility=false;
        for (int i=0;i<0;i++){
            for (int j=0;j<100000;j++)
                env.update();
            System.out.printf("%d/10\n",i);
        }
        env.visibility=true;
        System.out.println("Done training");
        while(true)
            env.update();
    }
}
