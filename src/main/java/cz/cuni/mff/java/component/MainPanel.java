package cz.cuni.mff.java.component;

import cz.cuni.mff.java.component.editor.CodeEditorPane;
import cz.cuni.mff.java.component.editor.CodeEditorStyle;
import cz.cuni.mff.java.component.graph.TargetGraph;
import org.jdom2.JDOMException;
import cz.cuni.mff.java.xml.XmlSyntaxHighlighter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.io.IOException;

/**
 * created: 09/08/2020
 *
 * @author Peter Grajcar
 */
public class MainPanel extends JTabbedPane {

    private JPanel editorPanel;
    private CodeEditorPane codeEditor;
    private TargetGraph graph;

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
        try {
            graph = new TargetGraph("src/main/resources/build.xml");
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }

        addTab("Editor", editorPanel);
        addTab("Graph", graph);
        addChangeListener(this::tabChangeListener);
        setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    /**
     *
     * @param e Change cz.cuni.mff.java.event
     */
    public void tabChangeListener(ChangeEvent e) {
        JTabbedPane pane = (JTabbedPane) e.getSource();
        System.out.println(pane.getSelectedIndex());
    }

    public JPanel getEditorPanel() {
        return editorPanel;
    }


    public CodeEditorPane getCodeEditor() {
        return codeEditor;
    }


    public TargetGraph getGraph() {
        return graph;
    }

}
