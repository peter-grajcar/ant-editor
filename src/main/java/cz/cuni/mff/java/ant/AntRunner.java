package cz.cuni.mff.java.ant;

import cz.cuni.mff.java.event.AntLogListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * created: 10/08/2020
 *
 * @author Peter Grajcar
 */
public class AntRunner extends SwingWorker<Void, String> {

    private String targetName;

    private Runnable callback;

    private List<AntLogListener> logListenerList;

    public AntRunner() {
        this.logListenerList = new ArrayList<>();
    }

    @Override
    protected Void doInBackground() throws Exception {
        File buildFile = new File("src/main/resources/build_2.xml");
        Project project = new Project();
        DefaultLogger consoleLogger = new DispatchLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
        project.addBuildListener(consoleLogger);
        ProjectHelper.configureProject(project, buildFile);
        project.setUserProperty("cz.cuni.mff.java.ant.file", buildFile.getAbsolutePath());
        project.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        project.addReference("cz.cuni.mff.java.ant.projectHelper", helper);
        project.executeTarget(targetName);
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

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}
