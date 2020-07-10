package component;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;

/**
 * A text component that supports code syntax highlighting.
 *
 * @author Peter Grajcar
 */
public class CodeEditorPane extends JTextPane implements DocumentListener {

    private CodeEditorSyntaxHighlighter syntaxHighlighter;

    private SimpleAttributeSet defaultStyle;

    public CodeEditorPane() {
        this.getDocument().addDocumentListener(this);
        this.setEditable(true);

        Font font = new Font("Courier New", Font.PLAIN, 14);
        this.setFont(font);

        defaultStyle = new CodeEditorStyle(
                false,
                false,
                Color.BLACK,
                new Color(0, 0, 0, 0)
        ).asAttributeSet();
    }

    public CodeEditorSyntaxHighlighter getSyntaxHighlighter() {
        return syntaxHighlighter;
    }

    public void setSyntaxHighlighter(CodeEditorSyntaxHighlighter syntaxHighlighter) {
        this.syntaxHighlighter = syntaxHighlighter;
    }

    private void applyStyle() {
        this.getStyledDocument().setCharacterAttributes(0, this.getText().length(), defaultStyle, false);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(this::applyStyle);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(this::applyStyle);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
}
