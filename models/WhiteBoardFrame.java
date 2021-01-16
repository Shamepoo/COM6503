package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;
import shapes.*;

/**
 * I declare that this code is my own work.
 * Author: Mite Li (mli115@sheffield.ac.uk)
 */

public class WhiteBoardFrame {

  private SGNode frameRoot;
  private final Model Frame;
  private final Model whiteBoard;
  private final Model whiteBoardPIC1;
  private final Model whiteBoardPIC2;
  private final Model whiteBoardPIC3;
  private final float roomWidth;
  private final float roomHeight;
  private final float roomDepth;
  private final float windowWidth;
  private final float windowHeight;
  private final float FRAME_DIM = Cube.THICKNESS / 2.5f;
  public static final Vec2 RATIO = new Vec2(0.5f, 0.4f);

  public WhiteBoardFrame(Vec3 roomSize, Model Frame, Model whiteBoard, Model whiteBoardPIC1, Model whiteBoardPIC2, Model whiteBoardPIC3) {

    this.roomWidth = roomSize.x;
    this.roomHeight = roomSize.y;
    this.roomDepth = roomSize.z;
    this.Frame = Frame;
    this.whiteBoard = whiteBoard;
    this.whiteBoardPIC1 = whiteBoardPIC1;
    this.whiteBoardPIC2 = whiteBoardPIC2;
    this.whiteBoardPIC3 = whiteBoardPIC3;

    windowWidth = roomWidth * RATIO.x;
    windowHeight = roomHeight * RATIO.y;



  }

  public void initialise() {
    frameRoot = new NameNode("WhiteBoard frame structure");
    TransformNode rootTranslate = new TransformNode("Root translate",
            Mat4Transform.translate(0, roomHeight * (RATIO.y + 0.1f), -(roomDepth + Cube.THICKNESS) / 2));

    frameRoot.addChild(rootTranslate);
    createWhiteBoard(rootTranslate);
    createVertical(rootTranslate);
    createHorizon(rootTranslate);
    createWhiteBoardPIC1(rootTranslate);
    createWhiteBoardPIC2(rootTranslate);
    createWhiteBoardPIC3(rootTranslate);

    frameRoot.update();
  }

  public void render(GL3 gl) { frameRoot.draw(gl); }

  private void createWhiteBoard(SGNode parent) {
    NameNode WhiteBoard = new NameNode("WhiteBoard");
    Mat4 m = Mat4Transform.scale(windowWidth - 2*FRAME_DIM, 1, windowHeight - FRAME_DIM);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(0, windowHeight / 2 , 2*FRAME_DIM), m);
    TransformNode WhiteBoardTransform = new TransformNode("WhiteBoard transform", m);
    ModelNode WhiteBoardModel = new ModelNode("WhiteBoard model", whiteBoard);

    parent.addAllChildren(WhiteBoard, WhiteBoardTransform, WhiteBoardModel);
  }

  private void createVertical(SGNode parent) {
    final Mat4 V = Mat4Transform.scale(FRAME_DIM, windowHeight, FRAME_DIM);

    NameNode left = new NameNode("Left");
    Mat4 m = Mat4.multiply(Mat4Transform.translate(-(windowWidth - FRAME_DIM) / 2, windowHeight / 2, FRAME_DIM), V);
    TransformNode leftTransform = new TransformNode("left Transform", m);
    ModelNode leftModel = new ModelNode("left Model", Frame);

    NameNode right = new NameNode("Right");
    m = Mat4.multiply(Mat4Transform.translate((windowWidth - FRAME_DIM) / 2, windowHeight / 2, FRAME_DIM), V);
    TransformNode rightTransform = new TransformNode("right Transform", m);
    ModelNode rightModel = new ModelNode("right Model", Frame);

    parent.addAllChildren(left, leftTransform, leftModel);
    parent.addAllChildren(right, rightTransform, rightModel);
  }

  private void createHorizon(SGNode parent) {
    final Mat4 H = Mat4Transform.scale(windowWidth, FRAME_DIM, FRAME_DIM);

    NameNode bot = new NameNode("Bottom horizontal");
    Mat4 m = Mat4.multiply(Mat4Transform.translate(0, (windowHeight - windowHeight) / 2, FRAME_DIM), H);
    TransformNode botTransform = new TransformNode("bot Transform", m);
    ModelNode botModel = new ModelNode("bot Model", Frame);

    NameNode top = new NameNode("Top horizontal bar");
    m = Mat4.multiply(Mat4Transform.translate(0, (windowHeight + windowHeight) / 2, FRAME_DIM), H);
    TransformNode topTransform = new TransformNode("top Transform", m);
    ModelNode topModel = new ModelNode("top Model", Frame);

    parent.addAllChildren(bot, botTransform, botModel);
    parent.addAllChildren(top, topTransform, topModel);
  }

  private void createWhiteBoardPIC1(SGNode parent) {
    NameNode WhiteBoard = new NameNode("WhiteBoard");
    Mat4 m = Mat4Transform.scale(2, 1, 2);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(3, windowHeight / 2 , 3*FRAME_DIM), m);
    TransformNode WhiteBoardTransform = new TransformNode("WhiteBoard transform", m);
    ModelNode WhiteBoardModel = new ModelNode("WhiteBoard model", whiteBoardPIC1);

    parent.addAllChildren(WhiteBoard, WhiteBoardTransform, WhiteBoardModel);
  }

  private void createWhiteBoardPIC2(SGNode parent) {
    NameNode WhiteBoard = new NameNode("WhiteBoard");
    Mat4 m = Mat4Transform.scale(2, 1, 2);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(-3, windowHeight / 2 , 3*FRAME_DIM), m);
    TransformNode WhiteBoardTransform = new TransformNode("WhiteBoard transform", m);
    ModelNode WhiteBoardModel = new ModelNode("WhiteBoard model", whiteBoardPIC2);

    parent.addAllChildren(WhiteBoard, WhiteBoardTransform, WhiteBoardModel);
  }

  private void createWhiteBoardPIC3(SGNode parent) {
    NameNode WhiteBoard = new NameNode("WhiteBoard");
    Mat4 m = Mat4Transform.scale(2, 1, 2);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(0, windowHeight / 4, 3*FRAME_DIM), m);
    TransformNode WhiteBoardTransform = new TransformNode("WhiteBoard transform", m);
    ModelNode WhiteBoardModel = new ModelNode("WhiteBoard model", whiteBoardPIC3);

    parent.addAllChildren(WhiteBoard, WhiteBoardTransform, WhiteBoardModel);
  }


}
