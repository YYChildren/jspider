package net.javacoding.jspider.core.task;

import net.javacoding.jspider.core.logging.Log;
import net.javacoding.jspider.core.logging.LogFactory;
import net.javacoding.jspider.core.threading.WorkerThreadPool;

/**
 * Interface that will be implemented upon each class that represents a
 * JSpider workertask that needs to be executed by a Worker Thread.
 *
 * JSpider has two types of tasks: spider tasks (that fetch data from a
 * web server), and thinker tasks (that interpret data, take decision,
 * etc...)
 *
 * $Id: WorkerTask.java,v 1.5 2003/04/25 21:29:02 vanrogu Exp $
 *
 * @author G�nther Van Roey
 */
public abstract class WorkerTask extends Thread implements Task {

    /**
     * Task type that is used for every task that will require the fetching
     * of data from a site.
     */
    public static final int WORKERTASK_SPIDERTASK = 1;

    /**
     * Task type used for all tasks that don't require any fetching of data
     */
    public static final int WORKERTASK_THINKERTASK = 2;
    
    protected WorkerThreadPool stp;

    /**
     * Returns the type of the task - spider or thinker.
     * @return the type of the task
     */
    public abstract int getType ( );

    /**
     * Allows some work to be done before the actual Task is carried out.
     * During the invocation of prepare, the WorkerThread's state will be
     * WORKERTHREAD_BLOCKED.
     */
    public abstract void prepare ( );

    /**
     * Allows us to put common code in the abstract base class.
     */
    public abstract void tearDown ( );
    
    @Override
	public void run() {
    	Log log = LogFactory.getLog(WorkerTask.class);
        log.debug("Worker thread (" + this.getName() + ") born");
        prepare();
        try {
            execute();
            tearDown();
        } catch (Exception e) {
            log.fatal("PANIC! Task " + this + " threw an excpetion!", e);
            System.exit(1);
        }
        synchronized (stp) {
            stp.notify();
        }
	}
}
