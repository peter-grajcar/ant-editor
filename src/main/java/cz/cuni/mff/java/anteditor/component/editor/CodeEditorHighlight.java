package cz.cuni.mff.java.anteditor.component.editor;

/**
 * This class contains information about highlighted area in the {@link CodeEditorPane}.
 *
 * @see CodeEditorPane
 * @author Peter Grajcar
 */
public class CodeEditorHighlight {

    private int start;
    private int end;
    private CodeEditorStyle style;

    /**
     * Creates a new highlighted area.
     *
     * @param start start of the area
     * @param end end of the area
     * @param style highlight style
     */
    public CodeEditorHighlight(int start, int end, CodeEditorStyle style) {
        this.start = start;
        this.end = end;
        this.style = style;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public CodeEditorStyle getStyle() {
        return style;
    }

    public void setStyle(CodeEditorStyle style) {
        this.style = style;
    }
}
