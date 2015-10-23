package ass2.spec;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

public class Goblin {
	private double[] myPos;
	
	// Vertexes of the cube
	private float positions[] =  {-1,-1,-1, 
			-1,-1,1,
			-1,1,-1, 
			-1, 1,1,
			1,-1,-1, 
			1,-1,1,
			1, 1, -1,
			1, 1, 1};
	
	//There should be a matching entry in this array for each entry in
		//the positions array
		private float colors[] =     {1,0,0, 
				1,0,0,
				0,1,0,
				0,1,0,
				1,0,0, 
				1,0,0,
				0,1,0,
				0,1,0}; 
		
		private double normals[][] = {{0,-1,0},
				{0,1,0},
				{-1,0,0},
				{1,0,0},
				{0,0,-1},
				{0,0,1}
		};
		
		//Best to use smallest data type possible for indexes 
		//We could even use byte here...
	private short indexes[] = {0,4,5,1,6,2,3,7,2,0,1,3,4,6,7,5,4,0,2,6,1,5,7,3};
	
	//These are not vertex buffer objects, they are just java containers
	private FloatBuffer  posData= Buffers.newDirectFloatBuffer(positions);
	private FloatBuffer colorData = Buffers.newDirectFloatBuffer(colors);
	private ShortBuffer indexData = Buffers.newDirectShortBuffer(indexes);
	
	//We will be using 2 vertex buffer objects
	private int bufferIds[] = new int[2];
	
	private static final String VERTEX_SHADER = "src/ass2/spec/AttributeVertex.glsl";
	private static final String FRAGMENT_SHADER = "src/ass2/spec/AttributeFragment.glsl";
	
	private int shaderprogram;
	
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
		
		//Generate 2 VBO buffer and get their IDs
        gl.glGenBuffers(2,bufferIds,0);
        
      //This buffer is now the current array buffer
        //array buffers hold vertex attribute data
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
      //This is just setting aside enough empty space
        //for all our data
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,    //Type of buffer  
       	        positions.length * Float.BYTES +  colors.length* Float.BYTES, //size needed
       	        null,    //We are not actually loading data here yet
       	        GL2.GL_STATIC_DRAW); //We expect once we load this data we will not modify it
        
      //Actually load the positions data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, //From byte offset 0
       		 positions.length*Float.BYTES,posData);

        //Actually load the color data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
       		 positions.length*Float.BYTES,  //Load after the position data
       		 colors.length*Float.BYTES,colorData);
        
        
        //Now for the element array
        //Element arrays hold indexes to an array buffer
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);

        //We can load it all at once this time since there are not
        //two separate parts like there was with color and position.
        gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER,      
    	        indexes.length *Short.BYTES,
    	        indexData, GL2.GL_STATIC_DRAW);
   	    	 
   	 	try {
   		 shaderprogram = Shader.initShaders(gl,VERTEX_SHADER,FRAGMENT_SHADER);
  		    		 
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
   	 	
		gl.glPushMatrix();
			gl.glTranslated(myPos[0], myPos[1]+0.5, myPos[2]);
			gl.glScaled(0.4, 0.4, 0.4);
			drawCube(gl);
		gl.glPopMatrix();
	}
	
	 private void drawCube(GL2 gl) {
		 
		//Use the shader
		 
	        gl.glUseProgram(shaderprogram);
	        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
	           
	        int vertexColLoc = gl.glGetAttribLocation(shaderprogram,"vertexCol");
	        int vertexPosLoc = gl.glGetAttribLocation(shaderprogram,"vertexPos");
	               
	   	    // Specify locations for the co-ordinates and color arrays.
	        gl.glEnableVertexAttribArray(vertexPosLoc);
	        gl.glEnableVertexAttribArray(vertexColLoc);
	   	   	gl.glVertexAttribPointer(vertexPosLoc,3, GL.GL_FLOAT, true,0, 0); //last num is the offset
	   	   	gl.glVertexAttribPointer(vertexColLoc,3, GL.GL_FLOAT, true,0, positions.length*Float.BYTES);
	       
	    	
	   	    gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);
	   	 
	   	    for(int i = 0; i < 6; i++){
	   	    	gl.glNormal3d(normals[i][0], normals[i][1], normals[i][2]);
	   	    	gl.glDrawElements(GL2.GL_QUADS, 4, GL2.GL_UNSIGNED_SHORT,i*8);  
	   	    }
	    	
	    	gl.glUseProgram(0);
	    	   
	     	//Un-bind the buffer. 
	     	//This is not needed in this simple example but good practice
	         gl.glBindBuffer(GL.GL_ARRAY_BUFFER,0);
	         gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER,0);
	         
	         
	         
