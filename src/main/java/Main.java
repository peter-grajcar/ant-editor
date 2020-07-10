import component.CodeEditorPane;
import component.CodeEditorStyle;
import xml.XmlSyntaxHighlighter;

import javax.swing.*;
import java.awt.*;

/**
 * created: 10/07/2020
 *
 * @author Peter Grajcar
 */
public class Main {

    private JPanel mainPanel;
    private CodeEditorPane codeEditor;

    public void makeUI() {
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(new Dimension(600, 400));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Xml code editor
        codeEditor = new CodeEditorPane();

        XmlSyntaxHighlighter xmlHighlighter = new XmlSyntaxHighlighter();
        xmlHighlighter.setCommentStyle(new CodeEditorStyle(false, true, Color.GRAY));
        xmlHighlighter.setElementStyle(new CodeEditorStyle(false, false, Color.BLUE));

        codeEditor.setSyntaxHighlighter(xmlHighlighter);
        JScrollPane scrollPane = new JScrollPane(codeEditor);


        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainFrame.add(mainPanel);


        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main()::makeUI);
    }

}
