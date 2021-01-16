package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;
import shapes.*;

/**
 * I declare that this code is my own work.
 * Author: Mite Li (mli115@sheffield.ac.uk)
 */
public class Room {

  private SGNode roomRoot;
  private final Model floor;
  private final Model wall;
  private final Model wall2;
  private Model topWallpaper, bottomWallpaper, leftWallpaper, rightWallpaper, wall2Wallpaper;
  private final float roomWidth;
  private final float roomHeight;
  private final float roomDepth;
  private final float windowWidth;
  private final float windowMaxHeight;
  private final float leftWallWidth;
  private final float leftWallHeight;
  private final float bottomWallWidth;
  private final float bottomWallHeight;
  private final float topWallHeight;

  public Room(Vec3 roomSize, Model floor, Model wall, Model wall2) {
    this.roomWidth = roomSize.x;
    this.roomHeight = roomSize.y;
    this.roomDepth = roomSize.z;
    this.floor = floor;
    this.wall = wall;
    this.wall2 = wall2;

    windowWidth = roomDepth * Window.RATIO.x;
    windowMaxHeight = roomHeight * (Window.Y_POS + Window.RATIO.y);

    leftWallWidth = (roomDepth - windowWidth) / 2;
    leftWallHeight = roomHeight + Cube.THICKNESS / 2;
    bottomWallWidth = windowWidth;
    bottomWallHeight = (roomHeight + Cube.THICKNESS) * Window.Y_POS;
    topWallHeight = roomHeight - windowMaxHeight;
  }

  public void initialise() {

    roomRoot = new NameNode("Room root");
    TransformNode wallTransform = new TransformNode("Wall transform",
        Mat4Transform.translate(0, 0, -(roomDepth + Cube.THICKNESS) / 2));

    createFloor(roomRoot);
    roomRoot.addChild(wallTransform);
      createWall2(wallTransform);
      createBottomWall(wallTransform);
      createLeftRightWall(wallTransform);
      createTopWall(wallTransform);

    roomRoot.update();
  }

  public void render(GL3 gl) {
    roomRoot.draw(gl);
  }

  private void createFloor(SGNode parent) {
    NameNode floor = new NameNode("Floor");
    Mat4 m = Mat4Transform.scale(roomWidth, 1, roomDepth);
    TransformNode floorTransform = new TransformNode("Floor transform", m);
    ModelNode floorModel = new ModelNode("Floor model", this.floor);

    parent.addAllChildren(floor, floorTransform, floorModel);
  }

  private void createWall2(SGNode parent) {
    NameNode wall2 = new NameNode("Wall2");
    Mat4 m = Mat4Transform.scale(roomDepth, roomHeight, 1);
    m = Mat4.multiply(Mat4Transform.translate(0, roomHeight/2, 0), m);
    TransformNode wall2Transform = new TransformNode("Wall2 transform", m);
    ModelNode wall2Model = new ModelNode("Wall2 model", this.wall2);

    NameNode wall2Wallpaper = new NameNode("Wall2 wallpaper");
    m = Mat4Transform.scale(roomWidth, roomHeight, roomDepth);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(0, roomHeight/2, Cube.THICKNESS/2), m);
    TransformNode wall2WallpaperTransform = new TransformNode("Wall2 wallpaper transform", m);
    ModelNode wall2WallpaperModel = new ModelNode("Wall2 wallpaper model", this.wall2Wallpaper);

