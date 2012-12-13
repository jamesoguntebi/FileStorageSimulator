package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import logic.Algorithm;
import requestor.RequestStreamModel;
import structure.DiskType;
import system.Master;

public class ControlPanel extends JPanel implements ActionListener {
  private static final long serialVersionUID = 1L;
  private static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);

  private static ControlPanel instance;

  public static ControlPanel get() {
    if (instance == null) {
      instance = new ControlPanel();
    }
    return instance;
  }

  private ControlPanel() {
    setBackground(MainFrame.DARK_BLUE);
    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(0, 300));

    add(getOptionsPanel(), BorderLayout.CENTER);
    add(getButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel optionsPanel;
  private ArrayList<JComponent> optionsComponents;

  private JPanel getOptionsPanel() {
    if (optionsPanel == null) {
      optionsPanel = new JPanel();
      optionsPanel.setBackground(null);
      optionsPanel.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();

      optionsComponents = new ArrayList<JComponent>();

      gbc.gridx = 0;
      gbc.gridy = 0;
      optionsPanel.add(getOptionsLabel("Cluster count:"), gbc);

      gbc.gridx = 1;
      optionsPanel.add(UiUtil.getBorderedContainer(getClusterCountTextField(), 5), gbc);
      optionsComponents.add(getClusterCountTextField());

      gbc.gridx = 0;
      gbc.gridy = 1;
      optionsPanel.add(getOptionsLabel("Nodes per cluster:"), gbc);

      gbc.gridx = 1;
      optionsPanel.add(UiUtil.getBorderedContainer(getNodesPerClusterTextField(), 5), gbc);
      optionsComponents.add(getNodesPerClusterTextField());

      gbc.gridx = 0;
      gbc.gridy = 2;
      optionsPanel.add(getOptionsLabel("Storage disk type:"), gbc);

      gbc.gridx = 1;
      optionsPanel.add(UiUtil.getBorderedContainer(getDiskTypeComboBox(), 5), gbc);
      optionsComponents.add(getDiskTypeComboBox());

      gbc.gridx = 0;
      gbc.gridy = 3;
      optionsPanel.add(getOptionsLabel("DFS algorithm:"), gbc);

      gbc.gridx = 1;
      optionsPanel.add(UiUtil.getBorderedContainer(getAlgorithmComboBox(), 5), gbc);
      optionsComponents.add(getAlgorithmComboBox());

      gbc.gridx = 0;
      gbc.gridy = 4;
      optionsPanel.add(getOptionsLabel("Access request stream:"), gbc);

      gbc.gridx = 1;
      optionsPanel.add(UiUtil.getBorderedContainer(getRequestStreamComboBox(), 5), gbc);
      optionsComponents.add(getRequestStreamComboBox());
    }
    return optionsPanel;
  }

  private JLabel getOptionsLabel(String text) {
    JLabel label = new JLabel(text);
    label.setHorizontalAlignment(SwingConstants.RIGHT);
    label.setPreferredSize(new Dimension(150, 25));
    label.setForeground(Color.WHITE);
    return label;
  }

  private JTextField clusterCountTextField;
  private String clusterCountText;

  private JTextField getClusterCountTextField() {
    if (clusterCountTextField == null) {
      clusterCountTextField = new JTextField();
      clusterCountTextField.setPreferredSize(new Dimension(140, 25));
      clusterCountText = "8";
      clusterCountTextField.setText(clusterCountText);
      clusterCountTextField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent arg0) {
          if (clusterCountTextField.getText().isEmpty()) {
            return;
          }
          try {
            Integer.parseInt(clusterCountTextField.getText());
            clusterCountText = clusterCountTextField.getText();
          } catch (NumberFormatException e) {
            clusterCountTextField.setText(clusterCountText);
          }
        }
      });
    }
    return clusterCountTextField;
  }

  private JTextField nodesPerClusterTextField;
  private String nodesPerClusterText;

  private JTextField getNodesPerClusterTextField() {
    if (nodesPerClusterTextField == null) {
      nodesPerClusterTextField = new JTextField();
      nodesPerClusterTextField.setPreferredSize(new Dimension(140, 25));
      nodesPerClusterText = "256";
      nodesPerClusterTextField.setText(nodesPerClusterText);
      nodesPerClusterTextField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent arg0) {
          if (nodesPerClusterTextField.getText().isEmpty()) {
            return;
          }
          try {
            Integer.parseInt(nodesPerClusterTextField.getText());
            nodesPerClusterText = nodesPerClusterTextField.getText();
          } catch (NumberFormatException e) {
            nodesPerClusterTextField.setText(nodesPerClusterText);
          }
        }
      });
    }
    return nodesPerClusterTextField;
  }

  private JComboBox<DiskType> diskTypeComboBox;

  private JComboBox<DiskType> getDiskTypeComboBox() {
    if (diskTypeComboBox == null) {
      diskTypeComboBox = new JComboBox<DiskType>(DiskType.values());
      diskTypeComboBox.setPreferredSize(new Dimension(140, 25));
    }
    return diskTypeComboBox;
  }

  private JComboBox<Algorithm> algorithmComboBox;

  private JComboBox<Algorithm> getAlgorithmComboBox() {
    if (algorithmComboBox == null) {
      algorithmComboBox = new JComboBox<Algorithm>(Algorithm.getAlgorithms());
      algorithmComboBox.setPreferredSize(new Dimension(140, 25));
    }
    return algorithmComboBox;
  }

  private JComboBox<RequestStreamModel> requestStreamComboBox;

  private JComboBox<RequestStreamModel> getRequestStreamComboBox() {
    if (requestStreamComboBox == null) {
      requestStreamComboBox =
          new JComboBox<RequestStreamModel>(RequestStreamModel.getRequestStreamModels());
      requestStreamComboBox.setPreferredSize(new Dimension(140, 25));
    }
    return requestStreamComboBox;
  }

  private JPanel buttonPanel;

  private JPanel getButtonPanel() {
    if (buttonPanel == null) {
      buttonPanel = new JPanel();
      buttonPanel.setLayout(new GridBagLayout());
      buttonPanel.setBackground(null);

      buttonPanel.add(UiUtil.getBorderedContainer(getStartButton(), 5));
      buttonPanel.add(UiUtil.getBorderedContainer(getStopButton(), 5));
    }
    return buttonPanel;
  }

  private JButton startButton;

  private JButton getStartButton() {
    if (startButton == null) {
      startButton = new JButton("Start");
      startButton.setFont(BUTTON_FONT);
      startButton.addActionListener(this);
      startButton.setPreferredSize(new Dimension(120, 40));
    }
    return startButton;
  }

  private JButton stopButton;

  private JButton getStopButton() {
    if (stopButton == null) {
      stopButton = new JButton("Stop");
      stopButton.setFont(BUTTON_FONT);
      stopButton.addActionListener(this);
      stopButton.setEnabled(false);
      stopButton.setPreferredSize(new Dimension(120, 40));
    }
    return stopButton;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == getStartButton()) {
      if (getStartButton().getText().equals("Start")) {
        ClusterList.get().clearClusterData();
        Master.create(Integer.parseInt(getClusterCountTextField().getText()),
            Integer.parseInt(getNodesPerClusterTextField().getText()),
            (DiskType) getDiskTypeComboBox().getSelectedItem(),
            (Algorithm) getAlgorithmComboBox().getSelectedItem(),
            (RequestStreamModel) getRequestStreamComboBox().getSelectedItem());
        Master.getStaticInstance().initializeWithWrites(1000,
            (RequestStreamModel) getRequestStreamComboBox().getSelectedItem());
        Master.getStaticInstance().addPropertyChangeListener(ClusterList.get());
        Master.getStaticInstance().addPropertyChangeListener(StatisticsPanel.get());
        Master.getStaticInstance().execute();

        getOptionsPanel().setEnabled(false);
        getStartButton().setText("Pause");
        getStopButton().setEnabled(true);
        setOptionsComponentsEnabled(false);
      } else if (getStartButton().getText().equals("Pause")) {
        Master.getStaticInstance().pause();

        getStartButton().setText("Resume");
      } else if (getStartButton().getText().equals("Resume")) {
        Master.getStaticInstance().resume();

        getStartButton().setText("Pause");
      }
    } else if (evt.getSource() == getStopButton()) {
      Master.getStaticInstance().cancel(true);
      while (!Master.getStaticInstance().isDone()) {}
      Master.getStaticInstance().firePropertyChange("CLUSTERS_UPDATED", 0, 1);

      Master.getStaticInstance().flushSimState();

      getStartButton().setText("Start");
      getStopButton().setEnabled(false);
      setOptionsComponentsEnabled(true);
    }
  }

  private void setOptionsComponentsEnabled(boolean enabled) {
    getOptionsPanel();
    for (JComponent component : optionsComponents) {
      component.setEnabled(enabled);
    }
  }
}
