package cz.cuni.mff.java.xml;

import cz.cuni.mff.java.component.editor.CodeEditorHighlight;
import cz.cuni.mff.java.component.editor.CodeEditorStyle;
import cz.cuni.mff.java.component.editor.CodeEditorSyntaxHighlighter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class processes XML code and lists parts of code which are to be highlighted.
 *
 * @author Peter Grajcar
 */
public class XmlSyntaxHighlighter implements CodeEditorSyntaxHighlighter {

    /**
     *
     */
    private static class StringStream implements Iterable<Character>, Iterator<Character> {
        private String str;
        private int index;

        /**
         *
         * @param str
         */
        public StringStream(String str) {
            this.str = str;
        }

        /**
         *
         * @return
         */
        public char peek() {
            if(index >= str.length())
                return 0;
            return str.charAt(index);
        }

        /**
         *
         * @param offset
         * @return
         */
        public char peek(int offset) {
            if(index + offset >= str.length())
                return 0;
            return str.charAt(index + offset);
        }

        /**
         *
         */
        public void reset() {
            index = 0;
        }

        /**
         *
         * @param offset
         */
        public void skip(int offset) {
            index = Math.min(index + offset, str.length());
        }

        /**
         *
         * @return
         */
        public int position() {
            return index;
        }

        /**
         *
         * @param position
         */
        public void move(int position) {
            this.index = position;
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
    private CodeEditorStyle stringStyle;
    private CodeEditorStyle attributeStyle;

    @Override
    public Iterable<CodeEditorHighlight> highlightCode(String code) {
        StringStream stream = new StringStream(code);

        List<CodeEditorHighlight> highlights = new ArrayList<>();

        for(char ch : stream) {
            if(isWhitespace(ch)) continue;

            if(ch == '<') {
                if(stream.peek() == '!') {
                    CodeEditorHighlight comment = processComment(stream.position(), stream);
                    if(comment != null) highlights.add(comment);
                } else if(stream.peek() == '/') {
                    CodeEditorHighlight closeTag = processCloseTag(stream.position(), stream);
                    if(closeTag != null) highlights.add(closeTag);
                } else {
                    List<CodeEditorHighlight> element = processStartTag(stream.position(), stream);
                    if(element != null) highlights.addAll(element);
                }
            }

            //TODO:
        }

        return highlights;
    }

    /**
     *
     * @param start
     * @param stream
     * @return
     */
    private CodeEditorHighlight processComment(int start, StringStream stream) {
        if(stream.peek() != '!' || stream.peek(1) != '-' || stream.peek(2) != '-')
            return null;

        stream.skip(3);

        while((stream.peek() != '-' || stream.peek(1) != '-' || stream.peek(2) != '>') && stream.hasNext())
            stream.next();

        stream.skip(3);

        return new CodeEditorHighlight(start - 1, stream.position(), commentStyle);
    }

    /**
     *
     * @param start
     * @param stream
     * @return
     */
    private List<CodeEditorHighlight> processStartTag(int start, StringStream stream) {
        List<CodeEditorHighlight> highlights = new ArrayList<>();

        if(!Character.isLetter(stream.peek()) && stream.peek() != '_' && stream.peek() != ':')
            return null;

        stream.next();

        while(stream.hasNext() && isNameChar(stream.peek()))
            stream.next();

        skipWhitespace(stream);

        // attribute processing
        while(isNameChar(stream.peek())) {
            int attrStart = stream.position();
            while(isNameChar(stream.peek())) stream.next();
            highlights.add(new CodeEditorHighlight(attrStart, stream.position(), attributeStyle));

            skipWhitespace(stream);
            if(stream.peek() == '=') {
                stream.next();
                skipWhitespace(stream);
                CodeEditorHighlight stringHighlight = processString(stream.position(), stream);
                if (stringHighlight != null) {
                    highlights.add(stringHighlight);
                }
            }
            skipWhitespace(stream);
        }

        if(stream.peek() == '/' && stream.peek(1) == '>') {
            stream.skip(2);
        } else if(stream.peek() == '>') {
            stream.next();
        } else {
            return null;
        }

        CodeEditorHighlight highlight = new CodeEditorHighlight(start - 1, stream.position(), elementStyle);
        highlights.add(0, highlight);

        return highlights;
    }

    /**
     *
     * @param start
     * @param stream
     * @return
     */
    private CodeEditorHighlight processCloseTag(int start, StringStream stream) {
        if(stream.peek() != '/')
            return null;

        stream.skip(1);

        skipWhitespace(stream);

        while(isNameChar(stream.peek()) || stream.peek() == '_' || stream.peek() == ':') {
            stream.next();
        }

        skipWhitespace(stream);

        if(stream.peek() != '>') {
            stream.move(start);
            return null;
        }

        stream.skip(1);

        return new CodeEditorHighlight(start - 1, stream.position(), elementStyle);
    }

    /**
     *
     * @param start
     * @param stream
     * @return
     */
    private CodeEditorHighlight processString(int start, StringStream stream) {
        if(stream.peek() != '\"' && stream.peek() != '\'')
            return null;

        char quote = stream.peek();

        if(!stream.hasNext())
            return null;

        stream.next();

        int backslashes = 0;
        while(!(stream.peek() == quote && (backslashes & 1) == 0)) {
            if(stream.peek() == '\\') ++backslashes;
            else backslashes = 0;

            if(!stream.hasNext()) {
                stream.move(start);
                return null;
            }

            stream.next();
        }

        if(stream.peek() != quote) {
            stream.move(start);
            return null;
        }

        stream.next();
        return new CodeEditorHighlight(start, stream.position(), stringStyle);
    }

    /**
     *
     * @param stream
     */
    private void skipWhitespace(StringStream stream) {
        while(stream.hasNext() && isWhitespace(stream.peek()))
            stream.next();
    }

    /**
     *
     * @param ch
     * @return
     */
    private boolean isWhitespace(char ch) {
        return ch == 0x20 || ch == 0x09 || ch == 0x0D || ch == 0x0A;
    }

    /**
     *
     * @param ch
     * @return
     */
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

    public CodeEditorStyle getStringStyle() {
        return stringStyle;
    }

    public void setStringStyle(CodeEditorStyle stringStyle) {
        this.stringStyle = stringStyle;
    }

    public CodeEditorStyle getAttributeStyle() {
        return attributeStyle;
    }

    public void setAttributeStyle(CodeEditorStyle attributeStyle) {
        this.attributeStyle = attributeStyle;
    }
}
