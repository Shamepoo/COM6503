package lib;

import com.jogamp.opengl.*;
import java.util.*;
import lib.gmaths.*;

/**
 * SGNode class is from tutorial 7
 * addAllChildren() is created added
 *
 *  Author: Mite Li (mli115@sheffield.ac.uk) and Dr. Steve Maddock
 */
public class SGNode {

  final String name;
  final ArrayList<SGNode> children;
  Mat4 worldTransform;

  SGNode(String name) {
    children = new ArrayList<>();
    this.name = name;
    worldTransform = new Mat4(1);
  }

  public void addChild(SGNode child) {
    children.add(child);
  }

  public void addAllChildren(NameNode name, TransformNode transform, SGNode model) {
    children.add(name);
    name.addChild(transform);
    transform.addChild(model);
  }

  public void update() {
    update(worldTransform);
  }

  void update(Mat4 t) {
    worldTransform = t;
    for (SGNode aChildren : children) {
      aChildren.update(t);
    }
  }

  String getIndentString(int indent) {
    StringBuilder s = new StringBuilder("" + indent + " ");
    for (int i = 0; i < indent; ++i) {
      s.append("  ");
    }
    return s.toString();
  }

  void print(int indent, boolean inFull) {
    System.out.println(getIndentString(indent) + "Name: " + name);
    if (inFull) {
      System.out.println("worldTransform");
      System.out.println(worldTransform);
    }
    for (SGNode aChildren : children) {
      aChildren.print(indent + 1, inFull);
    }
  }

  public void draw(GL3 gl) {
    for (SGNode aChildren : children) {
      aChildren.draw(gl);
    }
  }
}
