package engine.items;

import engine.graph.HeightMapMesh;
import org.lwjgl.opengl.GL32;

/**
 * Created by IceEye on 2017-10-27.
 */
public class Terrain {

    HeightMapMesh heightMapMesh;

    private final GameItem[] gameItems;

    public Terrain(int blocksPerRow, float scale, float minY, float maxY, String heightMap, String textureFile, int textInc) throws Exception {

        gameItems = new GameItem[blocksPerRow * blocksPerRow];

        heightMapMesh = new HeightMapMesh(minY, maxY, heightMap, textureFile, textInc);

        for (int row = 0; row < blocksPerRow; row++) {
            for (int col = 0; col < blocksPerRow; col++) {
                float xDisplacement = (col - ((float) blocksPerRow - 1) / (float) 2) * scale * HeightMapMesh.getXLength();
                float zDisplacement = (row - ((float) blocksPerRow - 1) / (float) 2) * scale * HeightMapMesh.getZLength();

                GameItem terrainBlock = new GameItem(heightMapMesh.getMesh());
                terrainBlock.setScale(scale);
                terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
                gameItems[row * blocksPerRow + col] = terrainBlock;
            }
        }

    }
    public GameItem[] getGameItems(){
        return gameItems;
    }

    public HeightMapMesh getHeightMapMesh(){
        return heightMapMesh;
    }

}
