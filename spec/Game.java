package ass2.spec;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener{

    private Terrain myTerrain;
    private double[] green = {0,1,0,1};

    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;
   
    }
    
    /** 
     * Run the game.
     *
     */
    public void run() {
    	  GLProfile glp = GLProfile.getDefault();
          GLCapabilities caps = new GLCapabilities(glp);
          GLJPanel panel = new GLJPanel();
          panel.addGLEventListener(this);
 
          // Add an animator to call 'display' at 60fps        
          FPSAnimator animator = new FPSAnimator(60);
          animator.add(panel);
          animator.start();

          getContentPane().add(panel);
          setSize(800, 600);        
          setVisible(true);
          setDefaultCloseOperation(EXIT_ON_CLOSE);        
    }
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        Game game = new Game(terrain);
        game.run();
    }
    
    private void drawTriangle(double[] p1, double[] p2, double[] p3,double[] colour, GL2 gl) {
    	//if (colour == null){
    		//return;
    	//}
    	//System.out.print(p1[0] + ","+p1[1]+","+p1[2]+"|");
    	//System.out.print(p2[0] + ","+p2[1]+","+p2[2]+"|");
    	//System.out.println(p3[0] + ","+p3[1]+","+p3[2]+"|");
    	gl.glColor4d(colour[0], colour[1],colour[2],colour[3]);
		gl.glBegin(GL2.GL_TRIANGLES);
		{	
            gl.glVertex3d(p1[0], p1[1], p1[2]);
            gl.glVertex3d(p2[0], p2[1], p2[2]);
            gl.glVertex3d(p3[0], p3[1], p3[2]);
           
        }
        gl.glEnd();
		
	}
    
    private void drawTerrain(GL2 gl){
    	Dimension size = myTerrain.size();
    	for (int x = 0; x < size.width - 1; x++){
    		for (int z = 0; z < size.height - 1; z++){
    			//System.out.println(this.myTerrain.altitude(x, z));
    			double[] p3 = {x,myTerrain.getGridAltitude(x, z),z};
    			double[] p2 = {x+1,myTerrain.getGridAltitude(x+1, z),z};
    			double[] p1 = {x,myTerrain.getGridAltitude(x, z+1),z+1};
    			drawTriangle(p1, p2, p3, green, gl);
    			
    			double[] p6 = {x,myTerrain.getGridAltitude(x, z+1),z+1};
    			double[] p5 = {x+1,myTerrain.getGridAltitude(x+1, z),z};
    			double[] p4 = {x+1,myTerrain.getGridAltitude(x+1, z+1),z+1};
    			drawTriangle(p4, p5, p6, green, gl);
    		}
    	}
    }

	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
    	gl.glLoadIdentity();
    	//gl.glScaled(0.1, 0.1, 0.1);
    	gl.glClearColor(0.3f ,0.3f, 0.3f, 1);
    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    	gl.glRotated(30, 1, 0, 0);
    	gl.glScaled(0.1, 0.1, 0.1);
    	//gl.glTranslated(0, -1, 0);
    	drawTerrain(gl);
    	
    	
    	double[] p3 = {0.5,0,0};
    	double[] p2 = {0.5,0.5,0};
    	double[] p1 = {0,0,0};	
    	this.drawTriangle(p1, p2, p3, green, gl);
    	
    	//gl.glColor4d(1,1,1,1);
		
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();
    	gl.glEnable(GL2.GL_DEPTH_TEST);
    	gl.glEnable(GL2.GL_CULL_FACE);
    	gl.glCullFace(GL2.GL_BACK);
    	//gl.glEnable(GL2.GL_LIGHTING);
    	//gl.glEnable(GL2.GL_LIGHT0); 
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
}
