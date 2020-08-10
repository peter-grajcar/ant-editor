package cz.cuni.mff.java.anteditor.component.editor;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

/**
 * This class represents a style of a highlighted area in the {@link CodeEditorPane}.
 *
 * @see CodeEditorPane
 * @author Peter Grajcar
 */
public class CodeEditorStyle {

    private boolean bold;
    private boolean italic;
    private Color colour;
    private Color backgroundColour;

    public CodeEditorStyle(boolean bold, boolean italic, Color colour, Color backgroundColour) {
        this.bold = bold;
        this.italic = italic;
        this.colour = colour;
        this.backgroundColour = backgroundColour;
    }

    public CodeEditorStyle(boolean bold, boolean italic, Color colour) {
        this.bold = bold;
        this.italic = italic;
        this.colour = colour;
        this.backgroundColour = new Color(0, 0, 0, 0);
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public Color getBackgroundColour() {
        return backgroundColour;
    }

    public void setBackgroundColour(Color backgroundColour) {
        this.backgroundColour = backgroundColour;
    }

    public SimpleAttributeSet asAttributeSet() {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setItalic(attributeSet, isItalic());
        StyleConstants.setBold(attributeSet, isBold());
        StyleConstants.setForeground(attributeSet, getColour());
        StyleConstants.setBackground(attributeSet, getBackgroundColour());
        return attributeSet;
    }

}
