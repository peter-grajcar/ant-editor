import component.CodeEditorPane;
import component.CodeEditorStyle;
import component.TargetGraph;
import org.jdom2.JDOMException;
import xml.XmlSyntaxHighlighter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * created: 10/07/2020
 *
 * @author Peter Grajcar
 */
public class Main {

    static final String CODE_EDITOR_CARD = "code-editor";
    static final String TARGET_GRAPH_CARD = "target-graph";

    private JMenuBar menuBar;

    private JPanel mainPanel;

    private JTabbedPane centralPanel;

    private CardLayout editorCardLayout;
    private JPanel editorPanel;
    private CodeEditorPane codeEditor;

    private TargetGraph graph;

    private JPanel logPanel;
    private JTextArea log;

    private JPanel structurePanel;
    private JTree structureTree;

    public void makeUI() {
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(new Dimension(800, 600));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        System.setProperty("apple.laf.useScreenMenuBar", "true");

        menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.add(new JMenuItem("New"));
        menu.add(new JMenuItem("Open"));
        menu.add(new JMenuItem("Save"));
        menuBar.add(menu);
        mainFrame.add(menuBar, BorderLayout.NORTH);

        logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout());
        logPanel.setPreferredSize(new Dimension(400, 100));
        mainPanel.add(logPanel, BorderLayout.SOUTH);

        log = new JTextArea();
        log.setEditable(false);
        log.setFont(new Font("Courier New", Font.PLAIN, 12));
        log.setText("> test");
        JScrollPane logScrollPane = new JScrollPane(log);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        structurePanel = new JPanel();
        structurePanel.setLayout(new BorderLayout());
        structurePanel.setPreferredSize(new Dimension(100, 400));
        mainPanel.add(structurePanel, BorderLayout.WEST);

        structureTree = new JTree();
        JScrollPane treeScrollPane = new JScrollPane(structureTree);
        treeScrollPane.setBorder(new EmptyBorder(0, 0, 0 ,0));
        structurePanel.add(treeScrollPane);

        editorPanel = new JPanel();
        editorPanel.setLayout(new BorderLayout());

        // Xml code editor
        codeEditor = new CodeEditorPane();
        JTextArea lineNumbers = new JTextArea("1\n2\n3");
        codeEditor.setLineNumbers(lineNumbers);

        JPanel codeEditorPanel = new JPanel();
        codeEditorPanel.setLayout(new BorderLayout());
        codeEditorPanel.add(codeEditor, BorderLayout.CENTER);
        codeEditorPanel.add(lineNumbers, BorderLayout.WEST);

        JScrollPane editorScrollPane = new JScrollPane(codeEditorPanel);
        editorScrollPane.setBorder(new MatteBorder(1, 0, 0, 0, new Color(0xC0C0C0)));
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.getVerticalScrollBar().setUnitIncrement(12);
        editorScrollPane.getHorizontalScrollBar().setUnitIncrement(12);

        editorPanel.add(editorScrollPane, BorderLayout.CENTER);

        loadFile("src/main/resources/build.xml");

        XmlSyntaxHighlighter xmlHighlighter = new XmlSyntaxHighlighter();
        xmlHighlighter.setCommentStyle(new CodeEditorStyle(false, true, Color.GRAY));
        xmlHighlighter.setElementStyle(new CodeEditorStyle(false, false, Color.BLUE));
        xmlHighlighter.setStringStyle(new CodeEditorStyle(false, false, Color.RED));
        xmlHighlighter.setAttributeStyle(new CodeEditorStyle(false, false, new Color(0, 128, 0)));

        codeEditor.setSyntaxHighlighter(xmlHighlighter);

        // Target Graph
        try {
            graph = new TargetGraph("src/main/resources/build.xml");
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }

        centralPanel = new JTabbedPane();
        centralPanel.addTab("Editor", editorPanel);
        centralPanel.addTab("Graph", graph);

        mainPanel.add(centralPanel);


        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }

    private void loadFile(String filename) {
        try(FileReader fileReader = new FileReader(filename)) {

            StringBuilder builder = new StringBuilder();
            int b;
            while ((b = fileReader.read()) != -1)
                builder.append((char) b);

            codeEditor.setText(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main()::makeUI);
    }

}
