import java.applet.Applet;  
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
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
import javax.media.j3d.TransformGroup;
import javax.print.DocFlavor.URL;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class ReadObj extends Applet {

	
	public String filename;

	public SimpleUniverse u = null;
    
	public BranchGroup branchGroup = null;
	
	public BranchGroup createSceneGraph() {

        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();
 
        TransformGroup trans = new TransformGroup(); 
        
        Scene s = readScene("C:\\Users\\Lingbo\\workspace\\ObjProcessing\\res\\Bunny-499.obj");
        
        BranchGroup shape = s.getSceneGroup();
        
        trans.setCapability(trans.ALLOW_TRANSFORM_READ);
        trans.setCapability(trans.ALLOW_TRANSFORM_WRITE);
        
        trans.addChild(shape);
         
        objRoot.addChild(trans);
        // Have Java 3D perform optimizations on this scene graph.
        
        // Create a red light that shines for 100m from the origin

        Color3f light1Color = new Color3f(1.8f, 0.1f, 0.1f);

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 200.0);

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

	public ReadObj() {
	}
	
	public void init() {
		
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D c = new Canvas3D(config);
        add("Center", c);

        // Create a simple scene and attach it to the virtual universe
        BranchGroup template = createSceneGraph();
        

        u = new SimpleUniverse(c);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();

        // add the objects to the universe
        u.addBranchGraph(template);
        
        
    }
	
}