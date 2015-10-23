package ass2.spec;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import ass2.org.json.*;


/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private List<Goblin> myGoblins;
    private List<Pond> myPonds;
    private float[] mySunlight;
    private double[] green = {0,1,0,1};
    private boolean showLines = false;
    
    //Member of Textures
    private String textureFileName0 = "src/ass2/grass.bmp";
    private String textureExt0 = "bmp";
    private MyTexture myTerrainTexture;
    
    private String textureFileName1 = "src/ass2/green.jpg";
    private String textureFileName2 = "src/ass2/wood.jpg";
    private String textureExt1 = "jpg";
    private String textureExt2 = "jpg";
    private MyTexture myTreeTextures[];
    
    private String textureFileName3 = "src/ass2/road.jpg";
    private String textureExt3 = "jpg";
    private MyTexture myRoadTexture;
    
    private String textureFileName4 = "src/ass2/water.jpg";
    private String textureExt4 = "jpg";
    private MyTexture myPondTexture;
    
//    private String textureFileName5 = "src/ass2/Half_Life.jpg";
//    private String textureExt5 = "jpg";
//    private MyTexture myGoblinTexture;
    
    
    private double dayLength = 30;
    private double currentTime;
    private double[] midPoint;
    private double sunAngle;
    private double[] sunVector;
    private double sunDistance;
    private float[] sunColor;
    private float[] sunSetColor = {253,125,1};
    private float[] middayColor = {255,255,255};
    private float[] sunAmb = {0.3f,0.3f,0.3f,1f};
    

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myGoblins = new ArrayList<Goblin>();
        myRoads = new ArrayList<Road>();
        myPonds = new ArrayList<Pond>();
        mySunlight = new float[3];
        
        sunColor = new float[4];
        sunColor[3] = 1f;
        midPoint = new double[3];
        midPoint[0] = (-1.0 + width)/2;
        midPoint[1] = 0;
        midPoint[2] = (-1.0 + depth)/2;
    }
    
    public void init(GL2 gl){
    	myTerrainTexture = new MyTexture(gl, textureFileName0, textureExt0, true);
    	myTreeTextures = new MyTexture[2];
    	myTreeTextures[0] = new MyTexture(gl, textureFileName1, textureExt1, true);
    	myTreeTextures[1] = new MyTexture(gl, textureFileName2, textureExt2, true);
    	myRoadTexture = new MyTexture(gl, textureFileName3, textureExt3, true);
    	myPondTexture = new MyTexture(gl, textureFileName4, textureExt4, true);
//    	myGoblinTexture = new MyTexture(gl, textureFileName5, textureExt5, true);
    	
    	sunVector = new double[3];
    	sunVector[0] = - midPoint[0] + mySunlight[0];
    	sunVector[1] = 0;
    	sunVector[2] = - midPoint[1] + mySunlight[2];
    	
    	sunDistance = Math.sqrt(sunVector[0]*sunVector[0] + sunVector[2]*sunVector[2]);
    	
    	sunVector = MathUtil.normalise(sunVector);
    	
    	sunAngle = Math.toDegrees(Math.atan2(mySunlight[1], sunDistance));
    	//System.out.println(sunAngle);
    	currentTime = 24.0 * sunAngle/360 + 6;
    	
    	for (int i = 0; i < myGoblins.size();i++){
    		myGoblins.get(i).init(gl);
    	}
    }
    
    public void updateTime(double dt){
    	currentTime = Math.max(6,Math.min( 18 , currentTime + dt/dayLength * 24));
    	//System.out.println(currentTime);
    	if (currentTime >= 18){
    		currentTime = 6;
    	}
    	updateSunPos();
    	updateSunColor();
    }
    
    public void update(double dt){
    	updateTime(dt);
    	updatePonds(dt);
    }
    
    public void updatePonds(double dt){
    	for (int i = 0; i < myPonds.size();i++){
    		myPonds.get(i).update(dt);
    	}
    }
    
    public float[] getSunColor(){
    	return sunColor;
    }
    
    public float[] getSunAmb(){
    	return sunAmb;
    }
    
    public void updateSunColor(){
    	float r = sunSetColor[0] - middayColor[0];
    	float g = sunSetColor[1] - middayColor[1];
    	float b = sunSetColor[2] - middayColor[2];
    	
    	float ratio = (float) (Math.abs(currentTime - 12f) / 6f);
    	
    	
    	sunColor[0] = (middayColor[0] + r*ratio)/255f;
    	sunColor[1] = (middayColor[1] + g*ratio)/255f;
    	sunColor[2] = (middayColor[2] + b*ratio)/255f;
    }
    
    public void updateSunPos(){
    	sunAngle = (currentTime - 6) / 24 * 360;
    	double d = Math.cos(Math.toRadians(sunAngle)) * sunDistance;
    	mySunlight[0] = (float) (sunVector[0] * d);
    	mySunlight[1] = (float) Math.sin(Math.toRadians(sunAngle));
    	mySunlight[2] = (float) (sunVector[2] * d);
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }
    
    public List<Goblin> goblins() {
        return myGoblins;
    }

    public List<Road> roads() {
        return myRoads;
    }
    
    public List<Pond> ponds() {
        return myPonds;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * TO BE COMPLETED
     * 
     * @param x
     * @param z
     * @return
     */
    public double altitude(double x, double z) {
    	double altitude = 0;
    	int iX = (int)x;
    	int iZ = (int)z;
    	int nX = Math.min(iX+1, mySize.width-1);
    	int nZ = Math.min(iZ+1, mySize.height-1);
    	if (x-iX + z - iZ < 1 ){
    		altitude = (x - iX) * (myAltitude[nX][iZ] - myAltitude[iX][iZ]) + myAltitude[iX][iZ];
    		altitude += (z - iZ) * (myAltitude[iX][nZ] - myAltitude[iX][iZ]);
    		//System.out.println("case 1");
    	} else {
    		altitude = (x - iX) * (myAltitude[nX][nZ] - myAltitude[iX][nZ]) + myAltitude[iX][nZ];
    		altitude += ( myAltitude[nX][iZ] - myAltitude[nX][nZ])*(1 - z + iZ) ;
    		//System.out.println("case 2");
    	}   
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        myTrees.add(tree);
    }
    
    public void addGoblin(double x, double z) {
        double y = altitude(x, z);
        Goblin goblin = new Goblin(x, y, z);
        myGoblins.add(goblin);
    }

    public void addPond(double x, double z, double r) {
        double y = altitude(x, z);
        Pond pond = new Pond(x, y, z, r);
        myPonds.add(pond);
    }

    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        myRoads.add(road);        
    }
    
    private void drawTriangle(double[] p1, double[] p2, double[] p3,double[] colour, GL2 gl) {
    	//if (colour == null){
    		//return;
    	//}
    	//System.out.print(p1[0] + ","+p1[1]+","+p1[2]+"|");
    	//System.out.print(p2[0] + ","+p2[1]+","+p2[2]+"|");
    	//System.out.println(p3[0] + ","+p3[1]+","+p3[2]+"|");
    	
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, myTerrainTexture.getTextureId());
    	float matAmbAndDif[] = {0.3f, 0.3f, 0.3f, 1.0f};
        float matSpec[] = { 0.1f, 0.1f, 0.1f, 1.0f };
        float matShine[] = { 30.0f };
        float emm[] = {0.0f, 0.0f, 0.0f, 1.0f};
        // Material properties of sphere.
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpec,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShine,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
		gl.glBegin(GL2.GL_TRIANGLES);
		{	
			//I add textures amoung every vertex
			gl.glColor4d(1,1,1,1);
			double[] n = MathUtil.getNormal(p1, p2, p3);
			n = MathUtil.normalise(n);
			//System.out.println(n[1]);
			gl.glNormal3d(n[0],n[1],n[2]);
			gl.glTexCoord2d(0.0, 0.0);
            gl.glVertex3d(p1[0], p1[1], p1[2]);
            gl.glTexCoord2d(1.0, 0.0);
            gl.glVertex3d(p2[0], p2[1], p2[2]);
            gl.glTexCoord2d(0.5, 1.0);
            gl.glVertex3d(p3[0], p3[1], p3[2]);
            
            
           
        }
        gl.glEnd();
        
        if (showLines){
        	gl.glColor4d(0,0,0,1);
    		gl.glBegin(GL2.GL_LINE_LOOP);
    		{	
                gl.glVertex3d(p1[0], p1[1], p1[2]);
                gl.glVertex3d(p2[0], p2[1], p2[2]);
                gl.glVertex3d(p3[0], p3[1], p3[2]);
               
            }
            gl.glEnd();
        }
		
	}
    
    public void draw(GL2 gl){
    	
    	for (int x = 0; x < mySize.width - 1; x++){
    		for (int z = 0; z < mySize.height - 1; z++){
    			//System.out.println(this.myTerrain.altitude(x, z));
    			double[] p3 = {x,getGridAltitude(x, z),z};
    			double[] p2 = {x+1,getGridAltitude(x+1, z),z};
    			double[] p1 = {x,getGridAltitude(x, z+1),z+1};
    			drawTriangle(p1, p2, p3, null, gl);
    			
    			double[] p6 = {x,getGridAltitude(x, z+1),z+1};
    			double[] p5 = {x+1,getGridAltitude(x+1, z),z};
    			double[] p4 = {x+1,getGridAltitude(x+1, z+1),z+1};
    			drawTriangle(p4, p5, p6, null, gl);
    		}
    	}
    	
    	for (int i = 0; i < myTrees.size(); i++){
    		myTrees.get(i).draw(gl,myTreeTextures);
    		//System.out.println(i);
    	}
    	
    	for (int i = 0; i < myRoads.size(); i++){
    		myRoads.get(i).draw(gl,this,myRoadTexture);
    		//System.out.println(i);
    	}
    	
    	for (int i = 0; i < myGoblins.size(); i++){
    		myGoblins.get(i).draw(gl);
    		//System.out.println(i);
    	}
    	
    	for (int i = 0; i < myPonds.size(); i++){
    		myPonds.get(i).draw(gl,myPondTexture);
    		//System.out.println(i);
    	}
    	
    }


}
