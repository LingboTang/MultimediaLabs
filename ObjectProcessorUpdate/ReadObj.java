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
import javax.swing.JPanel;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;


// At first create 3 scenegroup move it let 2 untransperent when click the 3D mode
// Remove the Textured one and then display the 2.
// For specific comments, just look at my lab 3.

public class ReadObj extends Applet implements AdjustmentListener, ActionListener {

	
	/**
	 * Make it serializable
	 */
	private static final long serialVersionUID = 1L;

	private SimpleUniverse u = null;
	private static String objpath="";
	private static String texturepath ="";
	private ObjectFile objfile = new ObjectFile(ObjectFile.RESIZE);
	private PolygonAttributes pa;
	private Scene s = null;
	private BranchGroup scenegroup;
	private TransformGroup trans = new TransformGroup();
	private Texture texture;
	private TextureAttributes texAttr;
	private Appearance appearance,appearance2;
	private BranchGroup template2,template;
	private static TransparencyAttributes TA;
	
	private Scrollbar   sb;
    private Scrollbar	sb2;
    private Scrollbar	sb3;
    private Transform3D trans3d,trans3d2;
    private Shape3D Poly;
    private Vector3f    sliderVector;
    private TransformGroup sliderTrans,sliderTrans2;
    private Panel p = new Panel();
    private Button points = new Button("Points");
    private Button lines = new Button("Line");
    private Button mesh = new Button("Mesh");
    private Button skin = new Button("Skin");
    private Button HighDmode = new Button("3DMode");
    private int is3D = 0;
	
    private Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    private Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    private Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
    private Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
    private ColoringAttributes redT = new ColoringAttributes(red,ColoringAttributes.NICEST);
    private ColoringAttributes blueT = new ColoringAttributes(blue,ColoringAttributes.NICEST);
    private static float x = 0.0f, y=0.0f, z=0.0f;
    
    
    
    public ReadObj() {
		
    }
    
	public BranchGroup createSceneGraph(TransformGroup Ttrans, Transform3D HD, Appearance ap) {

		// Initial the BranchGroup
        BranchGroup objRoot = new BranchGroup();
        objRoot.setCapability(BranchGroup.ALLOW_DETACH);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        
        
        // Add basic transform group
        
        // Add Rotate mouse Group
        MouseRotate mr = new MouseRotate();
        mr.setTransformGroup(Ttrans);
        BoundingSphere bounds = new BoundingSphere(new Point3d(10.0,10.0,10.0), 300.0);
        mr.setSchedulingBounds(bounds);
        objRoot.addChild(mr);

        // Add Scene Branchgroup
        s = readScene(objpath);
        scenegroup = s.getSceneGroup();
        //Scene s2 = readScene(objpath);
        //BranchGroup scenegroup2 = s2.getSceneGroup();
        
        // Add Writbale polygon attributes 
        Poly = (Shape3D)scenegroup.getChild(0);
        Poly.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        ap.setPolygonAttributes(pa);
        Poly.setAppearance(ap);
        
        Ttrans.addChild(scenegroup);
        
        // Add Light
        Color3f light1Color = new Color3f(0.5f, 0.5f, 0.5f);
        Vector3f light1Direction = new Vector3f(1.0f, -2.0f, -3.0f);
        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        objRoot.addChild(light1);
        
        objRoot.addChild(Ttrans);
        //Compile the objRoot
        objRoot.compile();

        return objRoot;
    }

