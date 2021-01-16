package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;
import shapes.*;

/**
 * I declare that this code is my own work.
 * Author: Mite Li (mli115@sheffield.ac.uk)
 */

public class Table {


  static SGNode tableRoot;
  static NameNode tableTop;
  private final Model tableFrame;
  static float tableWidth, tableHeight, tableDepth, laptopDepth;
  private final float roomWidth;
  private final float roomHeight;
  private final float roomDepth;
  static final float FRAME_DIM = Cube.THICKNESS / 4;

  private static final Vec3 RATIO = new Vec3(0.68f, 0.4f, 0.29f);

  public Table(Vec3 roomSize, Model tableFrame) {
    this.roomWidth = roomSize.x;
    this.roomHeight = roomSize.y;
    this.roomDepth = roomSize.z;
    this.tableFrame = tableFrame;

    tableWidth = roomWidth * RATIO.x;
    tableHeight = roomHeight * RATIO.y;
    tableDepth = roomDepth * RATIO.z;
  }

  public void initialise() {

    tableRoot = new NameNode("Table structure");
    TransformNode rootTranslateX = new TransformNode("Root translate",
        Mat4Transform.translate(0, tableHeight / 2  + FRAME_DIM, -tableDepth));

    tableRoot.addChild(rootTranslateX);
      createLegs(rootTranslateX);
    tableRoot.update();
  }

  public void render(GL3 gl) { tableRoot.draw(gl); }

  private void createLegs (SGNode parent){
      final Mat4 LEG_MAT = Mat4Transform.scale(FRAME_DIM, tableHeight, FRAME_DIM); // Legs matrix

      NameNode leftFrontLeg = new NameNode("Left Front leg");
      Mat4 m = Mat4.multiply(Mat4Transform.translate(-tableWidth / 2, 0, tableDepth / 2 - FRAME_DIM), LEG_MAT);
      TransformNode leftFrontLegTransform = new TransformNode("Left Front leg transform", m);
      ModelNode leftLegFrontModel = new ModelNode("Left Front leg model", tableFrame);

      NameNode leftBackLeg = new NameNode("Left Back leg");
      m = Mat4.multiply(Mat4Transform.translate(-tableWidth / 2, 0, -tableDepth / 2 + FRAME_DIM), LEG_MAT);
      TransformNode leftBackLegTransform = new TransformNode("Left Back leg transform", m);
      ModelNode leftBackLegModel = new ModelNode("Left Back leg model", tableFrame);

      NameNode rightFrontLeg = new NameNode("Right Front leg");
      m = Mat4.multiply(Mat4Transform.translate(tableWidth / 2, 0, tableDepth / 2 - FRAME_DIM), LEG_MAT);
      TransformNode rightFrontLegTransform = new TransformNode("Right Front leg transform", m);
      ModelNode rightFrontLegModel = new ModelNode("Right Front leg model", tableFrame);

      NameNode rightBackLeg = new NameNode("Right Back leg");
      m = Mat4.multiply(Mat4Transform.translate(tableWidth / 2, 0, -tableDepth / 2 + FRAME_DIM), LEG_MAT);
      TransformNode rightBackLegTransform = new TransformNode("Right Back leg transform", m);
      ModelNode rightBackLegModel = new ModelNode("Right Back leg model", tableFrame);

      parent.addAllChildren(leftFrontLeg, leftFrontLegTransform, leftLegFrontModel);
      createTableTop(leftFrontLeg);                         // Table top
      parent.addAllChildren(rightFrontLeg, rightFrontLegTransform, rightFrontLegModel);
      parent.addAllChildren(leftBackLeg, leftBackLegTransform, leftBackLegModel);
      parent.addAllChildren(rightBackLeg, rightBackLegTransform, rightBackLegModel);
    }

  private void createTableTop (SGNode parent){
      TransformNode tableTopTranslate = new TransformNode("table Top Translate",
              Mat4Transform.translate(0, tableHeight / 2, 0));

      tableTop = new NameNode("Table top");
      Mat4 m = Mat4Transform.scale(tableWidth + FRAME_DIM, FRAME_DIM, tableDepth);
      TransformNode tableTopTransform = new TransformNode("Table top transform", m);
      ModelNode tableTopModel = new ModelNode("Table top model", tableFrame);

      parent.addChild(tableTopTranslate);
      tableTopTranslate.addAllChildren(tableTop, tableTopTransform, tableTopModel);

    }

}
