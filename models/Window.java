package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;
import shapes.*;

/**
 * I declare that this code is my own work.
 * Author: Mite Li (mli115@sheffield.ac.uk)
 */

public class Window {

  private SGNode windowRoot;
  private final Model outsideScene;
  private final double startTime;
  private final float roomWidth;
  private final float roomHeight;
  private final float roomDepth;
  private final float windowWidth;
  private final float windowHeight;
  private final float FRAME_DIM = Cube.THICKNESS / 2.5f;
  public static final Vec2 RATIO = new Vec2(0.5f, 0.4f);
  public static final float Y_POS = RATIO.y + 0.1f;

  public Window(Vec3 roomSize, Model outsideScene) {
    this.roomWidth = roomSize.x;
    this.roomHeight = roomSize.y;
    this.roomDepth = roomSize.z;
    this.outsideScene = outsideScene;
    windowWidth = roomWidth * RATIO.x;
    windowHeight = roomHeight * RATIO.y; // 2 * FRAME_DIM for top and bot bar
    startTime = getSeconds();
  }

  public void initialise() {

    windowRoot = new NameNode("Window frame structure");
    TransformNode rootTranslate = new TransformNode("Root translate",
        Mat4Transform.translate(0, roomHeight * Y_POS, -(roomDepth + Cube.THICKNESS) / 2));

    windowRoot.addChild(rootTranslate);
      createScene(rootTranslate);
    windowRoot.update();
  }

  public void render(GL3 gl) {
    double elapsedTime = getSeconds() - startTime;
    double wavelength = elapsedTime * 0.5;
    float cosine = (float) ((Math.cos(wavelength) + 1) * 0.5);

    outsideScene.setDayNightCycle(cosine);
    windowRoot.draw(gl);
  }

  private void createScene(SGNode parent) {
    NameNode scene = new NameNode("Scene");
    Mat4 m = Mat4Transform.scale(windowWidth, 1, windowHeight + 2 * FRAME_DIM);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(-roomWidth/2, windowHeight / 2 , roomDepth/2), m);
    TransformNode sceneTransform = new TransformNode("Scene transform", m);
    ModelNode sceneModel = new ModelNode("Scene model", outsideScene);

    parent.addAllChildren(scene, sceneTransform, sceneModel);
    parent.addChild(scene);
  }

  private double getSeconds() {
    return System.currentTimeMillis() / 1000.0;
  }
}
