import component.LogPanel;
import component.MainPanel;
import component.StructurePanel;
import component.editor.CodeEditorPane;
import component.graph.TargetGraph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;

/**
 * created: 10/07/2020
 *
 * @author Peter Grajcar
 */
public class Main {

    private JMenuBar menuBar;

    private MainPanel mainPanel;

    private LogPanel logPanel;

    private StructurePanel structurePanel;

    public void makeUI() {
        JFrame mainFrame = new JFrame("Ant Editor");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(new Dimension(800, 600));

        menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.add(new JMenuItem("New"));
        menu.add(new JMenuItem("Open"));
        menu.add(new JMenuItem("Save"));
        menuBar.add(menu);
        mainFrame.add(menuBar, BorderLayout.NORTH);


        structurePanel = new StructurePanel();
        mainPanel = new MainPanel();
        logPanel = new LogPanel();

        loadFile("src/main/resources/build.xml");

        JSplitPane structureSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, structurePanel, mainPanel);
        structureSplitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        structureSplitPane.setDividerLocation(200);
        structureSplitPane.setContinuousLayout(true);
        structureSplitPane.setDividerSize(3);
        mainFrame.add(structureSplitPane);

        JSplitPane logSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, structureSplitPane, logPanel);
        logSplitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        logSplitPane.setDividerLocation(400);
        logSplitPane.setContinuousLayout(true);
        logSplitPane.setDividerSize(3);
        mainFrame.add(logSplitPane);

        mainFrame.setVisible(true);
    }

    private void loadFile(String filename) {
        try(FileReader fileReader = new FileReader(filename)) {

            StringBuilder builder = new StringBuilder();
            int b;
            while ((b = fileReader.read()) != -1)
                builder.append((char) b);

            mainPanel.getCodeEditor().setText(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        /*try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }*/
        SwingUtilities.invokeLater(new Main()::makeUI);
    }

}
