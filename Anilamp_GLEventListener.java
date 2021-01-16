import com.jogamp.opengl.*;
import java.util.*;
import lib.*;
import lib.gmaths.*;
import models.*;
import shapes.*;

/**
 * I declare that this code is my own work.
 * Author: Mite Li (mli115@sheffield.ac.uk)
 */

class Anilamp_GLEventListener implements GLEventListener {
  Anilamp_GLEventListener(Camera camera) {
    this.camera = camera;
  }

  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearDepth(1.0f);
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    gl.glDepthFunc(GL.GL_LEQUAL);
    gl.glFrontFace(GL.GL_CCW);
    gl.glCullFace(GL.GL_BACK);
    gl.glEnable(GL.GL_CULL_FACE);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    initialise(gl);
  }

  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float) width / (float) height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();

    for (Light light : lightList) {
      light.dispose(gl);
    }

    for (Model model : modelList) {
      model.dispose(gl);
    }
  }

  private final Vec3 ROOM_Size = new Vec3(20f, 20f, 20f);
  private final Camera camera;
  private Light lampLight;
  private List<Light> lightList;
  private List<Model> modelList;
  private Mesh cubeMesh, cylinderMesh, sphereMesh, twoTrianglesMesh;
  private Shader cubeShader, twoTrianglesShader;
  private Model floor, wall, wall2;
  private Model topWallpaper, bottomWallpaper, leftWallpaper, rightWallpaper, wall2Wallpaper;
  private Model tableFrame;
  private Model laptopFrame, keyBoard, Screen;
  private Model cube, cylinder, sphere, frustumCone;
  private Model Ear, Tail;
  private Model Frame, outsideScene, whiteBoard, whiteBoardPIC1, whiteBoardPIC2, whiteBoardPIC3;
  private Model base, egg;
  private Room room;
  private Window window;
  private Table table;
  private WhiteBoardFrame WhiteBoardFrame;
  EggShape eggshape;
  Lamp lamp;
  Laptop Laptop;
  boolean lightIsOn = true;
  boolean spotlightIsOn = true;
  boolean LaptopIsOn = true;

  private void initialise(GL3 gl) {

    createMesh(gl);

    Light WorldLight1 = new Light(gl, camera);
    WorldLight1.setPosition(0, ROOM_Size.y, 0);
    Light WorldLight2 = new Light(gl, camera);
    WorldLight2.setPosition(0, ROOM_Size.y, -ROOM_Size.z);

    lampLight = new Spotlight(gl, camera);
    lightList = Arrays.asList(WorldLight1, WorldLight2, lampLight);

    modelFloor(gl);
    modelWall(gl);
    modelWall2(gl);
    modelWallpaper(gl);
    modelWindow(gl);
    modelWhiteBorad(gl);
    modelTable(gl);
    modelLaptop(gl);
    modelLamp(gl);
    modelEggShape(gl);

    modelList = Arrays.asList(floor, wall, wall2, topWallpaper, bottomWallpaper, leftWallpaper,
            rightWallpaper, wall2Wallpaper, tableFrame, laptopFrame, keyBoard, Screen, cube, cylinder, sphere, frustumCone,
            Ear, Tail, Frame, outsideScene, whiteBoard, whiteBoardPIC1, whiteBoardPIC2, whiteBoardPIC3, egg, base);

    room = new Room(ROOM_Size, floor, wall, wall2);
    room.new Wallpaper(topWallpaper, bottomWallpaper, leftWallpaper, rightWallpaper, wall2Wallpaper);
    room.initialise();

    window = new Window(ROOM_Size, outsideScene);
    window.initialise();

    table = new Table(ROOM_Size, tableFrame);
    table.initialise();

    Laptop = new Laptop(ROOM_Size, laptopFrame, sphere, keyBoard, Screen);
    Laptop.initialise();

    WhiteBoardFrame = new WhiteBoardFrame(ROOM_Size, Frame, whiteBoard, whiteBoardPIC1, whiteBoardPIC2, whiteBoardPIC3);
    WhiteBoardFrame.initialise();

    lamp = new Lamp(cube, cylinder, sphere, frustumCone, lampLight, Ear, Tail);
    lamp.initialise();

    eggshape = new EggShape(ROOM_Size, base, egg);
    eggshape.initialise();
  }


  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    for (Light light : lightList) {
      light.render(gl);
    }

    if (Laptop.isAnimatingOnOff) {
      Anilamp.onOroff.setEnabled(false);
    } else {
      Anilamp.onOroff.setEnabled(true);
    }

    if (lamp.isAnimatingRandom || lamp.isAnimatingReset || lamp.isAnimatingMove) {
      Anilamp.random.setEnabled(false);
      Anilamp.reset.setEnabled(false);
      Anilamp.move.setEnabled(false);
    } else {
      Anilamp.random.setEnabled(true);
      Anilamp.reset.setEnabled(true);
      Anilamp.move.setEnabled(true);
    }

    if(eggshape.isAnimatingJump){
      Anilamp.eggJump.setEnabled(false);
    } else {
      Anilamp.eggJump.setEnabled(true);
    }

    room.render(gl);
    table.render(gl);
    WhiteBoardFrame.render(gl);
    lamp.render(gl);
    window.render(gl);
    Laptop.render(gl);
    eggshape.render(gl);
  }

  private void createMesh(GL3 gl) {
    cubeMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    cylinderMesh = new Mesh(gl, Cylinder.vertices.clone(), Cylinder.indices.clone());
    sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    twoTrianglesMesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());

    cubeShader = new Shader(gl, "shaders/vs_cube.txt", "shaders/fs_cube.txt");
    twoTrianglesShader = new Shader(gl, "shaders/vs_tt.txt", "shaders/fs_tt.txt");
  }

  void setIntensity(float intensity) {
    for (Light worldLight : lightList) {
      if (!worldLight.getClass().equals(Spotlight.class)) {
        Material m = worldLight.getMaterial();

        m.setDiffuse(Vec3.multiply(new Vec3(1, 1, 1), intensity));
        m.setSpecular(Vec3.multiply(new Vec3(1, 1, 1), intensity));
        worldLight.setLightColor(1 - (1 - intensity) / 2);
      }
    }
  }

  void setSpotlightOnOrOff() {
    if (spotlightIsOn) {
      lampLight.setSpotlightIntensity(0);
      lampLight.setLightColor(0.5f);
    } else {
      lampLight.setSpotlightIntensity(1);
      lampLight.setLightColor(1);
    }

    spotlightIsOn = !spotlightIsOn;
  }

  void setLaptopOnorOff(){
    LaptopIsOn = !LaptopIsOn;
  }

  private void modelFloor(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/wood_specular.jpg");

    Material material = new Material(
            new Vec3(0, 0, 0),
            new Vec3(0, 0, 0),
            new Vec3(0.3f, 0.3f, 0.3f), 25f);
    floor = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
  }

  private void modelWall2(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/wood_specular.jpg");

    Material material = new Material(
            new Vec3(0, 0, 0),
            new Vec3(0, 0, 0),
            new Vec3(0.3f, 0.3f, 0.3f), 25f);
    wall2 = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
  }

  private void modelWall(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/wood_specular.jpg");

    Material material = new Material(
            new Vec3(0, 0, 0),
            new Vec3(0, 0, 0),
            new Vec3(0.3f, 0.3f, 0.3f), 25f);
    wall = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
  }

  private void modelWallpaper(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/wallpaper.jpg");

    float[] vertices = TwoTriangles.vertices.clone();
    int[] indices = TwoTriangles.indices.clone();

    final float[] topTexCoords = {
            Window.RATIO.x / 2, 1,
            Window.RATIO.x / 2, Window.Y_POS + Window.RATIO.y,
            (1 - Window.RATIO.x) / 2 + Window.RATIO.x, Window.Y_POS + Window.RATIO.y,
            (1 - Window.RATIO.x) / 2 + Window.RATIO.x, 1
    };

    final float[] bottomTexCoords = {
            (1 - Window.RATIO.x) / 2, Window.Y_POS,
            (1 - Window.RATIO.x) / 2, 0,
            (1 - Window.RATIO.x) / 2 + Window.RATIO.x, 0,
            (1 - Window.RATIO.x) / 2 + Window.RATIO.x, Window.Y_POS
    };

    final float[] leftTexCoords = {
            0, 1,
            0, 0,
            (1 - Window.RATIO.x) / 2, 0,
            (1 - Window.RATIO.x) / 2, 1
    };

    final float[] rightTexCoords = {
            (1 - Window.RATIO.x) / 2 + Window.RATIO.x, 1,
            (1 - Window.RATIO.x) / 2 + Window.RATIO.x, 0,
            1, 0,
            1, 1
    };

    final float[] wall2TexCoords = {
            0, 1,
            0, 0,
            1, 0,
            1, 1
    };

    Mesh topMesh = new Mesh(gl, TwoTriangles.setTexCoords(vertices, topTexCoords), indices);
    Mesh bottomMesh = new Mesh(gl, TwoTriangles.setTexCoords(vertices, bottomTexCoords), indices);
    Mesh leftMesh = new Mesh(gl, TwoTriangles.setTexCoords(vertices, leftTexCoords), indices);
    Mesh rightMesh = new Mesh(gl, TwoTriangles.setTexCoords(vertices, rightTexCoords), indices);
    Mesh wall2Mesh = new Mesh(gl, TwoTriangles.setTexCoords(vertices, wall2TexCoords), indices);

    Material material = new Material(
            new Vec3(1f, 1f, 1f),
            new Vec3(1f, 1f, 1f),
            new Vec3(0.0f, 0.0f, 0.0f), 32f);
    topWallpaper = new Model(camera, lightList, twoTrianglesShader, material, topMesh, DIFFUSE);
    bottomWallpaper = new Model(camera, lightList, twoTrianglesShader, material, bottomMesh, DIFFUSE);
    leftWallpaper = new Model(camera, lightList, twoTrianglesShader, material, leftMesh, DIFFUSE);
    rightWallpaper = new Model(camera, lightList, twoTrianglesShader, material, rightMesh, DIFFUSE);
    wall2Wallpaper = new Model(camera, lightList, twoTrianglesShader, material, wall2Mesh, DIFFUSE);
  }

  private void modelWindow(GL3 gl) {

    final int[] SCENE = TextureLibrary.loadTexture(gl, "textures/scene.jpg");


    Material material = new Material(
            new Vec3(1, 1, 1),
            new Vec3(1, 1, 1),
            new Vec3(0.3f, 0.3f, 0.3f), 30f);

    Shader shader = new Shader(gl, "shaders/vs_scene.txt", "shaders/fs_scene.txt");
    outsideScene = new Model(camera, lightList, shader, material, twoTrianglesMesh, SCENE);

  }

  private void modelWhiteBorad(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/wood_specular.jpg");
    final int[] WHITEBOARD = TextureLibrary.loadTexture(gl, "textures/whiteboard.jpg");
    final int[] WHITEBOARDPIC1 = TextureLibrary.loadTexture(gl, "textures/pic1.jpg");
    final int[] WHITEBOARDPIC2 = TextureLibrary.loadTexture(gl, "textures/pic2.jpg");
    final int[] WHITEBOARDPIC3 = TextureLibrary.loadTexture(gl, "textures/pic3.jpg");

    Material material = new Material(
            new Vec3(1, 1, 1),
            new Vec3(1, 1, 1),
            new Vec3(0.3f, 0.3f, 0.3f), 30f);
    Frame = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);

    whiteBoard = new Model(camera, lightList, twoTrianglesShader, material, twoTrianglesMesh, WHITEBOARD);
    whiteBoardPIC1 = new Model(camera, lightList, twoTrianglesShader, material, twoTrianglesMesh, WHITEBOARDPIC1);
    whiteBoardPIC2 = new Model(camera, lightList, twoTrianglesShader, material, twoTrianglesMesh, WHITEBOARDPIC2);
    whiteBoardPIC3 = new Model(camera, lightList, twoTrianglesShader, material, twoTrianglesMesh, WHITEBOARDPIC3);
  }

  private void modelTable(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/wood_specular.jpg");

    Material material = new Material(
            new Vec3(0, 0, 0),
            new Vec3(0, 0, 0),
            new Vec3(0.3f, 0.3f, 0.3f), 30f);
    tableFrame = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);

  }

  private void modelLaptop(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/scratch.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/polished_metal.jpg");
    final int[] KEYBOARD = TextureLibrary.loadTexture(gl, "textures/keyboard.jpg");
    final int[] SCREEN = TextureLibrary.loadTexture(gl, "textures/wallpaper.jpg");

    Material material = new Material(
            new Vec3(1, 1, 1),
            new Vec3(0, 0, 0),
            new Vec3(0.3f, 0.3f, 0.3f), 30f);
    laptopFrame = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
    keyBoard = new Model(camera, lightList, twoTrianglesShader, material, twoTrianglesMesh, KEYBOARD);
    Screen = new Model(camera, lightList, twoTrianglesShader, material, twoTrianglesMesh, SCREEN);
  }

  private void modelEggShape(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/scratch.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/metal_specular.jpg");
    final int[] BASE = TextureLibrary.loadTexture(gl, "textures/wood.jpg");

    Material material = new Material(
            new Vec3(0, 0, 0),
            new Vec3(0, 0, 0),
            new Vec3(0.3f, 0.3f, 0.3f), 30f);
    egg = new Model(camera, lightList, cubeShader, material, sphereMesh, DIFFUSE, SPECULAR);
    base = new Model(camera, lightList, cubeShader, material, cubeMesh, BASE);


  }

  private void modelLamp(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/scratch.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/metal_specular.jpg");
    final int[] JOINT = TextureLibrary.loadTexture(gl, "textures/black.jpg");
    final int[] EAR = TextureLibrary.loadTexture(gl, "textures/black.jpg");

    Material material = new Material(
            new Vec3(1, 1, 1),
            new Vec3(0, 0, 0),
            new Vec3(1, 1, 1), 30f);

    Mesh mesh = new Mesh(gl, FrustumCone.createVertices(true), FrustumCone.createIndices(false));

    cube = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
    cylinder = new Model(camera, lightList, cubeShader, material, cylinderMesh, DIFFUSE, SPECULAR);
    sphere = new Model(camera, lightList, cubeShader, material, sphereMesh, JOINT, SPECULAR);
    frustumCone = new Model(camera, lightList, cubeShader, material, mesh, DIFFUSE, SPECULAR);

    Ear = new Model(camera, lightList, cubeShader, material, sphereMesh, EAR, SPECULAR);
    Tail = new Model(camera, lightList, cubeShader, material, sphereMesh, EAR, SPECULAR);
  }
}
