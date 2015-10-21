package ass2.spec;

import javax.media.opengl.GL2;

public class Goblin {
	private double[] myPos;
	
	public Goblin(double x, double y, double z) {
		
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }
	
	public double[] getPosition() {
        return myPos;
    }
	
	public void draw(GL2 gl){
		gl.glPushMatrix();
			gl.glTranslated(myPos[0], myPos[1]+0.5, myPos[2]);
			gl.glScaled(0.4, 0.4, 0.4);
			drawCube(gl);
		gl.glPopMatrix();
	}
	
	 private void drawCube(GL2 gl) {

	        // bottom
	        gl.glBegin(GL2.GL_POLYGON);
	        {
	            // set all four normals to the face normal
	            gl.glNormal3d(0, -1, 0);            

	            // set texture coordinates for each vertex
	            gl.glTexCoord2d(0, 0);
	            gl.glVertex3d(-1, -1, -1);
	            gl.glTexCoord2d(1, 0);
	            gl.glVertex3d(1, -1, -1);
	            gl.glTexCoord2d(1, 1);
	            gl.glVertex3d(1, -1, 1);
	            gl.glTexCoord2d(0, 1);
	            gl.glVertex3d(-1, -1, 1);
	        }
	        gl.glEnd();

	        // top
	        gl.glBegin(GL2.GL_POLYGON);
	        {
	            gl.glNormal3d(0, 1, 0);
	            gl.glTexCoord2d(1, 1);
	            gl.glVertex3d(1, 1, -1);
	            gl.glTexCoord2d(0, 1);
	            gl.glVertex3d(-1, 1, -1);
	            gl.glTexCoord2d(0, 0);
	            gl.glVertex3d(-1, 1, 1);
	            gl.glTexCoord2d(1, 0);
	            gl.glVertex3d(1, 1, 1);
	        }
	        gl.glEnd();

	        // left
	        gl.glBegin(GL2.GL_POLYGON);
	        {
	            gl.glNormal3d(-1, 0, 0);
	            gl.glVertex3d(-1, 1, -1);
	            gl.glVertex3d(-1, -1, -1);
	            gl.glVertex3d(-1, -1, 1);
	            gl.glVertex3d(-1, 1, 1);
	        }
	        gl.glEnd();

	        // right
	        gl.glBegin(GL2.GL_POLYGON);
	        {
	            gl.glNormal3d(1, 0, 0);
	            gl.glVertex3d(1, -1, -1);
	            gl.glVertex3d(1, 1, -1);
	            gl.glVertex3d(1, 1, 1);
	            gl.glVertex3d(1, -1, 1);
	        }
	        gl.glEnd();

	        // back
	        gl.glBegin(GL2.GL_POLYGON);
	        {
	            gl.glNormal3d(0, 0, -1);
	            
	            gl.glTexCoord2d(1,1);
	            gl.glVertex3d(1, -1, -1);
	            gl.glTexCoord2d(0,1);
	            gl.glVertex3d(-1, -1, -1);
	            gl.glTexCoord2d(0,0);
	            gl.glVertex3d(-1, 1, -1);
	            gl.glTexCoord2d(1,0);
	            gl.glVertex3d(1, 1, -1);
	        }
	        gl.glEnd();

	        // front
	        gl.glBegin(GL2.GL_POLYGON);
	        {
	            gl.glNormal3d(0, 0, 1);
	            gl.glTexCoord2d(0,0);
	            gl.glVertex3d(-1, -1, 1);
	            
	            gl.glTexCoord2d(1,0);
	            gl.glVertex3d(1, -1, 1);
	            
	            gl.glTexCoord2d(1,1);            
	            gl.glVertex3d(1, 1, 1);
	            
	            gl.glTexCoord2d(0,1);
	            gl.glVertex3d(-1, 1, 1);
	        }
	        gl.glEnd();

	    }
}
