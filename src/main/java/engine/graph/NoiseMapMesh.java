package engine.graph;

import engine.Utils;
import org.joml.SimplexNoise;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IceEye on 2018-01-31.
 */
public class NoiseMapMesh {

    private static final int MAX_COLOR = 255 * 255 * 255;

    private static final float STARTX = -0.5f; //Starting X position for the grid

    private static final float STARTZ = -0.5f; // Starting Z position for the grid

    private final float minY;

    private final float maxY;

    private SimplexNoise noise;

    private final Mesh mesh;

    List<Float> positions;
    List<Integer> indices;

    public NoiseMapMesh(float minY, float maxY, String textureFile, int textInc)throws Exception {
        this.minY = minY;
        this.maxY = maxY;

        noise = new SimplexNoise();

        int Height = 556; //Size of the grid
        int Width = 556;

        Texture texture = new Texture(textureFile);

        float incx = getXLength() / (Width - 1); //The amount to step each vertice
        float incz = getZLength() / (Height - 1);

        positions = new ArrayList(); // Creating Arrays and Lists for position data
        List<Float> textCoords = new ArrayList();
        indices = new ArrayList();

        for(int row = 0; row < Height; row++) {
            for (int col = 0; col < Width; col++) {
                // Create vertex for current position
                positions.add(STARTX + col * incx); // x
                positions.add(getHeight(col, row, Width, noise)); //y
                positions.add(STARTZ + row * incz); //z

                // Set texture coordinates
                textCoords.add((float) textInc * (float) col / (float) Width);
                textCoords.add((float) textInc * (float) row / (float) Height);

                // Create indices
                if (col < Width - 1 && row < Height - 1) {
                    int leftTop = row * Width + col;
                    int leftBottom = (row + 1) * Width + col;
                    int rightBottom = (row + 1) * Width + col + 1;
                    int rightTop = row * Width + col + 1;

                    indices.add(leftTop);
                    indices.add(leftBottom);
                    indices.add(rightTop);

                    indices.add(rightTop);
                    indices.add(leftBottom);
                    indices.add(rightBottom);
                }
            }
        }

        float[] posArr = Utils.listToArray(positions);
        int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
        float[] textCoordsArr = Utils.listToArray(textCoords);
        float[] normalsArr = calcNormals(posArr, Width, Height);
        this.mesh = new Mesh(posArr, textCoordsArr, normalsArr, indicesArr);
        Material material = new Material(texture, 0.0f);
        mesh.setMaterial(material);
    }

    public static float getXLength() {
        return Math.abs(-STARTX * 2);
    }

    public static float getZLength() {
        return Math.abs(-STARTZ * 2);
    }

    public float getHeight(int col, int row, int Width, SimplexNoise noise){

        float outHeight = noise.noise(col / getXLength() / 248, row / getXLength() / 248);

        return (outHeight) / 12;

    }

    private float[] calcNormals(float[] posArr, int width, int height) {
        Vector3f v0 = new Vector3f();
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f v3 = new Vector3f();
        Vector3f v4 = new Vector3f();
        Vector3f v12 = new Vector3f();
        Vector3f v23 = new Vector3f();
        Vector3f v34 = new Vector3f();
        Vector3f v41 = new Vector3f();
        List<Float> normals = new ArrayList<>();
        Vector3f normal = new Vector3f();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (row > 0 && row < height - 1 && col > 0 && col < width - 1) {
                    int i0 = row * width * 3 + col * 3;
                    v0.x = posArr[i0];
                    v0.y = posArr[i0 + 1];
                    v0.z = posArr[i0 + 2];

                    int i1 = row * width * 3 + (col - 1) * 3;
                    v1.x = posArr[i1];
                    v1.y = posArr[i1 + 1];
                    v1.z = posArr[i1 + 2];
                    v1 = v1.sub(v0);

                    int i2 = (row + 1) * width * 3 + col * 3;
                    v2.x = posArr[i2];
                    v2.y = posArr[i2 + 1];
                    v2.z = posArr[i2 + 2];
                    v2 = v2.sub(v0);

                    int i3 = (row) * width * 3 + (col + 1) * 3;
                    v3.x = posArr[i3];
                    v3.y = posArr[i3 + 1];
                    v3.z = posArr[i3 + 2];
                    v3 = v3.sub(v0);

                    int i4 = (row - 1) * width * 3 + col * 3;
                    v4.x = posArr[i4];
                    v4.y = posArr[i4 + 1];
                    v4.z = posArr[i4 + 2];
                    v4 = v4.sub(v0);

                    v1.cross(v2, v12);
                    v12.normalize();

                    v2.cross(v3, v23);
                    v23.normalize();

                    v3.cross(v4, v34);
                    v34.normalize();

                    v4.cross(v1, v41);
                    v41.normalize();

                    normal = v12.add(v23).add(v34).add(v41);
                    normal.normalize();
                } else {
                    normal.x = 0;
                    normal.y = 1;
                    normal.z = 0;
                }
                normal.normalize();
                normals.add(normal.x);
                normals.add(normal.y);
                normals.add(normal.z);
            }
        }
        return Utils.listToArray(normals);
    }


    public List<Float> getPositions(){
        return positions;
    }

    public List<Integer> getIndices(){
        return indices;
    }

    public Mesh getMesh() {
        return mesh;
    }

}
