package cz.cuni.mff.java.anteditor.component;

import cz.cuni.mff.java.anteditor.component.editor.CodeEditorPane;
import cz.cuni.mff.java.anteditor.component.editor.CodeEditorStyle;
import cz.cuni.mff.java.anteditor.component.graph.TargetGraph;
import cz.cuni.mff.java.anteditor.xml.XmlSyntaxHighlighter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * Main panel contains code editor and dependency graph.
 *
 * @author Peter Grajcar
 */
public class MainPanel extends JTabbedPane {

    private JPanel editorPanel;
    private CodeEditorPane codeEditor;
    private TargetGraph graph;

    /**
     * Creates a new main panel.
     */
    public MainPanel() {
        editorPanel = new JPanel();
        editorPanel.setLayout(new BorderLayout());

        // Xml code editor
        codeEditor = new CodeEditorPane();
        JTextArea lineNumbers = new JTextArea();
        codeEditor.setLineNumbers(lineNumbers);
        codeEditor.setBorder(new EmptyBorder(0, 0, 0, 200));

        JPanel codeEditorPanel = new JPanel();
        codeEditorPanel.setLayout(new BorderLayout());
        codeEditorPanel.add(codeEditor, BorderLayout.CENTER);
        codeEditorPanel.add(lineNumbers, BorderLayout.WEST);

        JScrollPane editorScrollPane = new JScrollPane(codeEditorPanel);
        editorScrollPane.setBorder(new MatteBorder(1, 0, 0, 0, new Color(0xC0C0C0)));
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.getVerticalScrollBar().setUnitIncrement(12);
        editorScrollPane.getHorizontalScrollBar().setUnitIncrement(12);

        editorScrollPane.getVerticalScrollBar().setValueIsAdjusting(false);
        editorScrollPane.getHorizontalScrollBar().setValueIsAdjusting(false);

        editorPanel.add(editorScrollPane, BorderLayout.CENTER);

        XmlSyntaxHighlighter xmlHighlighter = new XmlSyntaxHighlighter();
        xmlHighlighter.setCommentStyle(new CodeEditorStyle(false, true, Color.GRAY));
        xmlHighlighter.setElementStyle(new CodeEditorStyle(false, false, Color.BLUE));
        xmlHighlighter.setStringStyle(new CodeEditorStyle(false, false, Color.RED));
        xmlHighlighter.setAttributeStyle(new CodeEditorStyle(false, false, new Color(0, 128, 0)));

        codeEditor.setSyntaxHighlighter(xmlHighlighter);

        // Target Graph
        graph = new TargetGraph();

        addTab("Editor", editorPanel);
        addTab("Graph", graph);
        addChangeListener(this::tabChangeHandler);
        setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    /**
     * Handles tab change event.
     *
     * @param e tab hange event
     */
    public void tabChangeHandler(ChangeEvent e) {
        JTabbedPane pane = (JTabbedPane) e.getSource();
        //TODO: reload graph
    }

    /**
     * Returns the code editor component.
     *
     * @return code editor component
     */
    public CodeEditorPane getCodeEditor() {
        return codeEditor;
    }

    /**
     * Returns the dependency graph component.
     *
     * @return dependency graph component
     */
    public TargetGraph getGraph() {
        return graph;
    }

}