//	        // bottom
//	        gl.glBegin(GL2.GL_POLYGON);
//	        {
//	            // set all four normals to the face normal
//	            gl.glNormal3d(0, -1, 0);            
//
//	            // set texture coordinates for each vertex
//	            gl.glTexCoord2d(0, 0);
//	            gl.glVertex3d(-1, -1, -1);
//	            gl.glTexCoord2d(1, 0);
//	            gl.glVertex3d(1, -1, -1);
//	            gl.glTexCoord2d(1, 1);
//	            gl.glVertex3d(1, -1, 1);
//	            gl.glTexCoord2d(0, 1);
//	            gl.glVertex3d(-1, -1, 1);
//	        }
//	        gl.glEnd();
//
//	        // top
//	        gl.glBegin(GL2.GL_POLYGON);
//	        {
//	            gl.glNormal3d(0, 1, 0);
//	            gl.glTexCoord2d(1, 1);
//	            gl.glVertex3d(1, 1, -1);
//	            gl.glTexCoord2d(0, 1);
//	            gl.glVertex3d(-1, 1, -1);
//	            gl.glTexCoord2d(0, 0);
//	            gl.glVertex3d(-1, 1, 1);
//	            gl.glTexCoord2d(1, 0);
//	            gl.glVertex3d(1, 1, 1);
//	        }
//	        gl.glEnd();
//
//	        // left
//	        gl.glBegin(GL2.GL_POLYGON);
//	        {
//	            gl.glNormal3d(-1, 0, 0);
//	            gl.glVertex3d(-1, 1, -1);
//	            gl.glVertex3d(-1, -1, -1);
//	            gl.glVertex3d(-1, -1, 1);
//	            gl.glVertex3d(-1, 1, 1);
//	        }
//	        gl.glEnd();
//
//	        // right
//	        gl.glBegin(GL2.GL_POLYGON);
//	        {
//	            gl.glNormal3d(1, 0, 0);
//	            gl.glVertex3d(1, -1, -1);
//	            gl.glVertex3d(1, 1, -1);
//	            gl.glVertex3d(1, 1, 1);
//	            gl.glVertex3d(1, -1, 1);
//	        }
//	        gl.glEnd();
//
//	        // back
//	        gl.glBegin(GL2.GL_POLYGON);
//	        {
//	            gl.glNormal3d(0, 0, -1);
//	            
//	            gl.glTexCoord2d(1,1);
//	            gl.glVertex3d(1, -1, -1);
//	            gl.glTexCoord2d(0,1);
//	            gl.glVertex3d(-1, -1, -1);
//	            gl.glTexCoord2d(0,0);
//	            gl.glVertex3d(-1, 1, -1);
//	            gl.glTexCoord2d(1,0);
//	            gl.glVertex3d(1, 1, -1);
//	        }
//	        gl.glEnd();
//
//	        // front
//	        gl.glBegin(GL2.GL_POLYGON);
//	        {
//	            gl.glNormal3d(0, 0, 1);
//	            gl.glTexCoord2d(0,0);
//	            gl.glVertex3d(-1, -1, 1);
//	            
//	            gl.glTexCoord2d(1,0);
//	            gl.glVertex3d(1, -1, 1);
//	            
//	            gl.glTexCoord2d(1,1);            
//	            gl.glVertex3d(1, 1, 1);
//	            
//	            gl.glTexCoord2d(0,1);
//	            gl.glVertex3d(-1, 1, 1);
//	        }
//	        gl.glEnd();
//
	    }
}
