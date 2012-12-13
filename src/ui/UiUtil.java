package ui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class UiUtil {
  public static JPanel getBorderedContainer(JComponent component, int borderSize) {
    JPanel container = new JPanel();
    container.setBackground(null);
    container.setBorder(new EmptyBorder(borderSize, borderSize, borderSize, borderSize));
    container.add(component);
    return container;
  }
}
