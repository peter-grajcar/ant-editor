package cz.cuni.mff.java;

import cz.cuni.mff.java.component.LogPanel;
import cz.cuni.mff.java.component.MainPanel;
import org.jdom2.JDOMException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * created: 10/07/2020
 *
 * @author Peter Grajcar
 */
public class Main {

    private String filename;
    private boolean saved = true;

    private JFrame mainFrame;

    private JMenuBar menuBar;

    private MainPanel mainPanel;

    private LogPanel logPanel;

    JFileChooser fileChooser;


    public void makeUI() {
        mainFrame = new JFrame("Ant Editor");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(new Dimension(800, 600));

        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Ant Build Script", "xml"));

        menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.add(new JMenuItem("New"));

        JMenuItem openButton = new JMenuItem("Open");
        openButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openButton.addActionListener(e -> {
            fileChooser.showOpenDialog(mainFrame);
            loadFile(fileChooser.getSelectedFile().getPath());
        });
        menu.add(openButton);

        JMenuItem saveButton = new JMenuItem("Save");
        openButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveButton.addActionListener((e) -> saveFile());
        menu.add(saveButton);
        menuBar.add(menu);

        mainFrame.add(menuBar, BorderLayout.NORTH);


        mainPanel = new MainPanel();
        mainPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        mainPanel.getCodeEditor().addChangeListener(e -> fileEdited());
        mainPanel.getCodeEditor().setText(
                "<?xml version=\"1.0\" encoding=\"us-ascii\"?>\n" +
                "<project basedir=\".\" name=\"\">\n" +
                        "\t<!-- TODO -->\n" +
                "</project>"
        );
        mainPanel.addChangeListener(e -> {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            if(pane.getSelectedIndex() == 1)
                loadGraph();
        });

        logPanel = new LogPanel();
        logPanel.setEnabled(false);
        logPanel.setFilename(filename);


        JSplitPane logSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainPanel, logPanel);
        logSplitPane.setBackground(new Color(0xEBEBEB));
        logSplitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        logSplitPane.setDividerLocation(400);
        logSplitPane.setContinuousLayout(true);
        logSplitPane.setDividerSize(3);
        mainFrame.add(logSplitPane);

        mainFrame.setVisible(true);

        //loadFile("src/main/resources/build.xml");
    }

    private void loadFile(String filename) {
        try(FileReader fileReader = new FileReader(filename)) {
            this.filename = filename;
            mainFrame.setTitle("Ant Editor - " + filename);

            StringBuilder builder = new StringBuilder();
            int b;
            while ((b = fileReader.read()) != -1)
                builder.append((char) b);

            mainPanel.getCodeEditor().setText(builder.toString());
            saved = true;
            logPanel.setEnabled(true);
            logPanel.setFilename(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fileEdited() {
        if(filename == null)
            mainFrame.setTitle("Ant Editor - untitled *");
        else
            mainFrame.setTitle("Ant Editor - " + filename + " *");
        saved = false;
    }

    public void saveFile() {
        if(filename == null) {
            fileChooser.showSaveDialog(mainFrame);
            filename = fileChooser.getSelectedFile().getPath();
        }

        AsyncSave save = new AsyncSave(
                filename,
                mainPanel.getCodeEditor().getText(),
                () -> {
                    mainFrame.setTitle("Ant Editor - " + filename);
                    logPanel.setEnabled(true);
                    logPanel.setFilename(filename);
                    saved = true;
                }
        );
        save.execute();
    }

    public void loadGraph() {
        InputStream is = new ByteArrayInputStream(
                mainPanel.getCodeEditor().getText().getBytes()
        );
        mainPanel.getGraph().load(is);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Main()::makeUI);
    }

}
