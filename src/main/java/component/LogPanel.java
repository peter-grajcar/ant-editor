package component;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * created: 09/08/2020
 *
 * @author Peter Grajcar
 */
public class LogPanel extends JPanel {

    private JTextArea log;

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

        JComboBox<String> targetSelection = new JComboBox<>();
        targetSelection.setMaximumSize(new Dimension(200, 100));
        toolBar.add(targetSelection);

        toolBar.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton runButton = new JButton("Run");
        runButton.setMaximumSize(new Dimension(50, 100));
        toolBar.add(runButton);

        toolBar.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        stopButton.setMaximumSize(new Dimension(50, 100));
        toolBar.add(stopButton);


        log = new JTextArea();
        log.setEditable(false);
        log.setFont(new Font("Courier New", Font.PLAIN, 12));
        log.setText("> test");
        JScrollPane logScrollPane = new JScrollPane(log);
        logScrollPane.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(logScrollPane, BorderLayout.CENTER);
    }
}
