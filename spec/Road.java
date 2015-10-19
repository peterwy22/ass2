package ass2.spec;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {

    private List<Double> myPoints;
    private double myWidth;
    private static final int NUM_OF_POINTS = 32;
    private int stacks = 20;
    private int slices = 10;
    private double pathStoneRadius = 0.1;
    
    /** 
     * Create a new road starting at the specified point
     */
    public Road(double width, double x0, double y0) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        myPoints.add(x0);
        myPoints.add(y0);
    }

    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(double width, double[] spine) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        for (int i = 0; i < spine.length; i++) {
            myPoints.add(spine[i]);
        }
    }

    /**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return myWidth;
    }

    /**
     * Add a new segment of road, beginning at the last point added and ending at (x3, y3).
     * (x1, y1) and (x2, y2) are interpolated as bezier control points.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void addSegment(double x1, double y1, double x2, double y2, double x3, double y3) {
        myPoints.add(x1);
        myPoints.add(y1);
        myPoints.add(x2);
        myPoints.add(y2);
        myPoints.add(x3);
        myPoints.add(y3);        
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return myPoints.size() / 6;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public double[] controlPoint(int i) {
        double[] p = new double[2];
        p[0] = myPoints.get(i*2);
        p[1] = myPoints.get(i*2+1);
        return p;
    }
    
    public void draw(GL2 gl, Terrain terrain){
    	for (int i = 0; i < size(); i++ ){
    		double[] prePoint = null;
    		for (int j = 0; j < NUM_OF_POINTS; j++){
    			double t = 1.0/NUM_OF_POINTS * j + i;
    			double[] p = point(t);
    			if (prePoint != null){
    				double[] v = {prePoint[0]-p[0],prePoint[1]-p[1]};
    				double[] n = {-v[1],0,v[0]};
    				n = MathUtil.normalise(n);
    				double[] left = {p[0]+n[0]*myWidth/2,p[1]+n[2]*myWidth/2};
    				double[] right = {p[0]-n[0]*myWidth/2,p[1]-n[2]*myWidth/2};
    				gl.glPushMatrix();
    					gl.glTranslated(left[0], terrain.altitude(left[0],left[1]), left[1]);
    					drawPathStone(gl);
    				gl.glPopMatrix();
    				gl.glPushMatrix();
    					gl.glTranslated(right[0], terrain.altitude(right[0],right[1]), right[1]);
    					drawPathStone(gl);
    				gl.glPopMatrix();
    			}
			prePoint = new double[2];
			prePoint[0] = p[0];
			prePoint[1] = p[1];
    		}
    	}
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public double[] point(double t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 6;
        
        double x0 = myPoints.get(i++);
        double y0 = myPoints.get(i++);
        double x1 = myPoints.get(i++);
        double y1 = myPoints.get(i++);
        double x2 = myPoints.get(i++);
        double y2 = myPoints.get(i++);
        double x3 = myPoints.get(i++);
        double y3 = myPoints.get(i++);
        
        double[] p = new double[2];

        p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
        p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;        
        
        return p;
    }
    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private double b(int i, double t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }
    
    public void drawPathStone(GL2 gl){
    	double deltaT;
    	deltaT = 0.5/stacks;
    	int ang;  
    	int delang = 360/slices;
    	double x1,x2,z1,z2,y1,y2;
    	gl.glPushMatrix();
    	for (int i = 0; i < stacks; i++) 
    	{ 
    		double t = -0.25 + i*deltaT;
    		
    		gl.glBegin(GL2.GL_TRIANGLE_STRIP); 
    		for(int j = 0; j <= slices; j++)  
    		{  
    			ang = j*delang;
    			x1=pathStoneRadius * MathUtil.sphereR(t)*Math.cos((double)ang*2.0*Math.PI/360.0); 
    			x2=pathStoneRadius * MathUtil.sphereR(t+deltaT)*Math.cos((double)ang*2.0*Math.PI/360.0); 
    			y1 = pathStoneRadius * MathUtil.sphereGetY(t);

    			z1=pathStoneRadius * MathUtil.sphereR(t)*Math.sin((double)ang*2.0*Math.PI/360.0);  
    			z2= pathStoneRadius * MathUtil.sphereR(t+deltaT)*Math.sin((double)ang*2.0*Math.PI/360.0);  
    			y2 = pathStoneRadius * MathUtil.sphereGetY(t+deltaT);

    			double normal[] = {x1,y1,z1};


    			MathUtil.normalise(normal);    

    			gl.glNormal3dv(normal,0);         
    			gl.glVertex3d(x1,y1,z1);
    			normal[0] = x2;
    			normal[1] = y2;
    			normal[2] = z2;

    			MathUtil.normalise(normal);    
    			gl.glNormal3dv(normal,0); 
    			gl.glVertex3d(x2,y2,z2); 
    		}; 
    		gl.glEnd();
    	}
    	gl.glPopMatrix();
    }


}
