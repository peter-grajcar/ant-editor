package component.graph;

import algorithm.TopoSort;
import org.apache.tools.ant.Target;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom Swing component which visualises Ant targets as an oriented
 * graph.
 *
 * @author Peter Grajcar
 */
public class TargetGraph extends JPanel implements MouseMotionListener {

    private static class TargetNode {
        String name;
        String[] depends;
        int x, y;
        int depth;
        int dependentCount;

        public TargetNode(String name, String[] depends) {
            this.name = name;
            this.depends = depends;
        }
    }

    private int nodeWidth = 150;
    private int nodeHeight = 50;
    private int positionX = 0;
    private int positionY = 0;

    private Document antBuildXml;
    private TopoSort.Node<TargetNode>[] targets;

    public TargetGraph(String filename) throws IOException, JDOMException {
        load(filename);

        addMouseMotionListener(this);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void load(String filename) throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        antBuildXml = builder.build(filename);

        Element root = antBuildXml.getRootElement();
        List<Element> elements = root.getChildren("target");

        targets = new TopoSort.Node[elements.size()];
        Map<String, TopoSort.Node<TargetNode>> targetMap = new HashMap<>();

        for(int i = 0; i < targets.length; ++i) {
            Element element = elements.get(i);
            String name = element.getAttribute("name").getValue();
            String[] depends;

            Attribute attr = element.getAttribute("depends");
            if(attr != null)
                depends = attr.getValue().split(",");
            else
                depends = new String[0];

            TargetNode targetNode = new TargetNode(name, depends);
            TopoSort.Node<TargetNode> node = new TopoSort.Node<>(targetNode);

            targets[i] = node;
            targetMap.put(name, node);
        }


        for (TopoSort.Node<TargetNode> node : targets) {
            for (String depend : node.getValue().depends) {
                TopoSort.Node<TargetNode> dependNode = targetMap.get(depend);
                node.getEdges().add(dependNode);
                node.getValue().depth = Math.max(node.getValue().depth, dependNode.getValue().depth + 1);
            }
        }

        targets = TopoSort.sort(targets);

        int offsetX = 20;
        int offsetY = 10;
        int height = 50;

        for (TopoSort.Node<TargetNode> node : targets) {
            System.out.println(node.getValue().name + ", " + node.getValue().depth);

            if (node.getEdges().size() == 0) {
                node.getValue().y = height;
                height += nodeHeight + offsetY;
            }

            node.getValue().x = offsetX;
            for (TopoSort.Node<TargetNode> parent : node.getEdges()) {
                int newX = parent.getValue().x + nodeWidth + offsetX;
                if(newX > node.getValue().x)
                    node.getValue().x = newX;

                node.getValue().y = parent.getValue().y + parent.getValue().dependentCount * (nodeHeight + offsetY);

                ++parent.getValue().dependentCount;
                height = Math.max(height, parent.getValue().dependentCount * (nodeHeight + offsetY));
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (TopoSort.Node<TargetNode> node : targets) {
            g2.setColor(Color.BLACK);
            for (TopoSort.Node<TargetNode> parent : node.getEdges()) {
                g2.drawLine(
                        node.getValue().x + nodeWidth / 2 + positionX,
                        node.getValue().y + positionY,
                        parent.getValue().x + nodeWidth / 2 + positionX,
                        parent.getValue().y + positionY
                );
            }
        }

        for (TopoSort.Node<TargetNode> node : targets) {
            g2.setColor(Color.WHITE);
            g2.fillRect(
                    node.getValue().x + positionX,
                    node.getValue().y - nodeHeight / 2 + positionY,
                    nodeWidth,
                    nodeHeight
            );

            g2.setColor(Color.BLACK);
            g2.drawString(
                    node.getValue().name,
                    node.getValue().x + 10 + positionX,
                    node.getValue().y + positionY
            );
        }
    }

    private int prevX = -1;
    private int prevY = -1;

    @Override
    public void mouseDragged(MouseEvent e) {
        if(prevX != -1 && prevY != -1) {
            positionX += e.getX() - prevX;
            positionY += e.getY() - prevY;
        }
        prevX = e.getX();
        prevY = e.getY();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        prevX = e.getX();
        prevY = e.getY();
    }

}
