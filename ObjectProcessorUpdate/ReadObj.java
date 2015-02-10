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
// Remove the Textured one and then display the 2. This is another way to sovle this problem


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
	private BranchGroup scenegroup;
	private TransformGroup trans = new TransformGroup();
	private Texture texture;
	private TextureAttributes texAttr;
	private Appearance appearance,appearance2;
	private BranchGroup template2,template;
	private static TransparencyAttributes TA;
	
	

	/**
	 * UI part
	 */
	private Scrollbar   sb;
	private Scrollbar	sb2;
	private Scrollbar	sb3;
	private Transform3D transvec,transvec2;
	private Shape3D Poly;
	private Vector3f    sliderVector;
	private TransformGroup sliderTrans,sliderTrans2;
	private Panel p = new Panel();
	private Button points = new Button("Points");
	private Button lines = new Button("Line");
	private Button mesh = new Button("Mesh");
	private Button skin = new Button("Skin");
	private Button HighDmode = new Button("3DMode");
	private int HDmode_flag = 0;
    
    /**
     * Texture color and initial position
     */
    
    private Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    private Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    private Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
    private Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
    private ColoringAttributes redT = new ColoringAttributes(red,ColoringAttributes.NICEST);
    private ColoringAttributes blueT = new ColoringAttributes(blue,ColoringAttributes.NICEST);
    private static float x = 0.0f, y=0.0f, z=0.0f;
    
    
    /**
     * 
     */
    
    public ReadObj() {
		
    }
    
    
    /**
     * Why I need to pass in the transform group and appearance into the createSceneGraph
     * Function? Because I actually found that we can't set the new appearance and new transform
     * group to the compiled branchgroup. However, if I change them outside the creator and pass 
     * them in the creator. It will work.
     * 
     * @param Ttrans
     * @param HD
     * @param ap
     * @return
     */
    
    
	public BranchGroup createSceneGraph(TransformGroup Ttrans, Transform3D HD, Appearance ap) {

		// Initial the BranchGroup
        BranchGroup objRoot = new BranchGroup();
        objRoot.setCapability(BranchGroup.ALLOW_DETACH);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        
        // Add Rotate mouse Group
        MouseRotate mr = new MouseRotate();
        mr.setTransformGroup(Ttrans);
        BoundingSphere bounds = new BoundingSphere(new Point3d(10.0,10.0,10.0), 300.0);
        mr.setSchedulingBounds(bounds);
        objRoot.addChild(mr);

        // Add Scene Branchgroup
        s = readScene(objpath);
        scenegroup = s.getSceneGroup();
        
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
	/**
	 * Take the object file name and load it
	 * 
	 * @param filename
	 * @return
	 */
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
	/**
	 * 
	 * Scrollbar listener to get the vector transform value
	 * sb2 is to adjust the x position (scene width)
	 * sb is to adjust the y position (scene height)
	 * sb3 is to adjust the z position (scene depth)
	 * They will be calculated by the Vector3d and stored in
	 * the translated vector.
	 * 
	 * @param e
	 * @return
	 */
	public void adjustmentValueChanged(AdjustmentEvent e) {
		x = (float)sb2.getValue()/20.0f;y=(float)sb.getValue()/20.0f;z = (float)sb3.getValue()/20.0f;
        transvec.setTranslation(new Vector3d(x,y,z));
        transvec2.setTranslation(new Vector3d(x+0.01f,y,z));
        sliderTrans.setTransform(transvec);
        sliderTrans2.setTransform(transvec2);
    }
	
	/**
	 * 
	 */
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
        // "points" to display the point cloud
        // "lines" to display the lines
        // "mesh" to display the object covered with mesh
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
        // I rearranged them to make them seems more formated.
        appearance = new Appearance();
        appearance.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
        appearance.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
        appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        
        
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
        
        transvec = new Transform3D();
        transvec.setTranslation(new Vector3d(x,y,z));
        transvec.setScale(0.5);
        sliderTrans = new TransformGroup(transvec);
        sliderTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        transvec2 = new Transform3D();
        transvec2.setTranslation(new Vector3d(x+0.01f,y,z));
        transvec.setScale(0.5);
        sliderTrans2 = new TransformGroup(transvec2);
        sliderTrans2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        // Create two different template
        
        template2 = createSceneGraph(sliderTrans2,transvec2,appearance2);       
        template = createSceneGraph(sliderTrans,transvec,appearance);
 
        // Set the universe
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
	/**
	 * This is the Buttom listener that will handle the switch case
	 * 
	 * @param e
     * @return
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// For Change display Line, Point and Fill settings
		// we just need to change the pa_flag
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
			if (appearance.getTexture()!= null){
				appearance.setTexture(null);
			}
			else if (appearance.getTexture() == null) {
				appearance.setTexture(texture);
			}
			// We need to erase the whole 3D mode
			// However it will cause the unsythesized branch group
			// problem,. but I don't have time to solve it.
			if (HDmode_flag == 1){
    			appearance.setColoringAttributes(null);
    			template.removeChild(template2);
    			HDmode_flag = 0;
    		}
		}
		else if (e.getSource() == HighDmode) {
			// For 3D ones, erase the texture first
			appearance.setTexture(null);			
    		if (HDmode_flag == 0){
    			//set transform group2
    			appearance.setColoringAttributes(redT);
    	        transvec2 = new Transform3D();
    	        transvec2.setTranslation(new Vector3d(x+0.01f,y,z));
    	        transvec2.setScale(0.5);
    	        sliderTrans2 = new TransformGroup(transvec2); 
    	        sliderTrans2.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    	        sliderTrans2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    			template2 = createSceneGraph(sliderTrans2,transvec2,appearance2);
    			// Add the second template the first one to make it systhesized
    			template.addChild(template2);
    			HDmode_flag = 1;
    		}else if (HDmode_flag == 1){
    			// If we want to set them back just kill them
    			appearance.setColoringAttributes(null);    			
    			template.removeChild(template2);
    			HDmode_flag = 0;
    		}
		}
	}
}
