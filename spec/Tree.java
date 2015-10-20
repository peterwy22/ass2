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
    private double trunkRadius = 0.1;
    private double height = 1;
    private static final int NUM_OF_SLICES = 32;
    
    //Textures
    private String textureLeafFileName = "src/ass2/green.bmp";
    private String textureTrunkFileName = "src/ass2/wood.bmp";
    private String textureExt = "bmp";
    private MyTexture myLeafTexture = new MyTexture(textureLeafFileName, textureExt, true);
    private MyTexture myTrunkTexture = new MyTexture(textureTrunkFileName, textureExt, true);
    
    
    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }
    
    public double[] getPosition() {
        return myPos;
    }
    
    public void drawLeaf(GL2 gl){
    	double deltaT;
    	deltaT = 0.5/stacks;
    	int ang;  
    	int delang = 360/slices;
    	double x1,x2,z1,z2,y1,y2;
    	
    	float matAmbAndDif[] = {1.0f, 0.0f, 0.0f, 1.0f};
        float matSpec[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float matShine[] = { 50.0f };
        float emm[] = {0.0f, 0.0f, 0.0f, 1.0f};
        // Material properties of sphere.
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpec,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShine,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
    	
    	
    	gl.glPushMatrix();
    	gl.glTranslated(0, height, 0);
    	
    	
    	
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
    			double tCoord = 1.0/stacks * i;
    			double sCoord = 1.0/slices * j;
    			gl.glTexCoord2d(sCoord, tCoord);
    			gl.glVertex3d(x1,y1,z1);
    			normal[0] = x2;
    			normal[1] = y2;
    			normal[2] = z2;

    			MathUtil.normalise(normal);    
    			gl.glNormal3dv(normal,0); 
    			tCoord = 1.0/stacks * (i+1);
    			gl.glTexCoord2d(sCoord, tCoord);
    			gl.glVertex3d(x2,y2,z2); 
    		}; 
    		gl.glEnd();
    	}
    	gl.glPopMatrix();
    }
    
    public void drawTrunk(GL2 gl){
    	
    	gl.glBegin(GL2.GL_QUADS);
		{	
        	double angle = 0;
        	double angleIncrement = 2*Math.PI/NUM_OF_SLICES;
        	for(int i=0; i <= NUM_OF_SLICES; i++){
        		angle = i* angleIncrement;
        		double x1 = trunkRadius * Math.cos(angle);
        		double z1 = trunkRadius * Math.sin(angle);
        		
        		angle = (i+1)* angleIncrement;
        		double x2 = trunkRadius * Math.cos(angle);
        		double z2 = trunkRadius * Math.sin(angle);
        
        		double[] p1 = {x1,0,z1};
        		double[] p2 = {x2,0,z2};
        		double[] p3 = {x2,height,z2};
        		double[] n = MathUtil.getNormal(p3, p2, p1);
    			n = MathUtil.normalise(n);
    			gl.glNormal3d(n[0],n[1],n[2]);
    			gl.glTexCoord2d(i/NUM_OF_SLICES, 0.0);
        		gl.glVertex3d(x1, 0, z1);
        		gl.glTexCoord2d((i+1)/NUM_OF_SLICES, 0.0);
        		gl.glVertex3d(x2, 0, z2);
        		gl.glTexCoord2d(i/NUM_OF_SLICES, 1.0);
        		gl.glVertex3d(x2, height, z2);
        		gl.glTexCoord2d((i+1)/NUM_OF_SLICES, 1.0);
        		gl.glVertex3d(x1, height, z1);
        
        	} 
    	}
    	gl.glEnd();
    }
    
    public void draw(GL2 gl){
    	gl.glMatrixMode(GL2.GL_MODELVIEW);
    	gl.glPushMatrix();
    	gl.glTranslated(myPos[0], myPos[1], myPos[2]);
    	myLeafTexture.setImage(gl);
    	drawLeaf(gl);
    	myTrunkTexture.setImage(gl);
    	drawTrunk(gl);
    	gl.glPopMatrix();
    }
    

}
