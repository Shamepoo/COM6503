package shapes;

/**
 * I declare that the code is my own work expect lines below 27.
 *
 * Author: Mite Li (mli115@sheffield.ac.uk) and Dr. Steve Maddock
 */
public final class TwoTriangles {

  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  public static float[] vertices = {      // position, colour, tex coords
      -0.5f, 0.0f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,  // top left
      -0.5f, 0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,  // bottom left
      0.5f, 0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,  // bottom right
      0.5f, 0.0f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f   // top right
  };

  public static final int[] indices = {         // Note that we start from 0!
      0, 1, 2,
      0, 2, 3
  };


  public static float[] setTexCoords(float[] original, float[] texCoords) {
    for (int i = 0; i < texCoords.length / 2; i++) {
      System.arraycopy(texCoords, (i * 2), original, (i * 8) + 6, 2);
    }

    return original;
  }
}
