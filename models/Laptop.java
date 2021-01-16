package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;
import shapes.*;

/**
 * I declare that this code is my own work.
 * Author: Mite Li (mli115@sheffield.ac.uk)
 */

public class Laptop {

    static SGNode LaptopRoot;
    private TransformNode  JointRotateX;
    private final Model laptopFrame;
    private final Model sphere;
    private final Model keyBoard;
    private final Model Screen;
    static float tableWidth, tableHeight, tableDepth, laptopWidth, laptopDepth;
    private final float roomWidth;
    private final float roomHeight;
    private final float roomDepth;
    private float initialAngle = 0;
    private float rotationAngle;
    static final float FRAME_DIM = Cube.THICKNESS / 4;
    private double startTime;

    public boolean clickedOnOff = false;
    public boolean isAnimatingOnOff = false;

    private static final Vec3 RATIO = new Vec3(0.68f, 0.4f, 0.29f);

    public Laptop(Vec3 roomSize, Model laptopFrame, Model sphere, Model keyBoard, Model Screen) {
        this.roomWidth = roomSize.x;
        this.roomHeight = roomSize.y;
        this.roomDepth = roomSize.z;
        this.laptopFrame = laptopFrame;
        this.sphere = sphere;
        this.keyBoard = keyBoard;
        this.Screen = Screen;

        tableWidth = roomWidth * RATIO.x;
        tableHeight = roomHeight * RATIO.y;
        tableDepth = roomDepth * RATIO.z;
        laptopWidth = tableWidth * RATIO.x;
        laptopDepth = tableDepth * RATIO.z;

    }

    public void initialise() {

        LaptopRoot = new NameNode("Laptop structure");
        TransformNode rootTranslate = new TransformNode("Root translate",
                Mat4Transform.translate(0, -(tableHeight+FRAME_DIM/2) / 2 , 0));

        Table.tableTop.addChild(LaptopRoot);
            LaptopRoot.addChild(rootTranslate);
                createLaptopBottom(rootTranslate);

        Table.tableRoot.update();
        LaptopRoot.update();
    }

    public void render(GL3 gl) {

        if (clickedOnOff || isAnimatingOnOff) {
            if (clickedOnOff) {
                calculateOnOffPose();
            } else {
                float elapsedTime = (float) Math.sin(getSeconds() - startTime);
                changePose(elapsedTime);
            }
        }

        LaptopRoot.update();
        LaptopRoot.draw(gl);
    }

    private void changePose (float time){

        final float THRESHOLD = 3f;

        if (Math.abs(90 - 90 * time) < THRESHOLD) {

            clickedOnOff = false;
            isAnimatingOnOff = false;
            startTime = getSeconds();
        }else {

            JointRotateX.setTransform(Mat4Transform.rotateAroundX(initialAngle + rotationAngle * time));
        }
    }

    private void calculateOnOffPose(){
        if(rotationAngle == 90){
            initialAngle = 90;
            rotationAngle = -90;

        } else{
            initialAngle = 0;
            rotationAngle = 90;

        }

        clickedOnOff = false;
        isAnimatingOnOff = true;
        startTime = getSeconds();

    };

    private void createLaptopBottom (SGNode parent){

        NameNode LaptopBottom = new NameNode("Laptop Bottom");
        Mat4 m = Mat4Transform.scale(laptopWidth / 3 + FRAME_DIM, FRAME_DIM / 4, laptopDepth);
        TransformNode LaptopBottomTranslate = new TransformNode("Laptop Bottom Translate",
                Mat4Transform.translate(0, tableHeight/2 + FRAME_DIM, laptopDepth / 2));
        TransformNode LaptopBottomTransform = new TransformNode("Laptop Bottom transform", m);
        ModelNode LaptopBottomModel = new ModelNode("Laptop Bottom model", laptopFrame);

        NameNode keyboard = new NameNode("Laptop keyboard");
        Mat4 n = Mat4Transform.scale(laptopWidth / 3 , FRAME_DIM / 5, 0.8f * laptopDepth );
        TransformNode keyboardTranslate = new TransformNode("keyboard Translate",
                Mat4Transform.translate(0, tableHeight/2 + FRAME_DIM + 0.04f, laptopDepth / 2));
        TransformNode keyboardTransform = new TransformNode("keyboard transform", n);
        ModelNode keyboardModel = new ModelNode("keyboard model", keyBoard);



        parent.addChild(LaptopBottomTranslate);
        parent.addChild(keyboardTranslate);
        LaptopBottomTranslate.addAllChildren(LaptopBottom, LaptopBottomTransform, LaptopBottomModel);
        keyboardTranslate.addAllChildren(keyboard, keyboardTransform, keyboardModel);
        createLaptopTop(LaptopBottom);
    }
    private void createLaptopTop (SGNode parent) {
        NameNode LaptopTop = new NameNode("Laptop Top");
        Mat4 m = Mat4Transform.scale(laptopWidth / 3 + FRAME_DIM, FRAME_DIM / 4, laptopDepth);
        TransformNode LaptopTopTranslate = new TransformNode("Laptop Top Translate",
                Mat4Transform.translate(0, laptopDepth/2, 0));
        m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
        TransformNode LaptopTopTransform = new TransformNode("Laptop Top transform", m);
        ModelNode LaptopTopModel = new ModelNode("Laptop Top model", laptopFrame);

        NameNode screen = new NameNode("Screen");
        Mat4 o = Mat4Transform.scale(laptopWidth / 3, 0,  0.8f * laptopDepth);
        TransformNode screenTranslate = new TransformNode("Screen Translate",
                Mat4Transform.translate(0, laptopDepth/2, 0.04f));
        o = Mat4.multiply(Mat4Transform.rotateAroundX(90),o);
        TransformNode screenTransform = new TransformNode("Screen transform", o);
        ModelNode screenModel = new ModelNode("Screen model", Screen);

        TransformNode JointTranslate = new TransformNode("Joint translate",
                Mat4Transform.translate(0, 0, -laptopDepth / 2));

        JointRotateX = new TransformNode("Joint rotate",
                Mat4Transform.rotateAroundX(rotationAngle));

        NameNode Joint = new NameNode("Joint");
        Mat4 n = Mat4Transform.scale(0.01f, 0.01f, 0.01f);
        TransformNode JointTransform = new TransformNode("Joint transform", n);
        ModelNode JointModel = new ModelNode("Joint model", sphere);

        parent.addChild(LaptopTopTranslate);
        parent.addChild(JointTranslate);
            JointTranslate.addChild(JointRotateX);
                JointRotateX.addAllChildren(Joint, JointTransform, JointModel);
                    Joint.addChild(LaptopTopTranslate);
                         LaptopTopTranslate.addAllChildren(LaptopTop, LaptopTopTransform, LaptopTopModel);
                    Joint.addChild(screenTranslate);
                         screenTranslate.addAllChildren(screen, screenTransform, screenModel);

    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}
