import java.applet.Applet;    
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;

import javax.swing.JPanel;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

import java.net.URL;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class ReadObj extends Applet implements AdjustmentListener, ActionListener {

	
	/**
	 * Make it serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * File path, Simple Universe and BranchGroup
	 * are the most important part of the program.
	 */

	private SimpleUniverse u = null;
	private static String objpath="";
	private static String texturepath ="";
	private ObjectFile objfile = new ObjectFile(ObjectFile.RESIZE);
	private PolygonAttributes pa;
	private Scene s = null;
	private Texture texture;
	private TextureAttributes texAttr;
	private Appearance appearance;
	
	/**
	 * UI part
	 */
	private Scrollbar   sb;
    private Scrollbar	sb2;
    private Scrollbar	sb3;
    private Transform3D sliderXform;
    private Vector3f    sliderVector;
    private TransformGroup sliderTrans;
    private Panel p = new Panel();
    private Button points = new Button("Points");
    private Button lines = new Button("Line");
    private Button mesh = new Button("Mesh");
    private Button nonSkin = new Button("NonSkin");
    private Button skin = new Button("Skin");
    private int istextured=0;
    
    
    /**
     * Texture file
     */
    private URL url;
    private String baseProto = "";
    private String baseHost = "";
    private String baseFile = "";
	
    /**
     * Texture color
     */
    private Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    private Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    private Color3f red = new Color3f(0.7f, .15f, .15f);
    
	public BranchGroup createSceneGraph() {

        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();
        
        // Add the Slider transform to the Transform Group
        sliderXform = new Transform3D();
        sliderVector = new Vector3f();
        sliderTrans = new TransformGroup(sliderXform);
        sliderTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRoot.addChild(sliderTrans);
        
        
        // Create a transform group, so we can manipulate the object
        TransformGroup trans = new TransformGroup();              
        trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        sliderTrans.addChild(trans);
        
        // create a behavior to allow the user to move the object with the mouse
        MouseRotate mr = new MouseRotate();
        
        // tell the behavior which transform Group it is operating on
        mr.setTransformGroup(trans);
        
        // create the bounds for rotate behavior (centered at the origin) 
        BoundingSphere bounds = new BoundingSphere(new Point3d(10.0,10.0,10.0), 300.0);
        mr.setSchedulingBounds(bounds);
        
        // add the Rotate Behavior to the root.(not the transformGroup
        objRoot.addChild(mr);
        
        
        // since the transfom in the transformGroup will be changing when we rotate the object
        // we need to explicitly allow the transform to be read, and written.
        trans.setCapability(trans.ALLOW_TRANSFORM_READ);
        trans.setCapability(trans.ALLOW_TRANSFORM_WRITE);
        
        
        /*try{
            url = new URL("F:\\"+"Daisy.jpg");
        }catch (java.net.MalformedURLException e){e.printStackTrace();;System.exit(-1);}*/
        
        
        // use a texture loader to get the texture file.
        //Texture tex = new TextureLoader(url, this).getTexture();
        
        //TextureLoader loader = new TextureLoader(texturepath,"RPG", new Container());
        
        texture = new TextureLoader(texturepath, this).getTexture();
        //Texture texture = loader.getTexture();
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));
        texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        // Create a new Appearance
        appearance = new Appearance();
        
        // Specify that we ARE using a texture
        appearance.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);
        if (istextured == 0)
        {
        	appearance.setTexture(texture);
        	istextured =1;
        }else {
        	appearance.setTexture(null);
        	istextured = 0;
        }
        appearance.setTextureAttributes(texAttr);
        //appearance.setMaterial(new Material(red, black, red, black, 1.0f));
        //int primflags = Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;
        
        Scene s = readScene(objpath);
        
        BranchGroup scenegroup = s.getSceneGroup();
        
        
        
        
        //appearance.setTexture(null);
        
        // Create the polygon attributes to display only the wireframe
        pa = new PolygonAttributes();
        pa.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
        pa.setPolygonMode(pa.POLYGON_FILL);
        pa.setCullFace(pa.CULL_NONE);
        
        // set the polygon attributes
        Shape3D Poly = (Shape3D)scenegroup.getChild(0);
        Poly.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        appearance.setPolygonAttributes(pa);
        Poly.setAppearance(appearance);
        
        // Add it back to the transform group
        trans.addChild(scenegroup);
         
        // Have Java 3D perform optimizations on this scene graph.
        // Create a red light that shines for 100m from the origin

        Color3f light1Color = new Color3f(0.5f, 0.5f, 0.5f);
        Vector3f light1Direction = new Vector3f(2.0f, -5.0f, -8.0f);
        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        objRoot.addChild(light1);
        
        //Compile the objRoot
        objRoot.compile();

        return objRoot;
    }

	public Scene readScene(String filename) {
		
		//Try to Load the file catch exceptions
        Scene s = null;
        try {
            s = objfile.load(filename);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (IncorrectFormatException ex) {
        	ex.printStackTrace();
            System.exit(1);
        } catch (ParsingErrorException ex) {
        	ex.printStackTrace();
            System.exit(1);
        }
        return s;
	}

	public ReadObj() {
    }
	
	
	public void adjustmentValueChanged(AdjustmentEvent e) {
        sliderVector.set((float)sb2.getValue()/20.0f, (float)sb.getValue()/20.0f, (float)sb3.getValue()/10.0f);
        sliderXform.setTranslation(sliderVector);
        sliderTrans.setTransform(sliderXform);
    }
	
	
	public void init() {
		
		// Initialize the 3D Canvas and the simple universe first
		
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D c = new Canvas3D(config);
        add("Center", c);
        // Add the vertical scroll bar to the east to addjust the
        // object position on the Y direction (Vertical)
        
        sb = new Scrollbar(Scrollbar.VERTICAL, 0, 1, -10, 10);
        sb.addAdjustmentListener(this);
        add("East", sb);
        
        
        // Add the vertical scroll bar to the south to addjust the
        // object position on the X direction (Horizontal).
        sb2 = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, -10, 10);
        sb2.addAdjustmentListener(this);
        add("South", sb2);
        
        
        // Add the vertical scroll bar to the north to addjust the
        // object scale on the depth direction.
        sb3 = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, -10, 10);
        sb3.addAdjustmentListener(this);
        add("North", sb3);
        
        
        // Add three button in a panel to swtich the display mode
        // "points" to display the point cloud
        // "lines" to display the lines
        // "mesh" to display the object covered with mesh
        //p =new Panel();
        p.add(points);
        add("West",p);
        points.addActionListener(this);
        p.add(lines);
        add("West",p);
        lines.addActionListener(this);
        p.add(mesh);
        add("West",p);
        mesh.addActionListener(this);
        p.add(nonSkin);
        add("West",p);
        nonSkin.addActionListener(this);
        p.add(skin);
        add("West",p);
        skin.addActionListener(this);

        
        // Create a simple scene and attach it to the virtual universe
        BranchGroup template = createSceneGraph();
        

        u = new SimpleUniverse(c);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();

        // add the objects to the universe
        u.addBranchGraph(template);
        
        
    }
	
	public void destroy() {
	    u.removeAllLocales();
	}
	
	/*
     * The following allows the code to be run as an application
     * as well as an applet
     */
	public static void main(String[] args) {
    	System.out.print("Enter obj address: ");
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	try {
    		objpath = br.readLine();
    	} catch (IOException e) {
    		e.printStackTrace();
    		System.exit(1);
    	}
    	System.out.println("The obj file address is: " + objpath);
    	System.out.print("Enter texture address: ");
    	BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
    	try {
    		texturepath = br2.readLine();
    	} catch (IOException e) {
    		e.printStackTrace();
    		System.exit(1);
    	}
    	System.out.println("The texture file address is: " + objpath);
        new MainFrame(new ReadObj(), 512, 512);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == points)
		{   
		    pa.setPolygonMode(pa.POLYGON_POINT);
		    
		}
		else if (e.getSource() == lines)
		{
		    pa.setPolygonMode(pa.POLYGON_LINE);
		}
		else if (e.getSource() == mesh)
		{
			pa.setPolygonMode(pa.POLYGON_FILL);
		}
		else if (e.getSource() == nonSkin) {
			if (istextured ==1){
				appearance.setTexture(null);
				istextured = 0;
			}
		}
		else if (e.getSource() == skin) {
			if (istextured == 0) {
				appearance.setTexture(texture);
				istextured = 1;
			}
		}
	}
	
}
