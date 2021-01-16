package models;

import com.jogamp.opengl.*;
import java.util.*;
import lib.*;
import lib.gmaths.*;

/**
 * I declare that this code is my own work.
 * Author: Mite Li (mli115@sheffield.ac.uk)
 */

public class Lamp {

  private SGNode lampRoot;
  private TransformNode rootTranslateX;
  private TransformNode baseRotateY;
  private TransformNode baseRotateZ;
  private TransformNode lowJointRotateZ;
  private TransformNode highJointRotateZ;
  private TransformNode headJointRotateY, headJointRotateZ;

  private final Model cube;
  private final Model cylinder;
  private final Model sphere;
  private final Model frustumCone;
  private final Model Ear;
  private final Model Tail;
  private final Light lampLight;

  private final float lampRadius;
  private final float baseHeight;
  private final float jointRadius;
  private final float bodyRadius;
  private final float lowBodyHeight;
  private final float highBodyHeight;
  private final float lampX;
  private final float lampY;

  private double startTime;

  public boolean clickedRandom = false;
  public boolean isAnimatingRandom = false;

  public boolean clickedReset = false;
  public boolean isAnimatingReset = false;

  public boolean clickedMove = false;
  public boolean isAnimatingMove = false;


  private final int DEFAULT_BASE_Y = -20;
  private float initialBase = DEFAULT_BASE_Y;
  private float targetBase;

  private final int DEFAULT_LOW_Z = 20;
  private final int MIN_LOW_Z = -30;
  private final int MAX_LOW_Z = 70;
  private float initialLow = DEFAULT_LOW_Z;
  private float targetLow;

  private final int DEFAULT_HIGH_Z = -60;
  private final int MIN_HIGH_Z = -100;
  private final int MAX_HIGH_Z = 0;
  private float initialHigh = DEFAULT_HIGH_Z;
  private float targetHigh;

  private final int DEFAULT_HEAD_Y = 0;
  private final int MIN_HEAD_Y = -80;
  private final int MAX_HEAD_Y = 100;
  private float initialHeadY = DEFAULT_HEAD_Y;
  private float targetHeadY;

  private final int DEFAULT_HEAD_Z = -10;
  private final int MIN_HEAD_ANGLE_Z = -30;
  private final int MAX_HEAD_ANGLE_Z = 50;
  private float initialHeadZ = DEFAULT_HEAD_Z;
  private float targetHeadZ;

  private float Target = 4;
  private float initialPos;

  private final Random r = new Random();

  public Lamp(Model cube, Model cylinder, Model sphere, Model frustumCone, Light lampLight,
      Model Ear, Model Tail) {
    this.cube = cube;
    this.cylinder = cylinder;
    this.sphere = sphere;
    this.frustumCone = frustumCone;
    this.lampLight = lampLight;
    this.Ear = Ear;
    this.Tail = Tail;

    lampRadius = Table.tableWidth * 0.1f;
    baseHeight = Table.tableHeight * 0.06f;
    jointRadius = lampRadius * 0.2f;
    bodyRadius = jointRadius / 2;
    lowBodyHeight = Table.tableHeight * 0.35f;
    highBodyHeight = Table.tableHeight * 0.37f;

    lampX = -Table.tableWidth / 2 + 2;
    lampY = (Table.FRAME_DIM + baseHeight / 2) / 2;
  }


  public void initialise() {

    lampRoot = new NameNode("Lamp root");
    TransformNode rootTranslateY = new TransformNode("Root translate Y",
        Mat4Transform.translate(0, lampY, 0));

    rootTranslateX = new TransformNode("Root translate X",
        Mat4Transform.translate(lampX, 0, 2f));

    Table.tableTop.addChild(lampRoot);
      lampRoot.addChild(rootTranslateY);
        rootTranslateY.addChild(rootTranslateX);
          createBase(rootTranslateX);

    Table.tableRoot.update();
    lampRoot.update();
  }

  public void render(GL3 gl) {
    if (clickedRandom || isAnimatingRandom) {
      if (clickedRandom) {
        calculateRandomPose();
      } else {
        float elapsedTime = (float) Math.sin(getSeconds() - startTime);
        changePose(elapsedTime);
      }
    } else if (clickedReset || isAnimatingReset) {
      if (clickedReset) {
        calculateResetPose();
      } else {
        float elapsedTime = (float) Math.sin(getSeconds() - startTime);
        changePose(elapsedTime);
      }
    } else if (clickedMove || isAnimatingMove) {
      if (clickedMove) {
        calculateMove();
      } else {
        float elapsedTime = (float) (getSeconds() - startTime);
        move(elapsedTime);
        }
      }

    lampRoot.update();
    lampRoot.draw(gl);
  }

