package component;

/**
 * created: 10/07/2020
 *
 * @author Peter Grajcar
 */
public interface CodeEditorSyntaxHighlighter {

    Iterable<CodeEditorHighlight> highlightCode(String code);

}
