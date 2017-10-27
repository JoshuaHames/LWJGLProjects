package engine.items;

import engine.graph.OBJLoader;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;

/**
 * Created by IceEye on 2017-10-14.
 * Sometimes I wonder if this is a waste of time
 */
public class SkyBox extends GameItem {

    public SkyBox(String objModel, String textureFile) throws Exception{

        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
        Texture skyBoxtexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0,0,0);
    }
}
