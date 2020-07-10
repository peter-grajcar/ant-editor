package xml;

import component.CodeEditorStyle;
import component.CodeEditorSyntaxHighlighter;

import java.util.ArrayList;
import java.util.List;

/**
 * created: 10/07/2020
 *
 * @author Peter Grajcar
 */
public class XmlSyntaxHighlighter implements CodeEditorSyntaxHighlighter {

    @Override
    public Iterable<CodeEditorStyle> highlightCode(String code) {
        List<CodeEditorStyle> tokens = new ArrayList<>();

        return tokens;
    }



}
