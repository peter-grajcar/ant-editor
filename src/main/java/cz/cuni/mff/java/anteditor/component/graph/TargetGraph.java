package cz.cuni.mff.java.anteditor.component.graph;

import cz.cuni.mff.java.anteditor.algorithm.TopoSort;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom Swing cz.cuni.mff.java.component which visualises Ant targets as an oriented
 * graph.
 *
 * @author Peter Grajcar
 */
public class TargetGraph extends JPanel implements MouseMotionListener {

    /**
     * A class representing a single Ant target dependency graph node.
     */
    private static class TargetNode {
        String name;
        String[] depends;
        int x, y;
        int depth;

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
    private Exception exception;

    /**
     * Creates a new Ant target dependency graph component.
     */
    public TargetGraph() {
        addMouseMotionListener(this);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Loads and processes Ant build script, creates nodes of the
     * dependency graph and sets their positions.
     *
     * @param is Ant build script input
     */
    public void load(InputStream is) {
        exception = null;
        try {
            SAXBuilder builder = new SAXBuilder();
            antBuildXml = builder.build(is);
        } catch (IOException | JDOMException e) {
            targets = new TopoSort.Node[0];
            exception = e;
            return;
        }

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

        // Make name-target node map
        int maxDepth = 0;
        for (TopoSort.Node<TargetNode> node : targets) {
            for (String depend : node.getValue().depends) {
                TopoSort.Node<TargetNode> dependNode = targetMap.get(depend);
                node.getEdges().add(dependNode);

                node.getValue().depth = Math.max(node.getValue().depth, dependNode.getValue().depth + 1);
                maxDepth = Math.max(maxDepth, node.getValue().depth);
            }
        }

        targets = TopoSort.sort(targets);

        int offsetX = 30;
        int offsetY = 30;
        int[] levels = new int[maxDepth + 1];

        // Compute position of each node
        for (TopoSort.Node<TargetNode> node : targets) {
            int depth = node.getValue().depth;
            node.getValue().x = depth * (offsetX + nodeWidth);
            node.getValue().y = levels[depth]  * (offsetY + nodeHeight);
            ++levels[depth];
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if(exception != null) {
            g2.setColor(Color.RED);
            g2.drawString(exception.getMessage(), 50, 50);
            return;
        }

        for (TopoSort.Node<TargetNode> node : targets) {
            int x = node.getValue().x;
            int y = node.getValue().y;

            g2.setColor(Color.BLACK);
            for (TopoSort.Node<TargetNode> parent : node.getEdges()) {
                float dx = parent.getValue().x - x;
                float dy = parent.getValue().y - y;
                float len = (float) Math.sqrt(dx * dx + dy * dy);

                float vx = dx * 10 / len;
                float vy = dy * 10 / len;
                float ux = -vy / 2;
                float uy = vx / 2;

                float midx = x + nodeWidth / 2 + positionX + dx / 2;
                float midy = y + positionY + dy / 2;

                Polygon polygon = new Polygon();
                polygon.addPoint((int) (midx + vx), (int) (midy + vy));
                polygon.addPoint((int) (midx + ux), (int) (midy + uy));
                polygon.addPoint((int) (midx - ux), (int) (midy - uy));
                g2.fillPolygon(polygon);


                g2.drawLine(
                        x + nodeWidth / 2 + positionX,
                        y + positionY,
                        parent.getValue().x + nodeWidth / 2 + positionX,
                        parent.getValue().y + positionY
                );
            }
        }


        for (TopoSort.Node<TargetNode> node : targets) {
            int x = node.getValue().x;
            int y = node.getValue().y;

            g2.setColor(Color.WHITE);
            g2.fillRect(
                    x + positionX,
                    y - nodeHeight / 2 + positionY,
                    nodeWidth,
                    nodeHeight
            );

            g2.setColor(Color.BLACK);
            g2.drawString(
                    node.getValue().name,
                    x + 10 + positionX,
                    y + positionY
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

    /**
     * Returns an exception from the last {@link #load(InputStream)} call
     *
     * @return exception.
     */
    public Exception getException() {
        return exception;
    }
}
