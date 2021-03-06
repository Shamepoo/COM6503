package lib;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import java.nio.*;
import lib.gmaths.*;
import shapes.*;

/**
 * Light class adapted from tutorial 7
 * A new render method is added
 * setDirection() and getDirection() are added
 *
 * Author: Mite Li (mli115@sheffield.ac.uk) and Dr. Steve Maddock
 */
public class Light {

  private final Material material;
  private final Vec3 position;
  private final Vec3 direction = new Vec3();
  private final Shader shader;
  private Camera camera;
  private float lightColor = 1;
  private float spotlightIntensity = 1;

  public Light(GL3 gl, Camera camera) {
    material = new Material();
    material.setAmbient(0.5f, 0.5f, 0.5f);
    material.setDiffuse(1, 1, 1);
    material.setSpecular(1, 1, 1);
    position = new Vec3(3f, 2f, 1f);
    shader = new Shader(gl, "shaders/vs_light.txt", "shaders/fs_light.txt");
    fillBuffers(gl);
    setCamera(camera);
  }

  public void setPosition(Vec3 v) {
    position.x = v.x;
    position.y = v.y;
    position.z = v.z;
  }

  public void setPosition(float x, float y, float z) {
    position.x = x;
    position.y = y;
    position.z = z;
  }

  Vec3 getPosition() {
    return position;
  }

  Vec3 getDirection() { return direction; }

  void setDirection(Vec3 v) {
    direction.x = v.x;
    direction.y = v.y;
    direction.z = v.z;
  }

  public Material getMaterial() {
    return material;
  }

  public void setLightColor(float value) { this.lightColor = value; }

  float getSpotlightIntensity() { return this.spotlightIntensity; }

  public void setSpotlightIntensity(float value) {
    this.spotlightIntensity = value;
  }

  private void setCamera(Camera camera) {
    this.camera = camera;
  }

  public void render(GL3 gl) {
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(0.3f, 0.3f, 0.3f), model);
    model = Mat4.multiply(Mat4Transform.translate(position), model);

    Mat4 mvpMatrix = Mat4
        .multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), model));

    shader.use(gl);
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    shader.setFloat(gl, "lightColor", this.lightColor);

    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4
        .multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));

    shader.use(gl);
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    shader.setFloat(gl, "lightColor", this.lightColor);

    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  public void dispose(GL3 gl) {
    gl.glDeleteBuffers(1, vertexBufferId, 0);
    gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    gl.glDeleteBuffers(1, elementBufferId, 0);
  }

  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering

  private static final float[] vertices = Sphere.vertices.clone();
  private static final int[] indices = Sphere.indices.clone();


  // ***************************************************
  /* THE LIGHT BUFFERS
   */

  private final int[] vertexBufferId = new int[1];
  private final int[] vertexArrayId = new int[1];
  private final int[] elementBufferId = new int[1];

  private void fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, vertexArrayId, 0);
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glGenBuffers(1, vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);

    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);

    int stride = 8;
    int numXYZFloats = 3;
    int offset = 0;
    gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride * Float.BYTES, offset);
    gl.glEnableVertexAttribArray(0);

    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib,
        GL.GL_STATIC_DRAW);
    gl.glBindVertexArray(0);
  }
}
