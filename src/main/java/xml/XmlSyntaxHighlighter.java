package xml;

import component.CodeEditorHighlight;
import component.CodeEditorStyle;
import component.CodeEditorSyntaxHighlighter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * created: 10/07/2020
 *
 * @author Peter Grajcar
 */
public class XmlSyntaxHighlighter implements CodeEditorSyntaxHighlighter {

    private static class StringStream implements Iterable<Character>, Iterator<Character> {
        private String str;
        private int index;

        public StringStream(String str) {
            this.str = str;
        }

        public char peek() {
            if(index >= str.length())
                return 0;
            return str.charAt(index);
        }

        public char peek(int offset) {
            if(index + offset >= str.length())
                return 0;
            return str.charAt(index + offset);
        }

        public void reset() {
            index = 0;
        }

        public void skip(int offset) {
            index = Math.min(index + offset, str.length());
        }

        public int position() {
            return index;
        }

        @Override
        public boolean hasNext() {
            return index < str.length();
        }

        @Override
        public Character next() {
            return str.charAt(index++);
        }

        @Override
        public Iterator<Character> iterator() {
            return this;
        }
    }

    private CodeEditorStyle commentStyle;
    private CodeEditorStyle elementStyle;

    @Override
    public Iterable<CodeEditorHighlight> highlightCode(String code) {
        StringStream stream = new StringStream(code);

        List<CodeEditorHighlight> highlights = new ArrayList<>();

        for(char ch : stream) {
            if(isWhitespace(ch)) continue;

            if(ch == '<') {
                if(stream.peek() == '!') {
                    CodeEditorHighlight comment = processComment(stream.position() - 1, stream);
                    if(comment != null) highlights.add(comment);
                } else {
                    List<CodeEditorHighlight> element = processElement(stream.position() - 1, stream);
                    if(element != null) highlights.addAll(element);
                }
            }

            //TODO:
        }

        return highlights;
    }

    private CodeEditorHighlight processComment(int start, StringStream stream) {
        if(stream.peek() != '!' || stream.peek(1) != '-' || stream.peek(2) != '-')
            return null;

        stream.skip(3);

        while((stream.peek() != '-' || stream.peek(1) != '-' || stream.peek(2) != '>') && stream.hasNext())
            stream.next();

        stream.skip(3);

        return new CodeEditorHighlight(start, stream.position(), commentStyle);
    }

    private List<CodeEditorHighlight> processElement(int start, StringStream stream) {
        return null;
    }


    private boolean isWhitespace(char ch) {
        return ch == 0x20 || ch == 0x09 || ch == 0x0D || ch == 0x0A;
    }

    private boolean isNameChar(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '.' || ch == '-' || ch == '_' || ch == ':';
    }

    public CodeEditorStyle getCommentStyle() {
        return commentStyle;
    }

    public void setCommentStyle(CodeEditorStyle commentStyle) {
        this.commentStyle = commentStyle;
    }

    public CodeEditorStyle getElementStyle() {
        return elementStyle;
    }

    public void setElementStyle(CodeEditorStyle elementStyle) {
        this.elementStyle = elementStyle;
    }
}
