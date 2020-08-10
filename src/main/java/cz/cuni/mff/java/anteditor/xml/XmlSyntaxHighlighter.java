package cz.cuni.mff.java.anteditor.xml;

import cz.cuni.mff.java.anteditor.component.editor.CodeEditorHighlight;
import cz.cuni.mff.java.anteditor.component.editor.CodeEditorStyle;
import cz.cuni.mff.java.anteditor.component.editor.CodeEditorSyntaxHighlighter;

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
         * Creates a new stream.
         *
         * @param str
         */
        public StringStream(String str) {
            this.str = str;
        }

        /**
         * Returns character at current position without moving.
         *
         * @return
         */
        public char peek() {
            if(index >= str.length())
                return 0;
            return str.charAt(index);
        }

        /**
         * Returns character at a position with given offset from the current position
         * without moving.
         *
         * @param offset offset from the current position
         * @return character at offset position
         */
        public char peek(int offset) {
            if(index + offset >= str.length())
                return 0;
            return str.charAt(index + offset);
        }

        /**
         * Resets the index to the start.
         */
        public void reset() {
            index = 0;
        }

        /**
         * Skips a certain number of characters.
         *
         * @param offset a number of characters to skip
         */
        public void skip(int offset) {
            index = Math.min(index + offset, str.length());
        }

        /**
         * Returns current position.
         *
         * @return current position
         */
        public int position() {
            return index;
        }

        /**
         * Moves to a new position.
         *
         * @param position absolute position.
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

    /**
     * Highlights XML code.
     *
     * @param code Code to highlight
     * @return a list of highlighted areas
     */
    @Override
    public Iterable<CodeEditorHighlight> highlightCode(String code) {
        StringStream stream = new StringStream(code);

        List<CodeEditorHighlight> highlights = new ArrayList<>();

        for(char ch : stream) {
            if(Character.isWhitespace(ch)) continue;

            if(ch == '<') {
                if(stream.peek() == '!') {
                    CodeEditorHighlight comment = processComment(stream.position(), stream);
                    if(comment != null) highlights.add(comment);
                } else if(stream.peek() == '/') {
                    CodeEditorHighlight closeTag = processCloseTag(stream.position(), stream);
                    if(closeTag != null) highlights.add(closeTag);
                } else {
                    List<CodeEditorHighlight> element = processOpenTag(stream.position(), stream);
                    if(element != null) highlights.addAll(element);
                }
            }

            //TODO:
        }

        return highlights;
    }

    /**
     * Processes a XML comment. Returns null if unable to process comment.
     *
     * @param start start position of the {@code StringStream}
     * @param stream string stream
     * @return highlighted area
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
     * Processes a XML opening tag along with its attributes.
     *
     * @param start start position of the {@code StringStream}
     * @param stream string stream
     * @return a list of highlighted areas
     */
    private List<CodeEditorHighlight> processOpenTag(int start, StringStream stream) {
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
     * Processes XML closing tag.
     *
     * @param start start position of the {@code StringStream}
     * @param stream string stream
     * @return highlighted area
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
     * Processes a XML string.
     *
     * @param start start position of the {@code StringStream}
     * @param stream string stream
     * @return highlighted area
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
     * Skips whitespaces in the stream.
     *
     * @param stream string stream
     */
    private void skipWhitespace(StringStream stream) {
        while(stream.hasNext() && Character.isWhitespace(stream.peek()))
            stream.next();
    }

    /**
     * Returns true if the character can be present in XML name.
     *
     * @param ch character
     * @return whether ch can be in XML name
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
