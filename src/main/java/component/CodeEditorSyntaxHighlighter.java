package component;

/**
 * Interface for code highlighting processors.
 *
 * @author Peter Grajcar
 */
public interface CodeEditorSyntaxHighlighter {

    /**
     * Processes code and lists parts of code to highlight.
     *
     * @param code Code to highlight
     * @return Parts of code which are to be highlighted
     */
    Iterable<CodeEditorHighlight> highlightCode(String code);

}
