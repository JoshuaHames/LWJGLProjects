package engine.items;

import engine.graph.HeightMapMesh;
import engine.graph.NoiseMapMesh;

/**
 * Created by IceEye on 2018-01-31.
 */
public class NoiseTerrain {

    NoiseMapMesh noiseMapMesh;

    private final GameItem[] gameItems;

    public NoiseTerrain(int blocksPerRow, float scale, float minY, float maxY, String textureFile, int textInc) throws Exception {

        gameItems = new GameItem[blocksPerRow * blocksPerRow];

        noiseMapMesh = new NoiseMapMesh(minY, maxY, textureFile, textInc);

        for (int row = 0; row < blocksPerRow; row++) {
            for (int col = 0; col < blocksPerRow; col++) {
                float xDisplacement = (col - ((float) blocksPerRow - 1) / (float) 2) * scale * HeightMapMesh.getXLength();
                float zDisplacement = (row - ((float) blocksPerRow - 1) / (float) 2) * scale * HeightMapMesh.getZLength();

                GameItem terrainBlock = new GameItem(noiseMapMesh.getMesh());
                terrainBlock.setScale(scale);
                terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
                gameItems[row * blocksPerRow + col] = terrainBlock;
            }
        }

    }
    public GameItem[] getGameItems(){
        return gameItems;
    }

    public NoiseMapMesh getNoiseMapMesh(){
        return noiseMapMesh;
    }

}
