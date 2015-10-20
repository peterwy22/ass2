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
    private float[] mySunlight;
    private double[] green = {0,1,0,1};
    private boolean showLines = false;
    
    //Member of Textures
    private String textureFileName0 = "src/ass2/grass.bmp";
    private String textureExt0 = "bmp";
    private MyTexture myTextures = new MyTexture(textureFileName0, textureExt0, true);
    
    

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
        myRoads = new ArrayList<Road>();
        mySunlight = new float[3];
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

    public List<Road> roads() {
        return myRoads;
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
    	if (colour != null){
    		gl.glColor4d(colour[0], colour[1],colour[2],colour[3]);
    	}
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures.getTextureId());
		gl.glBegin(GL2.GL_TRIANGLES);
		{	
			//I add textures amoung every vertex
			
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
    	myTextures.setImage(gl);
    	
    	
    	
    	
    	//gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
    	

    	
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
    		myTrees.get(i).draw(gl);
    		//System.out.println(i);
    	}
    	
    	for (int i = 0; i < myRoads.size(); i++){
    		myRoads.get(i).draw(gl,this);
    		//System.out.println(i);
    	}
    	
    }


}