  private void calculateRandomPose() {
    targetBase = 0;

    float min = MIN_LOW_Z - initialLow;
    float max = MAX_LOW_Z - initialLow;
    targetLow = min + r.nextFloat() * (max - min);

    float finallowJointAngle = initialLow + targetLow;

    if (finallowJointAngle > 0) {
      max = MIN_HIGH_Z - initialHigh;
      min = MIN_HIGH_Z - initialHigh + finallowJointAngle / 2;
    } else {
      max = MAX_HIGH_Z - initialHigh;
      min = initialLow + targetLow;
    }

    targetHigh = min + r.nextFloat() * (max - min);

    max = MAX_HEAD_Y - initialHeadY;
    min = MIN_HEAD_Y - initialHeadY;
    targetHeadY = min + r.nextFloat() * (max - min);

    max = MAX_HEAD_ANGLE_Z - initialHeadZ;
    min = MIN_HEAD_ANGLE_Z - initialHeadZ;
    targetHeadZ = min + r.nextFloat() * (max - min);

    clickedRandom = false;
    isAnimatingRandom = true;
    startTime = getSeconds();
  }

  private void calculateResetPose() {
    targetBase = 0;
    targetLow = DEFAULT_LOW_Z - initialLow;
    targetHigh = DEFAULT_HIGH_Z - initialHigh;
    targetHeadY = DEFAULT_HEAD_Y - initialHeadY;
    targetHeadZ = DEFAULT_HEAD_Z - initialHeadZ;

    clickedReset = false;
    isAnimatingReset = true;
    startTime = getSeconds();
  }

  private void calculateMove() {
    if(Target == 4){
      initialPos = 2;
      Target = -4;
    } else{
      initialPos = -2;
      Target = 4;
    }

    clickedMove = false;
    isAnimatingMove = true;
    startTime = getSeconds();
  }

  private void move(float time) {
    time = (float) Math.sin(time);
    final float currentPosition = 1f - time;
    final float THRESHOLD = 0.005f;

    if (Math.abs(currentPosition) < THRESHOLD) {
      isAnimatingMove = false;
    } else {
      rootTranslateX.setTransform(Mat4Transform.translate(lampX, lampY-0.25f, initialPos + Target * time));
    }
  }

