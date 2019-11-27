package Graphics;

import Math.Vector;
import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class Figure extends JFrame{
    private final int _WIDTH = 500;
    private final int _HEIGHT = 100;
    private Container _drawable;
    private GraphCanvas _canvas;
    
    public Figure() {
        super("Figure");
        _drawable = getContentPane();
        _canvas = new GraphCanvas();
        _drawable.add(_canvas);
        setSize(_WIDTH, _HEIGHT);
        show();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    
    public void plot(Vector x, Vector y){
        _canvas.setData(x,y);
    }
    
    public void plot(Vector y){
        Vector x = new Vector(y.getLength());
        for(int i=0;i<x.getLength();i++)
            x.set(i,i);
        this.plot(x,y);
    }
    
    public void plot(double[] x, double[] y){
        plot(new Vector(x),new Vector(y));
    }
    
    public class GraphCanvas extends JPanel {
        private Vector xData;
        private Vector yData;
		
        public GraphCanvas() {
            super();
        }
        
        public void setData(Vector x, Vector y){
            assert x.getLength()==y.getLength() : String.format("X and Y should have the same number of elements ( %d / %d )",x.getLength(),y.getLength());
            xData=x;
            yData=y;
        }
        
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            //     g.setColor(color);

            double xmin=xData.min();
            double ymin=yData.min();
            double xmax=xData.max();
            double ymax=yData.max();
            
            double xrange=xmax-xmin;
            double yrange=ymax-ymin;
            
            int width=this.getWidth();
            int height=this.getHeight();
            
            int x1=0,y1=0;
            for(int i = 0; i < xData.getLength() - 1; i++) {
                int x0 = (int) ((xData.get(i) - xmin) / xrange * width);
                 x1 = (int) ((xData.get(i+1) - xmin) / xrange * width);
                int y0 = height - (int) ((yData.get(i) - ymin) / yrange * height);
                 y1 = height- (int) ((yData.get(i+1) - ymin) / yrange * height);
                g2.drawLine(x0, y0, x1, y1);
                if (i == 0)
                  g2.drawString(("" + x0 + ", " + y0), x0 - 20, y0 + 10);
                if (i == xData.getLength() - 2)
                  g2.drawString(("" + x1 + ", " + y1), x1, y1);
            }
            System.out.printf("x: %d,%d / %d,%d",x1,y1,this.getWidth(),this.getHeight());
        }
    }
    public static void main(String[] args) {
        Random rnd = new Random();
        double AT=10;
        double dt=1e-3;
        double f=1;
        int N=(int)Math.floor(AT/dt);
                
        Vector t = new Vector(N);
        Vector x = new Vector(N);
        for(int i=0;i<N;i++){
            t.set(i,i*dt);
            double expo=Math.exp(-t.get(i)/(AT/5));
            double sine=Math.sin(t.get(i)*2*Math.PI*f)/2;
            x.set(i,expo*sine);
        }
        
        Figure fig = new Figure();
        fig.plot(t,x);
    }
}
