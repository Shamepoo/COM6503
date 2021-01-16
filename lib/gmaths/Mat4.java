package lib.gmaths;

/**
 * Provided by Dr Steve Maddock.
 * getTranslationVec() and getRotationVec() are added
 *
 *  Author: Mite Li (mli115@sheffield.ac.uk) and Dr. Steve Maddock
 */
public class Mat4 {   // row column formulation

  private final float[][] values;

  public Mat4() {
    this(0);
  }

  public Mat4(float f) {
    values = new float[4][4];
    makeZero();
    for (int i = 0; i < 4; ++i) {
      values[i][i] = f;
    }
  }

  public Mat4(Mat4 m) {
    this.values = new float[4][4];
    for (int i = 0; i < 4; ++i) {
      System.arraycopy(m.values[i], 0, this.values[i], 0, 4);
    }
  }

  public void set(int r, int c, float f) {
    values[r][c] = f;
  }

  private void makeZero() {
    for (int i = 0; i < 4; ++i) {
      for (int j = 0; j < 4; ++j) {
        values[i][j] = 0;
      }
    }
  }

  public static Mat4 multiply(Mat4 a, Mat4 b) {
    Mat4 result = new Mat4();
    for (int i = 0; i < 4; ++i) {
      for (int j = 0; j < 4; ++j) {
        for (int k = 0; k < 4; ++k) {
          result.values[i][j] += a.values[i][k] * b.values[k][j];
        }
      }
    }
    return result;
  }

  public float[] toFloatArrayForGLSL() {  // col by row
    float[] f = new float[16];
    for (int j = 0; j < 4; ++j) {
      for (int i = 0; i < 4; ++i) {
        f[j * 4 + i] = values[i][j];
      }
    }
    return f;
  }

  public Vec3 getTranslateVec() {
    float[] f;
    f = toFloatArrayForGLSL();

    return new Vec3(f[12], f[13], f[14]);
  }

  public Vec3 getRotationVec() {

    float x = (float) Math.asin(values[0][1]);
    float y = (float) Math.asin(values[1][1]);
    float z = (float) Math.asin(values[2][1]);

    return new Vec3(-x, -y, -z);
  }

  public String toString() {
    StringBuilder s = new StringBuilder("{");
    for (int i = 0; i < 4; ++i) {
      s.append((i == 0) ? "{" : " {");
      for (int j = 0; j < 4; ++j) {
        s.append(String.format("%.2f", values[i][j]));
        if (j < 3) {
          s.append(", ");
        }
      }
      s.append((i == 3) ? "}" : "},\n");
    }
    s.append("}");
    return s.toString();
  }

} // end of Mat4 class