  private void changePose(float time) {

    float changeBY = initialBase + targetBase * time;
    float changeL = initialLow + targetLow * time;
    float changeU = initialHigh + targetHigh * time;
    float changeHY = initialHeadY + targetHeadY * time;
    float changeHZ = initialHeadZ + targetHeadZ * time;
    float finalchangeBY = initialBase + targetBase - changeBY;
    float finalchangeL = initialLow + targetLow - changeL;
    float finalchangeU = initialHigh + targetHigh - changeU;
    float finalchangeHY = initialHeadY + targetHeadY - changeHY;
    float finalchangeHZ = initialHeadZ + targetHeadZ - changeHZ;
    final float THRESHOLD = 0.1f;

    if (Math.abs(finalchangeBY) < THRESHOLD && Math.abs(finalchangeL) < THRESHOLD &&
        Math.abs(finalchangeU) < THRESHOLD && Math.abs(finalchangeHY) < THRESHOLD &&
        Math.abs(finalchangeHZ) < THRESHOLD) {

      initialBase += targetBase;
      initialLow += targetLow;
      initialHigh += targetHigh;
      initialHeadY += targetHeadY;
      initialHeadZ += targetHeadZ;

      isAnimatingRandom = false;
      isAnimatingReset = false;
      startTime = getSeconds();
    } else {
      baseRotateY.setTransform(Mat4Transform.rotateAroundY(changeBY));
      lowJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(changeL));
      highJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(changeU));
      headJointRotateY.setTransform(Mat4Transform.rotateAroundY(changeHY));
      headJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(changeHZ));
    }
  }

  private void createBase(SGNode parent) {

    baseRotateY = new TransformNode("Base rotate Y",
        Mat4Transform.rotateAroundY(DEFAULT_BASE_Y));

    baseRotateZ = new TransformNode("Base rotate Z", Mat4Transform.rotateAroundZ(0));

    NameNode innerBase = new NameNode("Inner base");
    Mat4 m = Mat4Transform.scale(lampRadius / 2, baseHeight, lampRadius / 2);
    TransformNode innerBaseTransform = new TransformNode("Inner base transform", m);
    ModelNode innerBaseModel = new ModelNode("Inner base model", cube);

    NameNode outerBase = new NameNode("Outer base");
    m = Mat4Transform.scale(lampRadius/2 + 0.1f, baseHeight / 4, lampRadius/2 + 0.1f);
    m = Mat4.multiply(Mat4Transform.translate(0, -baseHeight / 10, 0), m);
    TransformNode outerBaseTransform = new TransformNode("Outer base transform", m);
    ModelNode outerBaseModel = new ModelNode("Outer base model", cube);

    parent.addChild(baseRotateY);
      baseRotateY.addChild(baseRotateZ);
        baseRotateZ.addAllChildren(innerBase, innerBaseTransform, innerBaseModel);
          createlowBody(innerBase);
        baseRotateZ.addAllChildren(outerBase, outerBaseTransform, outerBaseModel);
  }

  private void createlowBody(SGNode parent) {

    TransformNode lowJointTranslate = new TransformNode("low joint translate",
        Mat4Transform.translate(0, baseHeight / 3, 0));

    lowJointRotateZ = new TransformNode("low joint rotate",
        Mat4Transform.rotateAroundZ(DEFAULT_LOW_Z));

    NameNode lowJoint = new NameNode("Base joint");
    Mat4 m = Mat4Transform.scale(0.01f, 0.01f, 0.01f);
    TransformNode lowJointTransform = new TransformNode("Base joint transform", m);
    ModelNode lowJointModel = new ModelNode("Base joint model", sphere);

    TransformNode lowBodyTranslate = new TransformNode("low body translate",
        Mat4Transform.translate(0, lowBodyHeight / 4, 0));

    NameNode lowBody = new NameNode("low body");
    m = Mat4Transform.scale(bodyRadius, lowBodyHeight + 0.3f, bodyRadius);
    TransformNode lowBodyTransform = new TransformNode("low body transform", m);
    ModelNode lowBodyModel = new ModelNode("low body model", cylinder);

    parent.addChild(lowJointTranslate);
      lowJointTranslate.addChild(lowJointRotateZ);
        lowJointRotateZ.addAllChildren(lowJoint, lowJointTransform, lowJointModel);
          lowJoint.addChild(lowBodyTranslate);
            lowBodyTranslate.addAllChildren(lowBody, lowBodyTransform, lowBodyModel);
              createhighBody(lowBody);
  }

  private void createhighBody(SGNode parent) {
    TransformNode highJointTranslate = new TransformNode("high joint translate",
        Mat4Transform.translate(0, lowBodyHeight / 4, 0));

    highJointRotateZ = new TransformNode("high joint rotate",
        Mat4Transform.rotateAroundZ(DEFAULT_HIGH_Z));

    NameNode highJoint = new NameNode("high joint");
    Mat4 m = Mat4Transform.scale(jointRadius, jointRadius, jointRadius);
    TransformNode highJointTransform = new TransformNode("high joint transform", m);
    ModelNode highJointModel = new ModelNode("high joint model", sphere);

    TransformNode highBodyTranslate = new TransformNode("high body translate",
        Mat4Transform.translate(0, highBodyHeight / 4 , 0));

    NameNode highBody = new NameNode("high body");
    m = Mat4Transform.scale(bodyRadius, highBodyHeight, bodyRadius);
    TransformNode highBodyTransform = new TransformNode("high body transform", m);
    ModelNode highBodyModel = new ModelNode("high body model", cylinder);

    parent.addChild(highJointTranslate);
      highJointTranslate.addChild(highJointRotateZ);
        highJointRotateZ.addAllChildren(highJoint, highJointTransform, highJointModel);
          createTail(highJoint);
          highJoint.addChild(highBodyTranslate);
            highBodyTranslate.addAllChildren(highBody, highBodyTransform, highBodyModel);
              createBackHead(highBody);
  }


  private void createBackHead(SGNode parent) {

    TransformNode headJointTranslate = new TransformNode("Head joint translate",
        Mat4Transform.translate(0, highBodyHeight / 4, 0));

    headJointRotateY = new TransformNode("Head joint rotate Y",
        Mat4Transform.rotateAroundY(DEFAULT_HEAD_Y));
    headJointRotateZ = new TransformNode("Head joint rotate Z",
            Mat4Transform.rotateAroundZ(DEFAULT_HEAD_Z));

    NameNode headJoint = new NameNode("Head joint");
    Mat4 m = Mat4Transform.scale(jointRadius / 2, jointRadius / 2, jointRadius / 2);
    TransformNode headJointTransform = new TransformNode("Head joint transform", m);
    ModelNode headJointModel = new ModelNode("Head joint model", sphere);

    TransformNode backHeadTranslate = new TransformNode("Back head translate",
        Mat4Transform.translate(0, 0.8f / 4, 0));

    TransformNode backHeadRotate = new TransformNode("Back head rotate",
        Mat4Transform.rotateAroundZ(90));

    NameNode backHead = new NameNode("Back head");
    m = Mat4Transform.scale(bodyRadius * 2f, 0.8f, bodyRadius * 2f);
    TransformNode backHeadTranform = new TransformNode("Back head transform", m);
    ModelNode backHeadModel = new ModelNode("Back head model", cylinder);

    parent.addChild(headJointTranslate);
      headJointTranslate.addChild(headJointRotateY);
        headJointRotateY.addChild(headJointRotateZ);
          headJointRotateZ.addAllChildren(headJoint, headJointTransform, headJointModel);
            headJoint.addChild(backHeadTranslate);
              backHeadTranslate.addChild(backHeadRotate);
                backHeadRotate.addAllChildren(backHead, backHeadTranform, backHeadModel);
                  createEars(backHead, bodyRadius * 3f);
                  createFrontHead(backHead);
  }


  private void createFrontHead(SGNode parent) {

    TransformNode frontHeadTranslate = new TransformNode("Front head translate",
        Mat4Transform.translate(0, -0.48f, 0));

    NameNode frontHead = new NameNode("Front head");
    Mat4 m = Mat4Transform.scale(bodyRadius * 3.6f, 0.72f, bodyRadius * 3.6f);
    TransformNode frontHeadTransform = new TransformNode("Front head transform", m);
    ModelNode frontHeadModel = new ModelNode("Front head model", frustumCone);

    NameNode lightBulb = new NameNode("Light bulb");
    m = Mat4Transform.scale(0.4f, 0.4f, 0.4f);
    m = Mat4.multiply(Mat4Transform.translate(0, 0.2f, 0), m);
    TransformNode lightBulbTransform = new TransformNode("Light bulb transform", m);
    LightNode lightBulbModel = new LightNode("Light bulb node", lampLight);

    parent.addChild(frontHeadTranslate);
      frontHeadTranslate.addAllChildren(frontHead, frontHeadTransform, frontHeadModel);
        frontHead.addAllChildren(lightBulb, lightBulbTransform, lightBulbModel);
  }


  private void createEars(SGNode parent, float parentWidth) {

    NameNode leftEar = new NameNode("Left ear");
    Mat4 m = Mat4Transform.scale(bodyRadius * 0.3f, 0.6f, bodyRadius * 4);
    m = Mat4.multiply(Mat4Transform.translate(0, 0.3f, -parentWidth / 3), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(-20), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(-10), m);
    TransformNode leftEarTransform = new TransformNode("Left ear transform", m);
    ModelNode leftEarModel = new ModelNode("Left ear model", Ear);

    NameNode rightEar = new NameNode("Right ear");
    m = Mat4.multiply(Mat4Transform.translate(0, 0, parentWidth / 1.5f), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(40), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(-20), m);
    TransformNode rightEarTransform = new TransformNode("Right ear transform", m);
    ModelNode rightEarModel = new ModelNode("Right ear model", Ear);

    parent.addAllChildren(leftEar, leftEarTransform, leftEarModel);
    parent.addAllChildren(rightEar, rightEarTransform, rightEarModel);
  }

  private void createTail(SGNode parent) {

    TransformNode TailTranslate = new TransformNode("tail translate",
        Mat4.multiply(Mat4Transform.rotateAroundZ(45),
            Mat4Transform.translate(0, 0.22f, 0)));

    NameNode Tail1 = new NameNode("tail");
    Mat4 m = Mat4Transform.scale(0.2f, 0.4f, 0.15f);
    TransformNode TailTransform = new TransformNode(" tail transform", m);
    ModelNode TailModel = new ModelNode(" tail model", Tail);


    parent.addChild(TailTranslate);
      TailTranslate.addAllChildren(Tail1, TailTransform, TailModel);


  }

  private double getSeconds() {
    return System.currentTimeMillis() / 1000.0;
  }
}
