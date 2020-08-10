package cz.cuni.mff.java.anteditor.ant;

import cz.cuni.mff.java.anteditor.event.AntLogListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that runs the Ant build scripts.
 *
 * @author Peter Grajcar
 */
public class AntRunner extends SwingWorker<Void, String> {

    private String filename;

    private String targetName;

    private Runnable callback;

    private List<AntLogListener> logListenerList;

    /**
     * Creates a new {@code AntRunner} which will run the specified
     * target of the specified script on execution.
     *
     * @param filename Ant build script including file path
     * @param targetName Ant target name
     */
    public AntRunner(String filename, String targetName) {
        this.filename = filename;
        this.targetName = targetName;
        this.logListenerList = new ArrayList<>();
    }

    @Override
    protected Void doInBackground() throws Exception {
        File buildFile = new File(filename);
        Project project = new Project();
        DefaultLogger consoleLogger = new DispatchLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
        project.addBuildListener(consoleLogger);
        ProjectHelper.configureProject(project, buildFile);
        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
        project.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        project.addReference("ant.projectHelper", helper);

        Thread antThread = new Thread(() -> {
            try {
                project.executeTarget(targetName);
            } catch (Exception e) {
                publish(e.getMessage());
            }
        });
        antThread.start();

        while(antThread.isAlive()) {
            if(Thread.currentThread().isInterrupted())
                antThread.interrupt();
        }


        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for(AntLogListener listener : logListenerList)
            for(String message : chunks)
                listener.log(message);
    }

    @Override
    protected void done() {
        callback.run();
    }

    private class DispatchLogger extends DefaultLogger {
        @Override
        protected void log(String message) {
            super.log(message);
            publish(message);
        }
    }

    public boolean isRunning() {
        return !(isDone() || isCancelled());
    }

    public void addAntLogListener(AntLogListener listener) {
        logListenerList.add(listener);
    }

    public void removeAntLogListener(AntLogListener listener) {
        logListenerList.remove(listener);
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }
}