    parent.addAllChildren(wall2, wall2Transform, wall2Model);
    parent.addAllChildren(wall2Wallpaper, wall2WallpaperTransform, wall2WallpaperModel);
  }

  private void createBottomWall(SGNode parent) {
    NameNode bottomWall = new NameNode("Bottom wall");
    Mat4 m = Mat4Transform.scale(1, bottomWallHeight, bottomWallWidth);
    m = Mat4.multiply(Mat4Transform.translate(-roomWidth/2, (bottomWallHeight - Cube.THICKNESS) / 2, roomDepth/2), m);
    TransformNode bottomWallTransform = new TransformNode("Bottom wall transform", m);
    ModelNode bottomWallModel = new ModelNode("Bottom wall model", wall);

    NameNode bottomWallpaper = new NameNode("Bottom wallpaper");
    m = Mat4Transform.scale(bottomWallWidth, 1, bottomWallHeight - Cube.THICKNESS * (Window.Y_POS + 0.5f));
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundY(90), m);
    m = Mat4.multiply(Mat4Transform.translate(-(roomWidth - Cube.THICKNESS)/2, (bottomWallHeight - Cube.THICKNESS) / 2 + 0.5f, roomDepth/2), m);
    TransformNode bottomWallpaperTransform = new TransformNode("Bottom wallpaper transform", m);
    ModelNode bottomWallpaperModel = new ModelNode("Bottom wallpaper model", this.bottomWallpaper);

    parent.addAllChildren(bottomWall, bottomWallTransform, bottomWallModel);
    parent.addAllChildren(bottomWallpaper, bottomWallpaperTransform, bottomWallpaperModel);
  }

  private void createLeftRightWall(SGNode parent) {
    float wallpaperHeight = leftWallHeight - Cube.THICKNESS * (Window.Y_POS + 0.5f);
    final float WALLPAPER_Y = (wallpaperHeight + Cube.THICKNESS) / 2;

    NameNode leftWall = new NameNode("Left wall");
    Mat4 m = Mat4Transform.scale(1, leftWallHeight, leftWallWidth );
    m = Mat4.multiply(Mat4Transform.translate(-roomWidth / 2 , (leftWallHeight - Cube.THICKNESS) / 2, leftWallWidth/2), m);
    TransformNode leftWallTransform = new TransformNode("Left wall transform", m);
    ModelNode leftWallModel = new ModelNode("Left wall modeL", wall);

    NameNode rightWall = new NameNode("Right wall");
    Mat4 n = Mat4Transform.scale(1, leftWallHeight, leftWallWidth  );
    n = Mat4.multiply(Mat4Transform.translate(-roomWidth / 2, (leftWallHeight - Cube.THICKNESS) / 2, roomDepth - leftWallWidth/2), n);
    TransformNode rightWallTransform = new TransformNode("Right wall transform", n);
    ModelNode rightWallModel = new ModelNode("Right wall model", wall);

    NameNode leftWallpaper = new NameNode("Left wallpaper");
    m = Mat4Transform.scale(leftWallWidth, 1, wallpaperHeight);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundY(90), m);
    m = Mat4.multiply(Mat4Transform.translate(-(roomWidth - Cube.THICKNESS)/2, WALLPAPER_Y, roomDepth - leftWallWidth/2), m);
    TransformNode leftWallpaperTransform = new TransformNode("Left wallpaper transform", m);
    ModelNode leftWallpaperModel = new ModelNode("Left wallpaper model", this.leftWallpaper);

    NameNode rightWallpaper = new NameNode("Right wallpaper");
    n = Mat4Transform.scale(leftWallWidth, 1, wallpaperHeight);
    n = Mat4.multiply(Mat4Transform.rotateAroundX(90), n);
    n = Mat4.multiply(Mat4Transform.rotateAroundY(90), n);
    n = Mat4.multiply(Mat4Transform.translate(-(roomWidth - Cube.THICKNESS)/2, WALLPAPER_Y, leftWallWidth/2), n);
    TransformNode rightWallpaperTransform = new TransformNode("Right wallpaper transform", n);
    ModelNode rightWallpaperModel = new ModelNode("Right wallpaper model", this.rightWallpaper);

    parent.addAllChildren(leftWall, leftWallTransform, leftWallModel);
    parent.addAllChildren(leftWallpaper, leftWallpaperTransform, leftWallpaperModel);
    parent.addAllChildren(rightWall, rightWallTransform, rightWallModel);
    parent.addAllChildren(rightWallpaper, rightWallpaperTransform, rightWallpaperModel);
  }

  /**
   * Creates a top wall with wallpaper
   *
   * @param parent Parent node
   */
  private void createTopWall(SGNode parent) {

    NameNode topWall = new NameNode("Top wall");
    Mat4 m = Mat4Transform.scale(1, topWallHeight, bottomWallWidth);
    m = Mat4.multiply(Mat4Transform.translate(-roomWidth/2, windowMaxHeight + topWallHeight / 2, roomDepth/2), m);
    TransformNode topWallTransform = new TransformNode("Top wall transform", m);
    ModelNode topWallModel = new ModelNode("Top wall model", wall);

    NameNode topWallpaper = new NameNode("Top wallpaper");
    m = Mat4Transform.scale(bottomWallWidth, 1, topWallHeight);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundY(90), m);
    m = Mat4.multiply(Mat4Transform.translate(-roomWidth/2 + Cube.THICKNESS / 2, windowMaxHeight + topWallHeight / 2, roomDepth/2), m);
    TransformNode topWallpaperTransform = new TransformNode("Top wallpaper transform", m);
    ModelNode topWallpaperModel = new ModelNode("Top wallpaper model", this.topWallpaper);

    parent.addAllChildren(topWall, topWallTransform, topWallModel);
    parent.addAllChildren(topWallpaper, topWallpaperTransform, topWallpaperModel);
  }

  public class Wallpaper {
    public Wallpaper(Model top, Model bottom, Model left, Model right, Model wal2) {
      topWallpaper = top;
      bottomWallpaper = bottom;
      leftWallpaper = left;
      rightWallpaper = right;
      wall2Wallpaper = wal2;
    }
  }
}