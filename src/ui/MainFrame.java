package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Parent GUI object.
 * 
 * @author James
 */
public class MainFrame extends JFrame {
  private static final long serialVersionUID = 1L;

  public static final int APP_WIDTH = 900;
  public static final int APP_HEIGHT = 700;
  public static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
  public static final Color DARK_BLUE = new Color(0, 0, 80);

  private static MainFrame instance;

  public static MainFrame get() {
    if (instance == null) {
      instance = new MainFrame();
    }
    return instance;
  }

  private MainFrame() {
    setTitle("Distributed File Storage Simulator");

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new SpecialWindowAdapter());

    setSize(APP_WIDTH, APP_HEIGHT);
    setLocation(50, 50);

    setContentPane(MainPanel.get());
  }

  /**
   * An object that listens to {@link WindowAdapter#windowClosing(WindowEvent)}
   * in order to TODO to file before closing.
   * 
   * @author James Oguntebi
   */
  private static class SpecialWindowAdapter extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent evt) {
      System.exit(0);
    }
  }
}
