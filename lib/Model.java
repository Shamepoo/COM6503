package lib;

import com.jogamp.opengl.*;
import java.util.*;
import lib.gmaths.*;

/**
 * Model class is from tutorial 7
 * A new method is added
 *
 *  Author: Mite Li (mli115@sheffield.ac.uk) and Dr. Steve Maddock
 */
public class Model {

  private final Mesh mesh;
  private float offsetX = 0;
  private float offsetY = 0;
  private final int[] textureId1;
  private final int[] textureId2;
  private final Material material;
  private final Shader shader;
  private final Mat4 modelMatrix;
  private final Camera camera;
  private final List<Light> lightList;

  public Model(Camera camera, List<Light> lightList, Shader shader, Material material, Mesh mesh,
      int[] textureId1, int[] textureId2) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = new Mat4();
    this.shader = shader;
    this.camera = camera;
    this.lightList = lightList;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
  }

  public Model(Camera camera, List<Light> lightList, Shader shader, Material material, Mesh mesh,
      int[] textureId1) {
    this(camera, lightList, shader, material, mesh, textureId1, null);
  }

  public void setDayNightCycle(float value) {
    final float THRESHOLD = 0.25f;

    if (value < THRESHOLD) {
      this.material.setAmbient(value, value, THRESHOLD);
    } else {
      this.material.setAmbient(value, value, value);
    }

    this.material.setDiffuse(value, value, value);
  }

  /**
   * Makes texture moves by adding offsets
   *
   * @param offsetX X coordinate offset of texture
   * @param offsetY Y coordinate offset of texture
   */
  public void setMovingTexture(float offsetX, float offsetY) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(),
        Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

    shader.setVec3(gl, "viewPos", camera.getPosition());

    for (int i = 0; i < lightList.size(); i++) {
      String light;

      if (lightList.get(i).getClass().equals(Spotlight.class)) {
        // Spotlight - lamp light
        light = "spotLight";

        shader.setFloat(gl, "spotlight_intensity", lightList.get(i).getSpotlightIntensity());
        shader.setVec3(gl,"spotLight.direction", lightList.get(i).getDirection());
        shader.setFloat(gl, "spotLight.constant", 1.0f);
        shader.setFloat(gl, "spotLight.linear", 0.09f);
        shader.setFloat(gl, "spotLight.quadratic", 0.032f);
        shader.setFloat(gl, "spotLight.cutOff", (float) Math.cos(Math.toRadians(12.5)));
        shader.setFloat(gl, "spotLight.outerCutOff", (float) Math.cos(Math.toRadians(15)));
      } else {
        // Directional world light
        light = "dirLight" + "[" + i + "]";
      }

      shader.setVec3(gl, light + ".position", lightList.get(i).getPosition());
      shader.setVec3(gl, light + ".ambient", lightList.get(i).getMaterial().getAmbient());
      shader.setVec3(gl, light + ".diffuse", lightList.get(i).getMaterial().getDiffuse());
      shader.setVec3(gl, light + ".specular", lightList.get(i).getMaterial().getSpecular());
    }

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());

    shader.setFloat(gl, "offset", offsetX, offsetY);

    if (textureId1 != null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
    }
    if (textureId2 != null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
    }
    mesh.render(gl);
  }

  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1 != null) {
      gl.glDeleteBuffers(1, textureId1, 0);
    }
    if (textureId2 != null) {
      gl.glDeleteBuffers(1, textureId2, 0);
    }
  }
}
