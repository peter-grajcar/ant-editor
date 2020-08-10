package cz.cuni.mff.java.component.editor;

/**
 * created: 10/07/2020
 *
 * @author Peter Grajcar
 */
public class CodeEditorHighlight {

    private int start;
    private int end;
    private CodeEditorStyle style;

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
