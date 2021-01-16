import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout.*;
import lib.*;

/**
 * I declare that this code is my own work.
 * MyKeyboardInput and MyMouseInput are from M04.java
 *
 * Author: Mite Li (mli115@sheffield.ac.uk) and Dr. Steve Maddock
 */

public class Anilamp extends JFrame {

  private final GLCanvas canvas;
  private final Camera camera;
  private final Anilamp_GLEventListener glEventListener;

  static JButton onOroff, random, reset, move, eggJump;

  public static void main(String[] args) {
    new Anilamp();
  }
  private Anilamp() {
    final Container contentPane = getContentPane();

    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    glcapabilities.setDepthBits(24);
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(Camera.ROOM_X, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new Anilamp_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));

    final JPanel buttonPanel = createControlPanel();
    contentPane.add(buttonPanel, BorderLayout.WEST);
    contentPane.add(canvas, BorderLayout.CENTER);

    FPSAnimator animator = new FPSAnimator(canvas, 60);
    animator.start();

    setFrameProperties();
  }

  private void setFrameProperties() {
    final int WIDTH = 1920;
    final int HEIGHT = 1080;

    final Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
    setTitle("COM6503 Anilamp");
    setSize(WIDTH, HEIGHT);
    setLocation(new Point((screenDimensions.width - WIDTH) / 2,
            (screenDimensions.height - HEIGHT) / 2)
    );

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  private JPanel createControlPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 1, 0, 0));
    panel.add(createLightSubpanel());
    panel.add(createLaptopSubpanel());
    panel.add(createEggShapeSubpanel());
    panel.add(createLampSubpanel());

    return panel;
  }



  private JPanel createLightSubpanel() {
    JPanel lightPanel = new JPanel();
    lightPanel.setBorder(BorderFactory.createTitledBorder("World Lights"));

    JButton onOrOff = new JButton("Turn OFF");

    JPanel intensityPanel = new JPanel();
    GroupLayout lightGroup = new GroupLayout(lightPanel);
    lightGroup.setAutoCreateContainerGaps(true);
    lightGroup.setAutoCreateGaps(true);
    lightGroup.setHorizontalGroup(
            lightGroup.createParallelGroup(Alignment.LEADING)
                    .addGroup(lightGroup.createSequentialGroup()
                            .addGroup(lightGroup.createParallelGroup(Alignment.LEADING)
                                    .addComponent(onOrOff)
                                    .addComponent(intensityPanel)))
    );
    lightGroup.setVerticalGroup(
            lightGroup.createParallelGroup(Alignment.LEADING)
                    .addGroup(lightGroup.createSequentialGroup()
                            .addComponent(onOrOff)
                            .addComponent(intensityPanel)
                            .addGap(130))
    );

    GridBagLayout gbl_intensityPanel = new GridBagLayout();
    gbl_intensityPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
    intensityPanel.setLayout(gbl_intensityPanel);

    JLabel intensityLabel = new JLabel("Intensity");
    GridBagConstraints gbc_intensityLabel = new GridBagConstraints();
    gbc_intensityLabel.fill = GridBagConstraints.BOTH;
    gbc_intensityLabel.insets = new Insets(0, 0, 0, 5);
    intensityPanel.add(intensityLabel, gbc_intensityLabel);

    SpinnerNumberModel model = new SpinnerNumberModel(1.0, 0, 1, 0.1);
    JSpinner intensity = new JSpinner(model);
    intensity.setEditor(new JSpinner.DefaultEditor(intensity));
    GridBagConstraints gbc_intensity = new GridBagConstraints();
    gbc_intensity.fill = GridBagConstraints.BOTH;
    intensityPanel.add(intensity, gbc_intensity);
    lightPanel.setLayout(lightGroup);


    onOrOff.addActionListener(e -> {
      if (glEventListener.lightIsOn) {
        onOrOff.setText("Turn ON");
        intensity.setValue(0.0);
      } else {
        onOrOff.setText("Turn OFF");
        intensity.setValue(1.0);
      }
    });


    intensity.addChangeListener(e -> {
      float val = ((Double) intensity.getValue()).floatValue();
      glEventListener.setIntensity(val);

      if (val == 0) {
        glEventListener.lightIsOn = false;
        onOrOff.setText("Turn ON");
      } else {
        glEventListener.lightIsOn = true;
        onOrOff.setText("Turn OFF");
      }
    });

    return lightPanel;
  }

  private JPanel createLaptopSubpanel(){
    JPanel LaptopPanel = new JPanel();
    LaptopPanel.setBorder(BorderFactory.createTitledBorder("Laptop"));

    onOroff = new JButton("Turn OFF");


    GroupLayout LaptopGroup = new GroupLayout(LaptopPanel);
    LaptopGroup.setAutoCreateContainerGaps(true);
    LaptopGroup.setAutoCreateGaps(true);
    LaptopGroup.setHorizontalGroup(LaptopGroup.createParallelGroup(Alignment.LEADING)
            .addComponent(onOroff)

    );
    LaptopGroup.setVerticalGroup(LaptopGroup.createParallelGroup(Alignment.LEADING)
            .addGroup(LaptopGroup.createSequentialGroup()
                    .addComponent(onOroff))

    );
    LaptopPanel.setLayout(LaptopGroup);

    onOroff.addActionListener(e -> {
      if (glEventListener.LaptopIsOn) {
        onOroff.setText("Turn ON");
      } else {
        onOroff.setText("Turn OFF");
      }
      glEventListener.setLaptopOnorOff();
      glEventListener.Laptop.clickedOnOff = true;
    });

    return LaptopPanel;
  }

  private JPanel createEggShapeSubpanel(){
    JPanel EggShapePanel = new JPanel();
    EggShapePanel.setBorder(BorderFactory.createTitledBorder("EggShape"));

    eggJump = new JButton("Jump");

    GroupLayout EggShapeGroup = new GroupLayout(EggShapePanel);
    EggShapeGroup.setAutoCreateContainerGaps(true);
    EggShapeGroup.setAutoCreateGaps(true);
    EggShapeGroup.setHorizontalGroup(EggShapeGroup.createParallelGroup(Alignment.LEADING)
            .addComponent(eggJump)
    );
    EggShapeGroup.setVerticalGroup(EggShapeGroup.createParallelGroup(Alignment.LEADING)
            .addGroup(EggShapeGroup.createSequentialGroup()
                    .addComponent(eggJump))
    );
    EggShapePanel.setLayout(EggShapeGroup);

    eggJump.addActionListener(e -> {
      glEventListener.eggshape.clickedJump = true;
    });

    return EggShapePanel;
  }

  /**
   * Code provided by Dr. Steve Maddock
   */
  private class MyKeyboardInput extends KeyAdapter {

    private final Camera camera;

    MyKeyboardInput(Camera camera) {
      this.camera = camera;
    }

    public void keyPressed(KeyEvent e) {
      Camera.Movement m = Camera.Movement.NO_MOVEMENT;
      switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
          m = Camera.Movement.LEFT;
          break;
        case KeyEvent.VK_RIGHT:
          m = Camera.Movement.RIGHT;
          break;
        case KeyEvent.VK_UP:
          m = Camera.Movement.UP;
          break;
        case KeyEvent.VK_DOWN:
          m = Camera.Movement.DOWN;
          break;
        case KeyEvent.VK_A:
          m = Camera.Movement.FORWARD;
          break;
        case KeyEvent.VK_Z:
          m = Camera.Movement.BACK;
          break;
      }
      camera.keyboardInput(m);
    }
  }

  private JPanel createLampSubpanel() {
    JPanel lampPanel = new JPanel();
    lampPanel.setBorder(BorderFactory.createTitledBorder("Lamp"));

    JPanel lightPanel = new JPanel();
    lightPanel.setBorder(BorderFactory.createTitledBorder("Spotlight"));

    JButton onOrOff = new JButton("Turn OFF");

    JPanel animationPanel = new JPanel();
    animationPanel.setBorder(BorderFactory.createTitledBorder("Animation"));


    random = new JButton("Random Pose");
    reset = new JButton("Reset");
    move = new JButton("Slide");

    GroupLayout lampGroup = new GroupLayout(lampPanel);
    lampGroup.setAutoCreateGaps(true);
    lampGroup.setHorizontalGroup(lampGroup.createParallelGroup(Alignment.LEADING)
            .addComponent(lightPanel)
            .addComponent(animationPanel)
    );
    lampGroup.setVerticalGroup(lampGroup.createParallelGroup(Alignment.LEADING)
            .addGroup(lampGroup.createSequentialGroup()
                    .addComponent(lightPanel)
                    .addComponent(animationPanel))
    );
    lampPanel.setLayout(lampGroup);

    GroupLayout lightGroup = new GroupLayout(lightPanel);
    lightGroup.setAutoCreateContainerGaps(true);
    lightGroup.setAutoCreateGaps(true);
    lightGroup.setHorizontalGroup(lightGroup.createParallelGroup(Alignment.LEADING)
            .addComponent(onOrOff)
    );
    lightGroup.setVerticalGroup(lightGroup.createParallelGroup(Alignment.LEADING)
            .addGroup(lightGroup.createSequentialGroup()
                    .addComponent(onOrOff))
    );
    lightPanel.setLayout(lightGroup);

    GroupLayout animationGroup = new GroupLayout(animationPanel);
    animationGroup.setAutoCreateContainerGaps(true);
    animationGroup.setAutoCreateGaps(true);
    animationGroup.setHorizontalGroup(animationGroup.createParallelGroup(Alignment.LEADING)
            .addComponent(random)
            .addComponent(reset)
            .addComponent(move)
    );
    animationGroup.setVerticalGroup(animationGroup.createParallelGroup(Alignment.LEADING)
            .addGroup(animationGroup.createSequentialGroup()
                    .addComponent(random)
                    .addComponent(reset)
                    .addComponent(move))
    );
    animationPanel.setLayout(animationGroup);


    onOrOff.addActionListener(e -> {
      if (glEventListener.spotlightIsOn) {
        onOrOff.setText("Turn ON");
      } else {
        onOrOff.setText("Turn OFF");
      }

      glEventListener.setSpotlightOnOrOff();
    });

    random.addActionListener(e -> glEventListener.lamp.clickedRandom = true);
    reset.addActionListener(e -> glEventListener.lamp.clickedReset = true);
    move.addActionListener(e -> glEventListener.lamp.clickedMove = true);
    return lampPanel;
  }

  private class MyMouseInput extends MouseMotionAdapter {

    private Point lastpoint;
    private final Camera camera;

    MyMouseInput(Camera camera) {
      this.camera = camera;
    }

    /**
     * mouse is used to control camera position.
     *
     * @param e instance of MouseEvent
     */
    public void mouseDragged(MouseEvent e) {
      Point ms = e.getPoint();
      float sensitivity = 0.001f;
      float dx = (float) (ms.x - lastpoint.x) * sensitivity;
      float dy = (float) (ms.y - lastpoint.y) * sensitivity;
      if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
        camera.updateYawPitch(dx, -dy);
      }
      lastpoint = ms;
    }

    public void mouseMoved(MouseEvent e) {
      lastpoint = e.getPoint();
    }
  }
}