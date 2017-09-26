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

    boolean hasSecondPassed;
    boolean checkFPS = false;
    boolean doCheck = true;
    boolean doTimeStamp = true;
    double startTime;
    double newTime;
    int FPScount = 0;

    int Width;
    int Length;
    int Height;

    int numBlocks;
    int Step;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
        perlin = new ImprovedNoise();
        simplex = new SimplexNoise();

        Width = 50;
        Length = 50;
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

        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("/textures/grassblock.png");
        Material material = new Material(texture, reflectance);

        mesh.setMaterial(material);
        GameItem gameItem;


        for (int Y = Height; Y > 0; Y--){
            for (int X = Width; X > 0; X--){
                for (int Z = Length; Z > 0; Z--){

                    gameItem = new GameItem(mesh);
                    gameItem.setScale(0.5f);
                    gameItem.setPosition(X, Y, Z);
                    gameItems[Step] = gameItem;

                    Step--;
                }
            }
        }

        sceneLight = new SceneLight();

        // Ambient Light
        ambiantLight = (new Vector3f(0.2f, 0.2f, 0.2f));
        sceneLight.setAmbientLight(ambiantLight);


        //Directional Light
        Vector3f lightPosition = new Vector3f(-1, 1, -1);
        float lightIntensity = 1.00f;
        sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(1.1f, 1, 1), lightPosition, lightIntensity));

        // Create HUD

        // Point Light
        lightPosition = new Vector3f(0, 0, 1);
        lightIntensity = 1f;
        PointLight pointLight = new PointLight(new Vector3f(1.1f, 1, 1), lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        sceneLight.setPointLightList(new PointLight[]{pointLight});

        hud = new Hud(numBlocks + " Blocks");

    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)){
                cameraInc.z = -1 * 5;
            } else {
                cameraInc.z = -1;
            }
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
    }

    @Override

    public void update(float interval, MouseInput mouseInput) {

        //FPSsetup();


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

        //FPSCalc();

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

    private void FPSsetup() {
        if (checkFPS == true && doTimeStamp == true){
            startTime = System.nanoTime() / 1000_000_000.0;
            doTimeStamp = false;
            hasSecondPassed = false;
        }
    }

    private void FPSCalc() {

        if (checkFPS == true && hasSecondPassed == false) {
            newTime = System.nanoTime() / 1000_000_000.0;

            if ((newTime - startTime) >= 1){
                hasSecondPassed = true;
                doTimeStamp = true;
                hud.setStatusText("FPS: " + FPScount);
                FPScount = 0;
            } else {
                FPScount += 1;
            }
        }

        if (checkFPS == false && doCheck == true) {
            checkFPS = true;
        }
    }

}