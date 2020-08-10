package cz.cuni.mff.java.component.editor;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A text cz.cuni.mff.java.component that supports code syntax highlighting.
 *
 * @author Peter Grajcar
 */
public class CodeEditorPane extends JTextPane implements DocumentListener, CaretListener {

    private CodeEditorSyntaxHighlighter syntaxHighlighter;

    private SimpleAttributeSet defaultStyle;

    private JTextArea lineNumbers;

    private List<ChangeListener> changeListenerList;

    /**
     *
     */
    public CodeEditorPane() {
        this.changeListenerList = new ArrayList<>();
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

        addCaretListener(this);
    }

    /**
     *
     * @return
     */
    public CodeEditorSyntaxHighlighter getSyntaxHighlighter() {
        return syntaxHighlighter;
    }

    /**
     *
     * @param syntaxHighlighter
     */
    public void setSyntaxHighlighter(CodeEditorSyntaxHighlighter syntaxHighlighter) {
        this.syntaxHighlighter = syntaxHighlighter;
    }

    /**
     *
     */
    private void applyStyle() {
        getStyledDocument().setCharacterAttributes(0, this.getText().length(), defaultStyle, true);

        if(syntaxHighlighter == null) return;

        try {
            Iterable<CodeEditorHighlight> highlights = syntaxHighlighter.highlightCode(
                    getDocument().getText(0, getDocument().getLength())
            );

            for(CodeEditorHighlight highlight : highlights) {
                int length = highlight.getEnd() - highlight.getStart();
                getStyledDocument().setCharacterAttributes(highlight.getStart(), length, highlight.getStyle().asAttributeSet(), true);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void updateLineNumbers() {
        try {
            StringBuilder builder = new StringBuilder();
            long lineNum = getDocument().getText(0, getDocument().getLength())
                    .lines()
                    .count();
            for(long i = 0; i < lineNum; ++i)
                builder.append(i + 1).append('\n');
            lineNumbers.setText(builder.toString());
        } catch (BadLocationException badLocationException) {
            badLocationException.printStackTrace();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(this::applyStyle);
        if(lineNumbers != null)
            updateLineNumbers();

        for(ChangeListener listener : changeListenerList)
            listener.stateChanged(new ChangeEvent(this));
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(this::applyStyle);
        if(lineNumbers != null)
            updateLineNumbers();

        for(ChangeListener listener : changeListenerList)
            listener.stateChanged(new ChangeEvent(this));
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        for(ChangeListener listener : changeListenerList)
            listener.stateChanged(new ChangeEvent(this));
    }

    /**
     *
     * @return
     */
    public JTextArea getLineNumbers() {
        return lineNumbers;
    }

    /**
     *
     * @param lineNumbers
     */
    public void setLineNumbers(JTextArea lineNumbers) {
        this.lineNumbers = lineNumbers;
        lineNumbers.setFont(getFont());
        lineNumbers.setEditable(false);
        lineNumbers.setBackground(new Color(0xECECEC));
        lineNumbers.setBorder(new MatteBorder(0, 0, 0, 1, new Color(0xC0C0C0)));
        lineNumbers.setMargin(new Insets(0, 2, 0, 0));
        lineNumbers.setPreferredSize(new Dimension(48, 1));
        lineNumbers.setForeground(new Color(0xA9A9A9));

        // Disables text selection
        lineNumbers.setHighlighter(null);
        lineNumbers.setCaret(new DefaultCaret() {
            @Override
            public int getMark() {
                return getDot();
            }
        });
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        // TODO:
    }

    public void addChangeListener(ChangeListener listener) {
        changeListenerList.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeListenerList.remove(listener);
    }
}
