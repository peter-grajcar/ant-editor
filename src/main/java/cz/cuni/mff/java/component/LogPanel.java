package cz.cuni.mff.java.component;

import cz.cuni.mff.java.ant.AntRunner;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * created: 09/08/2020
 *
 * @author Peter Grajcar
 */
public class LogPanel extends JPanel {

    private String filename;

    private JTextArea log;
    private JButton runButton;
    private JButton stopButton;
    JComboBox<String> targetSelection;

    /**
     * Constructs new log panel. Log panel contains toolbar for running Ant script and also
     * a log produced by the Ant scipts.
     */
    public LogPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 150));

        JToolBar toolBar = new JToolBar("Ant Toolbar");
        toolBar.setRollover(true);
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.PAGE_START);

        toolBar.add(new JLabel("Ant Target:"));

        toolBar.add(Box.createRigidArea(new Dimension(10, 0)));

        targetSelection = new JComboBox<>();
        targetSelection.addItem("test");
        targetSelection.setMaximumSize(new Dimension(200, 100));
        toolBar.add(targetSelection);

        toolBar.add(Box.createRigidArea(new Dimension(10, 0)));

        runButton = new JButton("Run");
        runButton.addActionListener(e -> runAnt());
        runButton.setMaximumSize(new Dimension(50, 100));
        toolBar.add(runButton);

        toolBar.add(Box.createRigidArea(new Dimension(10, 0)));

        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        stopButton.setMaximumSize(new Dimension(50, 100));
        toolBar.add(stopButton);

        toolBar.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> log.setText(""));
        clearButton.setMaximumSize(new Dimension(50, 100));
        toolBar.add(clearButton);


        log = new JTextArea();
        log.setEditable(false);
        log.setFont(new Font("Courier New", Font.PLAIN, 12));
        log.setMargin(new Insets(5, 10, 5, 10));
        JScrollPane logScrollPane = new JScrollPane(log);
        logScrollPane.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(logScrollPane, BorderLayout.CENTER);
    }

    /**
     * Executes and build script in the background via AntRunner.
     */
    private void runAnt() {
        String targetName = (String) targetSelection.getSelectedItem();
        AntRunner antRunner = new AntRunner(filename, targetName);
        antRunner.addAntLogListener(msg -> {
            log.append(msg + "\n");
        });

        stopButton.addActionListener((e) -> {
            antRunner.cancel(true);
        });

        runButton.setEnabled(false);
        stopButton.setEnabled(true);
        antRunner.setCallback(() -> {
            runButton.setEnabled(true);
            stopButton.setEnabled(false);
        });

        log.append("----------------------------------------------------------------------\n");
        log.append("executing \"" + filename  + "\" target \"" + targetName + "\"...");

        antRunner.execute();

    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.runButton.setEnabled(enabled);
        this.stopButton.setEnabled(enabled);
        this.targetSelection.setEnabled(enabled);
    }
}
