package component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * created: 09/08/2020
 *
 * @author Peter Grajcar
 */
public class StructurePanel extends JPanel {

    private JTree structureTree;

    public StructurePanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 400));

        structureTree = new JTree();
        JScrollPane treeScrollPane = new JScrollPane(structureTree);
        treeScrollPane.setBorder(new EmptyBorder(0, 0, 0 ,0));
        add(treeScrollPane);
    }

    public JTree getStructureTree() {
        return structureTree;
    }
}

