import java.applet.Applet;  
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.FileNotFoundException;
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

public class ReadObj extends Applet implements AdjustmentListener {

	
	public String filename;

	public SimpleUniverse u = null;
    
	public BranchGroup branchGroup = null;
	
	Scrollbar   sb;
    Scrollbar	sb2;
    Scrollbar	sb3;
    
    Transform3D sliderXform;
    Vector3f    sliderVector;
    
    TransformGroup sliderTrans;
	
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
        
        Scene s = readScene("C:\\Users\\Lingbo\\workspace\\ObjProcessing\\res\\Cow-500.obj");
        
        BranchGroup shape = s.getSceneGroup();
        
        trans.addChild(shape);
         
        
        // Have Java 3D perform optimizations on this scene graph.
        
        // Create a red light that shines for 100m from the origin

        Color3f light1Color = new Color3f(2.0f, 0.5f, 0.5f);

        Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);

        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);

        light1.setInfluencingBounds(bounds);

        objRoot.addChild(light1);
        
        objRoot.compile();

        return objRoot;
    }

	public Scene readScene(String filename) {
		
        ObjectFile f = new ObjectFile();
        Scene s = null;
        
        try {
            s = f.load(filename);
            //System.out.println(s.getSceneGroup() + "");
        } catch (FileNotFoundException ex) {
            
        } catch (IncorrectFormatException ex) {
            
        } catch (ParsingErrorException ex) {
            
        }
        return s;
	}

	
	
	public void adjustmentValueChanged(AdjustmentEvent e) {
        sliderVector.set((float)sb2.getValue()/10.0f, (float)sb.getValue()/10.0f, (float)sb3.getValue()/10.0f);
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
        new MainFrame(new ViewPyramid(), 256, 256);
    }
}
