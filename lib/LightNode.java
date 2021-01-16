package lib;

import com.jogamp.opengl.*;

/**
 * I declare that this code is my own work.
 *
 *  Author: Mite Li (mli115@sheffield.ac.uk)
 */
public class LightNode extends SGNode {

  private final Light light;

  public LightNode(String name, Light l) {
    super(name);
    light = l;
  }

  public void draw(GL3 gl) {
    light.setPosition(worldTransform.getTranslateVec());
    light.setDirection(worldTransform.getRotationVec());
    light.render(gl, worldTransform);
  }
}
