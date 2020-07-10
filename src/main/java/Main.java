import component.CodeEditorPane;

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

        codeEditor = new CodeEditorPane();
        JScrollPane scrollPane = new JScrollPane(codeEditor);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainFrame.add(mainPanel);


        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main()::makeUI);
    }

}
