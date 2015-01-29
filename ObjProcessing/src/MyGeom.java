import javax.media.j3d.*;
import javax.vecmath.*;

public class MyGeom extends Shape3D {
    // create the shape With default appearance
    public MyGeom(float p) {
        this.setGeometry(texa(p));
    }//end mygeom constructor
    
    
    // create the shape with a specific appearance
    public MyGeom(float p,Appearance ap) {
        this.setGeometry(texa(p));
        this.setAppearance(ap);
    }//end mygeom constructor
    
    
    // create the points that make this cube
    private Point3f[] getPoints(float p) {
        int N = 8;
        Point3f corners[] = new Point3f[N];
        
        corners[0] = new Point3f(-p,-p,p);
        corners[1] = new Point3f(p,-p,p);
        corners[2] = new Point3f(-p,p,p);
        corners[3] = new Point3f(p,p,p);
        corners[4] = new Point3f(-p,-p,-p);
        corners[5] = new Point3f(p,-p,-p);
        corners[6] = new Point3f(-p,p,-p);
        corners[7] = new Point3f(p,p,-p);
        
        return corners;
    }
    
    //////////////////////////////////////////////////////////////////
    //
    // Triangle array WITH TEXTURE
    //
    //////////////////////////////////////////////////////////////////
    private Geometry texa(float p) {
        
        // use a triangle array to represent the geometry
        TriangleArray tta;
        
        int N = 8;
        int i;
        
        Point3f corners[] = getPoints(p);
        
        int numPoints = 36; // = 2*3*6
        Point3f triangles[] = new Point3f[numPoints];
        //
        //### Create the triangles that form the shape
        //
        // define triangles around the bottom
        triangles[0] = corners[0];
        triangles[1] = corners[1];
        triangles[2] = corners[3];
        
        triangles[3] = corners[1];
        triangles[4] = corners[5];
        triangles[5] = corners[7];
        
        triangles[6] = corners[5];
        triangles[7] = corners[4];
        triangles[8] = corners[6];
        
        triangles[9] = corners[4];
        triangles[10] = corners[0];
        triangles[11] = corners[2];
        
        // define triangles around top
        triangles[12] = corners[0];
        triangles[13] = corners[3];
        triangles[14] = corners[2];
        
        triangles[15] = corners[1];
        triangles[16] = corners[7];
        triangles[17] = corners[3];
        
        triangles[18] = corners[5];
        triangles[19] = corners[6];
        triangles[20] = corners[7];
        
        triangles[21] = corners[4];
        triangles[22] = corners[2];
        triangles[23] = corners[6];
        
        // define top and bottom
        triangles[24] = corners[2];
        triangles[25] = corners[3];
        triangles[26] = corners[7];
        
        triangles[27] = corners[2];
        triangles[28] = corners[7];
        triangles[29] = corners[6];
        
        triangles[30] = corners[4];
        triangles[31] = corners[1];
        triangles[32] = corners[0];
        
        triangles[33] = corners[4];
        triangles[34] = corners[5];
        triangles[35] = corners[1];
        
        //
        //### Create colors for each point
        //
        Color3f ctriangles[] = new Color3f[numPoints];
        Color3f ccorners[] = new Color3f[N];
        ccorners[0] = new Color3f(1.0f, 0.0f, 0.0f);
        ccorners[1] = new Color3f(0.0f, 1.0f, 0.0f);
        ccorners[2] = new Color3f(0.0f, 0.0f, 1.0f);
        ccorners[3] = new Color3f(1.0f, 1.0f, 1.0f);
        ccorners[4] = new Color3f(1.0f, 1.0f, 0.0f);
        ccorners[5] = new Color3f(1.0f, 0.0f, 1.0f);
        ccorners[6] = new Color3f(0.0f, 1.0f, 1.0f);
        ccorners[7] = new Color3f(0.7f, 0.4f, 1.0f);
        
        
        ctriangles[0] = ccorners[0];
        ctriangles[1] = ccorners[1];
        ctriangles[2] = ccorners[3];
        
        ctriangles[3] = ccorners[1];
        ctriangles[4] = ccorners[5];
        ctriangles[5] = ccorners[7];
        
        ctriangles[6] = ccorners[5];
        ctriangles[7] = ccorners[4];
        ctriangles[8] = ccorners[6];
        
        ctriangles[9] = ccorners[4];
        ctriangles[10] = ccorners[0];
        ctriangles[11] = ccorners[2];
        
        //////
        
        ctriangles[12] = ccorners[0];
        ctriangles[13] = ccorners[3];
        ctriangles[14] = ccorners[2];
        
        ctriangles[15] = ccorners[1];
        ctriangles[16] = ccorners[7];
        ctriangles[17] = ccorners[3];
        
        ctriangles[18] = ccorners[5];
        ctriangles[19] = ccorners[6];
        ctriangles[20] = ccorners[7];
        
        ctriangles[21] = ccorners[4];
        ctriangles[22] = ccorners[2];
        ctriangles[23] = ccorners[6];
        
        /////
        ctriangles[24] = ccorners[2];
        ctriangles[25] = ccorners[3];
        ctriangles[26] = ccorners[7];
        
        ctriangles[27] = ccorners[2];
        ctriangles[28] = ccorners[7];
        ctriangles[29] = ccorners[6];
        
        ctriangles[30] = ccorners[4];
        ctriangles[31] = ccorners[1];
        ctriangles[32] = ccorners[0];
        
        ctriangles[33] = ccorners[4];
        ctriangles[34] = ccorners[5];
        ctriangles[35] = ccorners[1];
        
        //
        // set up the texture
        //
        TexCoord2f tex[] = new TexCoord2f[numPoints];
        TexCoord2f tc[] = new TexCoord2f[6];
        
        tc[0] = new TexCoord2f(0.0f,0.0f);
        tc[1] = new TexCoord2f(1.0f,0.0f);
        tc[2] = new TexCoord2f(1.0f,1.0f);
        
        tc[3] = new TexCoord2f(0.0f,0.0f);
        tc[4] = new TexCoord2f(1.0f,1.0f);
        tc[5] = new TexCoord2f(0.0f,1.0f);
        
        //
        // whole image on one face
        //
        for (i=0; i<3; i++) {
            // A  - front
            tex[i+0] = tc[i];
            tex[i+12] = tc[i+3];
            // B
            tex[i+3] = tc[i];
            tex[i+15] = tc[i+3];
            // C  - back
            tex[i+6] = tc[i];
            tex[i+18] = tc[i+3];
            // D
            tex[i+9] = tc[i];
            tex[i+21] = tc[i+3];
            // E  - top
            tex[i+24] = tc[i];
            tex[i+27] = tc[i+3];
            // F - bottom
            tex[i+30] = tc[i+3];
            tex[i+33] = tc[i];
        }
        
        //
        // Create the Triangle Array, then set the coordinates, color, and texture info
        //
        tta = new TriangleArray(numPoints,TriangleArray.COORDINATES |
        TriangleArray.TEXTURE_COORDINATE_2 |
        TriangleArray.COLOR_3);
        tta.setCoordinates(0,triangles);
        tta.setTextureCoordinates(0,0,tex);
        tta.setColors(0,ctriangles);
        
        
        return tta;
        
    }//end geom
    
    
}// end myGeom