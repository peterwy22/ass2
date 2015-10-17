package ass2.spec;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener,KeyListener{

    public Terrain myTerrain;
    private boolean showLines = false;
    private static double angle;
    private static double angleIncrement = 30;
    private static double[] location = {0,0};   //{x,z}
    private static double speed = 1;
    private long myTime;
    private double[] AvatorPosition = {0,0,0};
    
    public HashMap<Integer, Key> keyBindings = new HashMap<Integer, Key>();
    public static boolean other[] = new boolean[256];
    public AvatorControlKey AvatorKey = new AvatorControlKey();
    
    private final double cameraDistance = 1;
    private final double cameraHeight = 0.5;
    
    private int stacks = 20;
    private int slices = 10;
    private double radius = 0.3;

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
          panel.addKeyListener(this);
 
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
    
    public void setAvator(double dt){
    	if (AvatorKey.turnLeft.isPressing){
    		angle -= angleIncrement * dt;
    	}
    	if (AvatorKey.turnRight.isPressing){
    		angle += angleIncrement * dt;
    	}
    	if (AvatorKey.forward.isPressing){
    		location[0] = Math.max(0,Math.min(myTerrain.size().width-1, location[0] + Math.sin(Math.toRadians(angle)) * speed * dt));
    		location[1] = Math.max(0,Math.min(myTerrain.size().height-1, location[1] + Math.cos(Math.toRadians(angle)) * speed * dt));
    		System.out.println(location[0] + "," + location[1]);
    	}
    	if (AvatorKey.backward.isPressing){
    		location[0] = Math.max(0,Math.min(myTerrain.size().width-1, location[0] - Math.sin(Math.toRadians(angle)) * speed * dt));
    		location[1] = Math.max(0,Math.min(myTerrain.size().height-1, location[1] - Math.cos(Math.toRadians(angle)) * speed * dt));
    	}
    	AvatorPosition[0] = location[0];
    	AvatorPosition[1] = myTerrain.getGridAltitude((int)location[0], (int)location[1]);
    	AvatorPosition[2] = location[1];
    	
    	double[] eye = new double[3];
    	eye[0] = AvatorPosition[0] + Math.sin(Math.toRadians(angle)) * cameraDistance;
    	eye[1] = AvatorPosition[1] + cameraHeight;
    	eye[2] = AvatorPosition[2] + Math.cos(Math.toRadians(angle)) * cameraDistance;
    	
    	//GLU glu = new GLU();
    	//glu.gluLookAt(eye[0], -eye[1], eye[2], AvatorPosition[0], AvatorPosition[1], AvatorPosition[2], 0, 1, 0);
    }
    
    public void setCamera(GL2 gl){
    	gl.glRotated(Math.toDegrees(-Math.atan2(cameraHeight,cameraDistance)), 1, 0, 0);
    	gl.glRotated(-angle, 0, 1, 0);
    	gl.glTranslated(Math.sin(Math.toRadians(angle)) * this.cameraDistance, cameraHeight , Math.sin(Math.toRadians(angle)));
    	gl.glTranslated(-AvatorPosition[0], -AvatorPosition[1], -AvatorPosition[2]);
    }
    
    
	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
		long time = System.currentTimeMillis();
        double dt = (time - myTime) / 1000.0;
        myTime = time;
        
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
    	gl.glLoadIdentity();
    	//gl.glScaled(0.1, 0.1, 0.1);
    	gl.glClearColor(0.3f ,0.3f, 0.3f, 1);
    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    	//gl.glRotated(60, 1, 0, 0);
    	//gl.glRotated(180, 0, 1, 0);
    	gl.glScaled(0.1, 0.1, 0.1);
    	setAvator(dt);
    	setCamera(gl);
    	myTerrain.draw(gl);
    	drawAvator(gl, AvatorPosition);
    	
    	
    	/*
    	double[] p3 = {0.5,0,0};
    	double[] p2 = {0.5,0.5,0};
    	double[] p1 = {0,0,0};	
    	this.drawTriangle(p1, p2, p3, green, gl);
    	*/
    	
    	//gl.glColor4d(1,1,1,1);
		
	}
	
	public void drawAvator(GL2 gl,double[] location){
    	double deltaT;
    	deltaT = 0.5/stacks;
    	int ang;  
    	int delang = 360/slices;
    	double x1,x2,z1,z2,y1,y2;
    	gl.glPushMatrix();
    	gl.glTranslated(location[0], location[1], location[2]);
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

    			double normal[] = {-x1,-y1,-z1};


    			MathUtil.normalise(normal);    

    			gl.glNormal3dv(normal,0);         
    			gl.glVertex3d(x1,y1,z1);
    			normal[0] = -x2;
    			normal[1] = -y2;
    			normal[2] = -z2;

    			MathUtil.normalise(normal);    
    			gl.glNormal3dv(normal,0); 
    			gl.glVertex3d(x2,y2,z2); 
    		}; 
    		gl.glEnd();
    	}
    	gl.glPopMatrix();
    }

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		setAvatorKeys(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S);
		GL2 gl = drawable.getGL().getGL2();
    	gl.glEnable(GL2.GL_DEPTH_TEST);
    	//gl.glEnable(GL2.GL_CULL_FACE);
    	//gl.glCullFace(GL2.GL_BACK);
    	gl.glEnable(GL2.GL_LIGHTING);
    	gl.glEnable(GL2.GL_LIGHT0); 
    	gl.glEnable(GL2.GL_NORMALIZE);
    	myTime = System.currentTimeMillis();
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
	
	public void setAvatorKeys(int left, int right, int forward, int backward){
		keyBindings.clear();
		bind(left, AvatorKey.turnLeft);
		bind(right, AvatorKey.turnRight);
		bind(forward, AvatorKey.forward);
		bind(backward, AvatorKey.backward);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (isKeyBinded(e.getExtendedKeyCode())){
			other[e.getExtendedKeyCode()] = true;
	    	keyBindings.get(e.getKeyCode()).isPressing = true;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (isKeyBinded(e.getExtendedKeyCode())){
			other[e.getExtendedKeyCode()] = false;
	    	keyBindings.get(e.getKeyCode()).isPressing = false;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isKeyBinded(int extendedKey){
	    return keyBindings.containsKey(extendedKey);
	}
	
	public void bind(Integer keyCode, Key key){
	    keyBindings.put(keyCode, key);
	}
}
