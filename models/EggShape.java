package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;
import shapes.*;

/**
 * I declare that this code is my own work.
 * Author: Mite Li (mli115@sheffield.ac.uk)
 */

public class EggShape {

    static SGNode EggRoot;
    private TransformNode rootTranslateY;
    private final Model base;
    private final Model egg;
    static float BaseWidth, BaseHeight, BaseDepth, tableWidth, tableHeight, tableDepth, EggHeight;
    private final float roomWidth;
    private final float roomHeight;
    private final float roomDepth;
    private float jumpHeight;
    static final float FRAME_DIM = Cube.THICKNESS / 4;

    private double startTime;

    public boolean clickedJump = false;
    public boolean isAnimatingJump = false;

    private static final Vec3 RATIO = new Vec3(0.68f, 0.4f, 0.29f);

    public EggShape(Vec3 roomSize, Model base, Model egg) {
        this.roomWidth = roomSize.x;
        this.roomHeight = roomSize.y;
        this.roomDepth = roomSize.z;
        this.base = base;
        this.egg = egg;

        tableWidth = roomWidth * RATIO.x;
        tableHeight = roomHeight * RATIO.y;
        tableDepth = roomDepth * RATIO.z;
        BaseWidth = roomWidth * 0.04f;
        BaseHeight = roomHeight * 0.02f;
        BaseDepth = roomDepth * 0.02f;
        EggHeight = roomHeight * 0.05f;

    }

    public void initialise() {

        EggRoot = new NameNode("Egg structure");
        TransformNode rootTranslate = new TransformNode("Root translate",
                Mat4Transform.translate(0, 2 * FRAME_DIM, 0));

        rootTranslateY = new TransformNode("Root translateY",
                Mat4Transform.translate(tableWidth/4, 0, 0));


        Table.tableTop.addChild(EggRoot);
            EggRoot.addChild(rootTranslate);
                createBase(rootTranslate);
                rootTranslate.addChild(rootTranslateY);
                    createEgg(rootTranslateY);

        Table.tableRoot.update();
        EggRoot.update();
    }

    public void render(GL3 gl) {

        if (clickedJump || isAnimatingJump) {
            if (clickedJump) {
                calculateJump();
            } else {
                float elapsedTime = (float) Math.sin(getSeconds() - startTime);
                Jump(elapsedTime);
            }
        }

        EggRoot.update();
        EggRoot.draw(gl);
    }

    private void Jump (float time){

        double p1Part = jumpHeight * 3 * time * Math.pow((1 - time), 2);
        double p2Part = jumpHeight * 3 * Math.pow(time, 2) * (1 - time);
        float translateY = (float) (p1Part + p2Part);

        final float THRESHOLD = 0.005f;
        if (Math.abs(translateY) < THRESHOLD) {
            isAnimatingJump = false;
        }else{
            rootTranslateY.setTransform(Mat4Transform.translate(tableWidth / 4, translateY, 0));
        }
    }

    private void calculateJump(){

        jumpHeight = 4;

        clickedJump = false;
        isAnimatingJump = true;
        startTime = getSeconds();

    };

    private void createBase (SGNode parent){

        NameNode Base = new NameNode("Base");
        Mat4 m = Mat4Transform.scale(BaseWidth, BaseHeight, BaseDepth);
        m = Mat4.multiply(Mat4Transform.translate(tableWidth/4, -FRAME_DIM / 2 , 0),m);
        TransformNode BaseTransform = new TransformNode("Base transform", m);
        ModelNode BaseModel = new ModelNode("Base model", base);

        parent.addAllChildren(Base, BaseTransform, BaseModel);
    }

    private void createEgg (SGNode parent){

        NameNode Egg = new NameNode("Egg");
        Mat4 m = Mat4Transform.scale(BaseWidth , EggHeight, BaseWidth);
        m = Mat4.multiply(Mat4Transform.translate(0, FRAME_DIM * 3/2, 0),m);
        TransformNode EggTransform = new TransformNode("Egg transform", m);
        ModelNode EggModel = new ModelNode("Egg model", egg);

        parent.addAllChildren(Egg, EggTransform, EggModel);
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}
