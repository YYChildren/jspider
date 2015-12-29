/**
 * $Id: TaskSchedulerException.java,v 1.2 2002/11/27 22:07:37 vanrogu Exp $
 */
package net.javacoding.jspider.core.exception;

@SuppressWarnings("serial")
public class TaskSchedulerException extends Exception {

    public TaskSchedulerException() {
        super();
    }

    public TaskSchedulerException(String message) {
        super(message);
    }

}
