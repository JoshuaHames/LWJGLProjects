package Game;

/**
 * Created by IceEye on 2017-03-02.
 */

import Engine.IGameLogic;
import Engine.Window;
import Engine.graph.Mesh;
import Engine.graph.OBJLoader;
import Engine.GameItem;
import org.joml.Vector3f;
import Engine.graph.Texture;
import Engine.MouseInput;
import Engine.graph.Camera;
import org.joml.*;
import Engine.graph.*;
import Engine.SceneLight;
import java.lang.Math;
import Engine.Timer;
import Engine.graph.ImprovedNoise;
import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private SceneLight sceneLight;

    private Hud hud;

    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.05f;

    private float spotAngle = 0;

    private float spotInc = 1;

    private ImprovedNoise perlin;

    private SimplexNoise simplex;

    GameItem[] gameItems;

    Vector3f ambiantLight;

    Vector4f color;

    float weight;
    float offset;

    int moveSpeed = 1;

    int Width;
    int Length;
    int Height;

    int numBlocks;
    int Step;

    Mesh mesh;
    Texture texture;
    Material material;
    GameItem gameItem;

    boolean floor = true;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(10.0f, 10.0f, 10.0f);
        lightAngle = -90;
        perlin = new ImprovedNoise();
        simplex = new SimplexNoise();

        Width = 100;
        Length = 100;
        Height = 1;

        numBlocks = Width*Length*Height;

        Step = numBlocks - 1;

        gameItems = new GameItem[numBlocks];


    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);


        float reflectance = 1f;
        //Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        //Material material = new Material(new Vector3f(0.2f, 0.5f, 0.5f), reflectance);

        mesh = OBJLoader.loadMesh("/models/cube.obj");
        texture = new Texture("/textures/grassblock.png");
        color = new Vector4f(1f,1f,1f,1f);
        material = new Material(texture);
        mesh.setMaterial(material);

        offset = 0;
        weight = 0;

        genBlocks(gameItem);

        sceneLight = new SceneLight();

        // Ambient Light
        ambiantLight = (new Vector3f(0.05f, 0.05f, 0.05f));
        sceneLight.setAmbientLight(ambiantLight);


        //Directional Light
        Vector3f lightPosition = new Vector3f(-1, 1, -1);
        float lightIntensity = 0.70f;
        sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(1.1f, 1, 1), lightPosition, lightIntensity));

        // Create HUD

        // Point Light
        lightPosition = new Vector3f(0, 0, 1);
        lightIntensity = 1f;
        PointLight pointLight = new PointLight(new Vector3f(1.1f, 1, 1), lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        sceneLight.setPointLightList(new PointLight[]{pointLight});

        hud = new Hud(("Offset: " + offset + " Weight: " + weight + " Floor: " + floor));

    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);

        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            moveSpeed = 5;

        } else {
            moveSpeed = 1;
        }

        if (window.isKeyPressed(GLFW_KEY_W)) {
                cameraInc.z = -moveSpeed;

        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = moveSpeed;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -moveSpeed;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = moveSpeed;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -moveSpeed;

        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = moveSpeed;
        } else if (window.isKeyPressed(GLFW_KEY_O)) {
            offset += 0.0001;
            hud.setStatusText(("Offset: " + offset + " Weight: " + weight + " Floor: " + floor));
            genBlocks(gameItem);
        } else if (window.isKeyPressed(GLFW_KEY_P)) {
            weight += 0.03;
            hud.setStatusText(("Offset: " + offset + " Weight: " + weight + " Floor: " + floor));
            genBlocks(gameItem);
        } else if (window.isKeyPressed(GLFW_KEY_L)) {
            offset -= 0.0001;
            hud.setStatusText(("Offset: " + offset + " Weight: " + weight + " Floor: " + floor));
            genBlocks(gameItem);
        } else if (window.isKeyPressed(GLFW_KEY_SEMICOLON)) {
            weight -= 0.03;
            hud.setStatusText(("Offset: " + offset + " Weight: " + weight + " Floor: " + floor));
            genBlocks(gameItem);
        } else if (window.isKeyPressed(GLFW_KEY_F)) {
            if (floor == false) {
                floor = true;
            } else {
                floor = false;
            }

            hud.setStatusText(("Offset: " + offset + " Weight: " + weight + " Floor: " + floor));
        } else if (window.isKeyPressed(GLFW_KEY_R)) {
            floor = true;
            weight = 0;
            offset = 0;
            genBlocks(gameItem);
        }
    }

    @Override

    public void update(float interval, MouseInput mouseInput) {

        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

            // Update HUD compass
            hud.rotateCompass(camera.getRotation().y);
        }

        PointLight[] pointLightList = sceneLight.getPointLightList();

        pointLightList[0].setPosition(camera.getPosition());

    }

    @Override
    public void render(Window window) {
        hud.updateSize(window);
        renderer.render(window, camera, gameItems, sceneLight, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
        hud.cleanup();
    }

    public void genBlocks(GameItem gameItem){

        Step = numBlocks - 1;
        for (int Z = 0; Z < Height; Z++){
            for (int X =0; X <  Width; X++){
                for (int Y = 0; Y < Length; Y++){
                    gameItem = new GameItem(mesh);
                    gameItem.setScale(0.5f);

                    if (floor == true) {
                        gameItem.setPosition(Y, Z+(float)Math.floor(simplex.noise(X * offset,Y * offset) * weight), X);
                    } else {
                        gameItem.setPosition(Y, Z+(simplex.noise(X * offset,Y * offset) * weight), X);
                    }
                    gameItems[Step] = gameItem;

                    Step--;
                }
            }
        }
    }
}