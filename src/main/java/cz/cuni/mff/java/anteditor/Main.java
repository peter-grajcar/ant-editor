package cz.cuni.mff.java.anteditor;

import cz.cuni.mff.java.anteditor.component.LogPanel;
import cz.cuni.mff.java.anteditor.component.MainPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;

/**
 * Main class.
 *
 * @author Peter Grajcar
 */
public class Main {

    private String filename;
    private boolean saved;

    private JFrame mainFrame;
    private JMenuBar menuBar;
    private MainPanel mainPanel;
    private LogPanel logPanel;
    private JFileChooser fileChooser;

    /**
     * Creates an application user interface.
     */
    public void makeUI() {
        mainFrame = new JFrame("Ant Editor");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(new Dimension(800, 600));

        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Ant Build Script", "xml"));

        menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        mainFrame.add(menuBar, BorderLayout.NORTH);

        // Menu > File > New
        JMenuItem newButton = new JMenuItem("New");
        newButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newButton.addActionListener(e -> newFile());
        menu.add(newButton);

        // Menu > File > Open
        JMenuItem openButton = new JMenuItem("Open");
        openButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openButton.addActionListener(e -> {
            fileChooser.showOpenDialog(mainFrame);
            loadFile(fileChooser.getSelectedFile().getPath());
        });
        menu.add(openButton);

        // Menu > File > Save
        JMenuItem saveButton = new JMenuItem("Save");
        saveButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveButton.addActionListener((e) -> saveFile());
        menu.add(saveButton);
        menuBar.add(menu);


        mainPanel = new MainPanel();
        mainPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        mainPanel.getCodeEditor().addChangeListener(e -> fileEdited());
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

        //prevents loadFile showing save dialog
        saved = true;
        newFile();
    }

    /**
     * Loads file with given filename.
     *
     * @param filename filename including path
     */
    private void loadFile(String filename) {
        try(FileReader fileReader = new FileReader(filename)) {
            StringBuilder builder = new StringBuilder();
            int b;
            while ((b = fileReader.read()) != -1)
                builder.append((char) b);

            mainPanel.getCodeEditor().setText(builder.toString());
            logPanel.setEnabled(true);
            logPanel.setFilename(filename);
            logPanel.refresh();

            mainFrame.setTitle("Ant Editor - " + filename);

            saved = true;
            this.filename = filename;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates UI on script change.
     */
    public void fileEdited() {
        if(filename == null)
            mainFrame.setTitle("Ant Editor - untitled *");
        else
            mainFrame.setTitle("Ant Editor - " + filename + (saved ? "" : " *"));
        saved = false;
    }

    /**
     * Saves file. In case of new file a file chooser is opened. Saving is done
     * asynchronously.
     */
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
                    logPanel.refresh();
                    saved = true;
                }
        );
        save.execute();
    }

    /**
     * Creates a new file. If another file was previously opened a confirm dialog
     * opens with option of saving the file.
     */
    private void newFile() {
        if(!saved) {
            switch (JOptionPane.showConfirmDialog(mainFrame, "Save changes?")) {
                case JOptionPane.OK_OPTION:
                    saveFile();
                    break;
                case JOptionPane.CANCEL_OPTION:
                    return;
            }
        }
        mainFrame.setTitle("Ant Editor - untitled *");
        filename = null;
        saved = false;

        File template = new File(
                getClass().getClassLoader().getResource("build-template.xml").getFile()
        );
        try(FileReader reader = new FileReader(template)) {
            StringBuilder builder = new StringBuilder();
            int b;
            while((b = reader.read()) != -1)
                builder.append((char) b);
            mainPanel.getCodeEditor().setText(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates dependency graph based on content of the editor. In other
     * words the file need not to be saved in order to see changes in the
     * dependency graph.
     */
    public void loadGraph() {
        InputStream is = new ByteArrayInputStream(
                mainPanel.getCodeEditor().getText().getBytes()
        );
        mainPanel.getGraph().load(is);
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
