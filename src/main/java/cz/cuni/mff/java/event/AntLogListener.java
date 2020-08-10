package cz.cuni.mff.java.event;

/**
 * created: 10/08/2020
 *
 * @author Peter Grajcar
 */
@FunctionalInterface
public interface AntLogListener {

    void log(String msg);

}
