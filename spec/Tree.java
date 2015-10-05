package ass2.spec;

import javax.media.opengl.GL2;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private double[] myPos;
    private int stacks = 20;
    private int slices = 10;
    private double radius = 0.5;
    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }
    
    public double[] getPosition() {
        return myPos;
    }
    
    public void draw(GL2 gl){
    	double deltaT;
    
    	
    	deltaT = 0.5/stacks;
    	int ang;  
    	int delang = 360/slices;
    	double x1,x2,z1,z2,y1,y2;
    	for (int i = 0; i < stacks; i++) 
    	{ 
    		double t = -0.25 + i*deltaT;
    		
    		gl.glBegin(GL2.GL_TRIANGLE_STRIP); 
    		for(int j = 0; j <= slices; j++)  
    		{  
    			ang = j*delang;
    			x1=radius * MathUtil.sphereR(t)*Math.cos((double)ang*2.0*Math.PI/360.0); 
    			x2=radius * MathUtil.sphereR(t+deltaT)*Math.cos((double)ang*2.0*Math.PI/360.0); 
    			y1 = radius * MathUtil.sphereGetY(t);

    			z1=radius * MathUtil.sphereR(t)*Math.sin((double)ang*2.0*Math.PI/360.0);  
    			z2= radius * MathUtil.sphereR(t+deltaT)*Math.sin((double)ang*2.0*Math.PI/360.0);  
    			y2 = radius * MathUtil.sphereGetY(t+deltaT);

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
    }
    

}
