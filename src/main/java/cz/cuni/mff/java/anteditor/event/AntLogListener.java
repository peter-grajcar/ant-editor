package cz.cuni.mff.java.anteditor.event;

/**
 * The listener interface for receiving logged messages by Ant.
 *
 * @author Peter Grajcar
 */
@FunctionalInterface
public interface AntLogListener {

    /**
     * Invoked when a new message is logged.
     *
     * @param msg message
     */
    void log(String msg);

}
