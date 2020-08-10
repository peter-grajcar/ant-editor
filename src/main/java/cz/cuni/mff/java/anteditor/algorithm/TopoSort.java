package cz.cuni.mff.java.anteditor.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Topological sorting of a graph.
 *
 * @author Peter Grajcar
 */
public class TopoSort {

    /**
     * Node mark
     */
    private enum Mark {NONE, VISITED, CLOSED}

    /**
     * Oriented graph node.
     *
     * @param <T> Node value type
     */
    public static class Node<T> {
        T value;
        List<Node<T>> edges;
        Mark mark;

        public Node() {
            edges = new ArrayList<>();
            mark = Mark.NONE;
        }

        public Node(T value) {
            this();
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public List<Node<T>> getEdges() {
            return edges;
        }

        public void setEdges(List<Node<T>> edges) {
            this.edges = edges;
        }
    }

    /**
     * Sorts an oriented graph topologically.
     *
     * @param <T>   Node type
     * @param nodes Graph nodes
     * @return Sorted nodes
     */
    public static <T> Node[] sort(Node<T>[] nodes) {
        Node[] sorted = new Node[nodes.length];

        // Clear marks
        for (Node<T> tNode : nodes) tNode.mark = Mark.NONE;

        int idx = 0;

        for (Node<T> start : nodes) {
            if (start.mark != Mark.NONE)
                continue;

            Stack<Node<T>> stack = new Stack<>();
            stack.push(start);
            while (!stack.isEmpty()) {
                Node<T> node = stack.pop();

                if (Mark.CLOSED.equals(node.mark))
                    continue;

                if (Mark.NONE.equals(node.mark)) {
                    node.mark = Mark.VISITED;
                    stack.add(node);
                    stack.addAll(node.edges);
                    continue;
                }

                if (node.edges.stream().allMatch(n -> Mark.CLOSED.equals(n.mark))) {
                    node.mark = Mark.CLOSED;
                    sorted[idx++] = node;
                    continue;
                }

                throw new RuntimeException("Cyclic dependency");
            }
        }

        return sorted;
    }

}