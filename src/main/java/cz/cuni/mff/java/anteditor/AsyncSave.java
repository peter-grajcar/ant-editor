package cz.cuni.mff.java.anteditor;

import javax.swing.*;
import java.io.*;

/**
 * Performs an asynchronous file save.
 *
 * @author Peter Grajcar
 */
public class AsyncSave extends SwingWorker<Boolean, Void> {

    private final String filename;
    private final InputStream inputStream;
    private Exception exception;
    private Runnable callback;

    public AsyncSave(String filename, InputStream inputStream) {
        this.filename = filename;
        this.inputStream = inputStream;
    }

    public AsyncSave(String filename, InputStream inputStream, Runnable callback) {
        this.filename = filename;
        this.inputStream = inputStream;
        this.callback = callback;
    }

    public AsyncSave(String filename, String content) {
        this.filename = filename;
        this.inputStream = new ByteArrayInputStream(content.getBytes());
    }

    public AsyncSave(String filename, String content, Runnable callback) {
        this.filename = filename;
        this.inputStream = new ByteArrayInputStream(content.getBytes());
        this.callback = callback;
    }


    @Override
    protected Boolean doInBackground() throws Exception {
        try(FileOutputStream outputStream = new FileOutputStream(filename)) {
            inputStream.transferTo(outputStream);
            return true;
        } catch (IOException e) {
            this.exception = e;
        }
        return false;
    }

    @Override
    protected void done() {
        callback.run();
    }

    public Exception getException() {
        return exception;
    }
}
