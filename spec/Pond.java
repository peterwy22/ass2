package ass2.spec;

import javax.media.opengl.GL2;

public class Pond {
	private double[] myPos;
	private double myRadius;
	private static final int NUM_OF_SLICES = 32;
	private static final double CYCLE = 2;
	private double currentTime = 0;
	private double textCordD = 0;
	
	public Pond(double x, double y, double z, double radius) {
		
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
        myRadius = radius;
    }
	
	public double[] getPosition() {
        return myPos;
    }
	
	public double getRadius(){
		return myRadius;
	}
	
	public void update(double dt){
		currentTime += dt;
		if (currentTime >= CYCLE){
			currentTime = 0;
		}
		textCordD = currentTime/CYCLE * 0.5;
	}
	
	public void draw(GL2 gl, MyTexture texture){
		
			if (texture != null){
				gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
			}
			gl.glPushMatrix();
			gl.glTranslated(myPos[0], myPos[1] + 0.1, myPos[2]);
			gl.glBegin(GL2.GL_TRIANGLE_FAN);
			{	
				gl.glNormal3d(0, 1, 0);
				gl.glTexCoord2d(0.5, 0.5-textCordD);
				gl.glVertex3d(0, 0,0); //The centre of the circle
            	double angle = 0;
            	double angleIncrement = 2*Math.PI/NUM_OF_SLICES;
            	for(int i=0; i <= NUM_OF_SLICES; i++){
            		angle = i* angleIncrement;
            		double x = myRadius * Math.cos(angle);
            		double z = myRadius * Math.sin(angle);
            		if (i %2 == 0){
            			gl.glTexCoord2d(0.25, 1-textCordD);
            		} else {
            			gl.glTexCoord2d(0.75, 1-textCordD);
            		}
            		gl.glVertex3d(x, 0,z);
            
            	} 
        	}
        	gl.glEnd();
        	
        	gl.glPopMatrix();


    }
}
