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

    /**
     * Returns start of the highlighted area.
     *
     * @return start of the highlighted area
     */
    public int getStart() {
        return start;
    }

    /**
     * Sets start of the highlighted area.
     *
     * @param start start of the highlighted area
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * Returns end of the highlighted area.
     *
     * @return end of the highlighted area
     */
    public int getEnd() {
        return end;
    }

    /**
     * Sets end of the highlighted area.
     *
     * @param end end of the highlighted area
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * Returns style of the highlighted area.
     *
     * @return style of the highlighted area
     */
    public CodeEditorStyle getStyle() {
        return style;
    }

    /**
     * Sets style of the highlighted area.
     *
     * @param style style of the highlighted area
     */
    public void setStyle(CodeEditorStyle style) {
        this.style = style;
    }
}