	// Read the OBJ file
	public Scene readScene(String filename) {
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

	// Add just the coordinate translation to the X,Y and Z axis
	public void adjustmentValueChanged(AdjustmentEvent e) {
		x = (float)sb2.getValue()/20.0f;y=(float)sb.getValue()/20.0f;z = (float)sb3.getValue()/20.0f;
        trans3d.setTranslation(new Vector3d(x,y,z));
        trans3d2.setTranslation(new Vector3d(x+0.01f,y,z));
        sliderTrans.setTransform(trans3d);
        sliderTrans2.setTransform(trans3d2);
    }
	
	public void init() {
		
		// Initialize the canvas and the universe
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D c = new Canvas3D(config);
        add("Center", c);
        
        // Add Scrollbar to adjust the coordinates
        sb = new Scrollbar(Scrollbar.VERTICAL, 0, 1, -10, 10);
        sb.addAdjustmentListener(this);
        add("East", sb);
        sb2 = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, -10, 10);
        sb2.addAdjustmentListener(this);
        add("South", sb2);
        sb3 = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, -10, 10);
        sb3.addAdjustmentListener(this);
        add("North", sb3);

        // Add button to switch the mode
        p.add(points);
        add("West",p);
        points.addActionListener(this);
        p.add(lines);
        add("West",p);
        lines.addActionListener(this);
        p.add(mesh);
        add("West",p);
        mesh.addActionListener(this);
        p.add(skin);
        add("West",p);
        skin.addActionListener(this);
        p.add(HighDmode);
        add("West",p);
        HighDmode.addActionListener(this);

        // Add texture to the appearance
        texture = new TextureLoader(texturepath, this).getTexture();
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));
        texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        appearance = new Appearance();
        appearance.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
        appearance.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
        appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        //appearance.setTextureAttributes(texAttr);
        
        appearance2 = new Appearance();
        appearance2.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
        appearance2.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
        appearance2.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        appearance2.setColoringAttributes(blueT);
        
        // Add TransparencyAttributes
        TA = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.6f);
        appearance.setTransparencyAttributes(TA);
        appearance2.setTransparencyAttributes(TA);
        
        // Add Polygon attributes
        pa = new PolygonAttributes();
        pa.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
        pa.setCullFace(pa.CULL_NONE);
        
        // Add slider transform group
        
        trans3d = new Transform3D();
        trans3d.setTranslation(new Vector3d(x,y,z));
        trans3d.setScale(0.5);
        sliderTrans = new TransformGroup(trans3d);
        sliderTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        trans3d2 = new Transform3D();
        trans3d2.setTranslation(new Vector3d(x+0.01f,y,z));
        trans3d.setScale(0.5);
        sliderTrans2 = new TransformGroup(trans3d2);
        sliderTrans2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        // Create two different template
        
        template2 = createSceneGraph(sliderTrans2,trans3d2,appearance2);       
        template = createSceneGraph(sliderTrans,trans3d,appearance);
 
        u = new SimpleUniverse(c);
        u.getViewingPlatform().setNominalViewingTransform();
        u.addBranchGraph(template);
        
        
    }
	
	// Destroy the 3D scene when exit
	public void destroy() {
	    u.removeAllLocales();
	}
	
	// Pipeline to pass in the OBJ file path and texture file path
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

	// Switch mode listener
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
		else if (e.getSource() == skin) {
			if (is3D == 1){
    			appearance.setColoringAttributes(null);
    			template.removeChild(template2);
    			is3D = 0;
    		}
			if (appearance.getTexture()!= null){
				appearance.setTexture(null);
			}
			else if (appearance.getTexture() == null) {
				appearance.setTexture(texture);
			}
		}
		else if (e.getSource() == HighDmode) {
			//trans.removeChild(0);
			appearance.setTexture(null);
    		if (is3D == 0){
    			//set transform group2
    			appearance.setColoringAttributes(redT);
    	        trans3d2 = new Transform3D();
    	        trans3d2.setTranslation(new Vector3d(x+0.01f,y,z));
    	        trans3d2.setScale(0.5);
    	        sliderTrans2 = new TransformGroup(trans3d2); 
    	        sliderTrans2.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    	        sliderTrans2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    			template2 = createSceneGraph(sliderTrans2,trans3d2,appearance2);   	
    			template.addChild(template2);
    			is3D = 1;
    		}else if (is3D == 1){
    			appearance.setColoringAttributes(null);    			
    			template.removeChild(template2);
    			is3D = 0;
    		}
		}
	}
}
