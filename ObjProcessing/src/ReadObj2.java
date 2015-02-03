import java.applet.Applet;   
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GraphicsConfiguration;
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
import java.net.URI;

import javax.swing.JPanel;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.print.DocFlavor.URL;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class ReadObj2 extends Applet implements AdjustmentListener, ActionListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String filename;

	public SimpleUniverse u = null;
    
	public BranchGroup branchGroup = null;
	
	public static String objpath;
	
	public static String displayset;
	
	public PolygonAttributes pa;
	private Scene scene = null;
	
	private ObjectFile objfile = new ObjectFile(ObjectFile.RESIZE);
	
	Scrollbar   sb;
    Scrollbar	sb2;
    Scrollbar	sb3;
    
    Transform3D sliderXform;
    Vector3f    sliderVector;
    
    TransformGroup sliderTrans;
    
    private Button points = new Button("Points");
    private Button lines = new Button("Line");
    private Button mesh = new Button("Mesh");
	
	public BranchGroup createSceneGraph() {

        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();
        
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
        
        Scene s = readScene(objpath);
        
        /*try {  
        	scene = objfile.load(objpath);  
        } catch (FileNotFoundException e) {  
            System.err.println(e);  
            System.exit(1);  
        } catch (ParsingErrorException e) {  
            System.err.println(e);  
            System.exit(1);  
        } catch (IncorrectFormatException e) {  
            System.err.println(e);  
            System.exit(1);  
        }*/        
        
        BranchGroup scenegroup = s.getSceneGroup();
        
        // Create a new Appearance
        Appearance appearance = new Appearance();
        appearance.setTexture(null);
        
        // Create the polygon attributes to display only the wireframe
        pa = new PolygonAttributes();
        pa.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
        pa.setPolygonMode(pa.POLYGON_FILL);
        pa.setCullFace(pa.CULL_NONE);
        // set the polygon attributes
        Shape3D Poly = (Shape3D)scenegroup.getChild(0);
        appearance.setPolygonAttributes(pa);
        Poly.setAppearance(appearance);
        
        trans.addChild(scenegroup);
         
        
        // Have Java 3D perform optimizations on this scene graph.
        
        // Create a red light that shines for 100m from the origin

        Color3f light1Color = new Color3f(0.5f, 0.5f, 0.5f);

        Vector3f light1Direction = new Vector3f(2.0f, -5.0f, -8.0f);

        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);

        light1.setInfluencingBounds(bounds);

        objRoot.addChild(light1);
        
        objRoot.compile();

        return objRoot;
    }

	public Scene readScene(String filename) {
		
        //ObjectFile f = new ObjectFile();
        Scene s = null;
        
        try {
            s = objfile.load(filename);
            //System.out.println(s.getSceneGroup() + "");
        } catch (FileNotFoundException ex) {
            
        } catch (IncorrectFormatException ex) {
            
        } catch (ParsingErrorException ex) {
            
        }
        return s;
	}

	
	
	public void adjustmentValueChanged(AdjustmentEvent e) {
        sliderVector.set((float)sb2.getValue()/20.0f, (float)sb.getValue()/20.0f, (float)sb3.getValue()/5.0f);
        sliderXform.setTranslation(sliderVector);
        sliderTrans.setTransform(sliderXform);
    }
	
	
	public void init() {
		
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D c = new Canvas3D(config);
        add("Center", c);
        
        sb = new Scrollbar(Scrollbar.VERTICAL, 0, 1, -10, 10);
        sb.addAdjustmentListener(this);
        add("East", sb);

        sb2 = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, -10, 10);
        sb2.addAdjustmentListener(this);
        add("South", sb2);
        
        sb3 = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, -10, 10);
        sb3.addAdjustmentListener(this);
        add("North", sb3);
        
        Panel p =new Panel();
        p.add(points);
        add("West",p);
        points.addActionListener(this);
        p.add(lines);
        add("West",p);
        lines.addActionListener(this);
        p.add(mesh);
        add("West",p);
        mesh.addActionListener(this);

        
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
        new MainFrame(new ReadObj(), 256, 256);
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
	}
}
