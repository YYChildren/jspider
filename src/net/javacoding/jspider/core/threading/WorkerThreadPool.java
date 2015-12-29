package net.javacoding.jspider.core.threading;


import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.javacoding.jspider.core.task.DispatcherTask;
import net.javacoding.jspider.core.task.WorkerTask;


/**
 * Thread Pool implementation that will be used for pooling the spider and
 * parser threads.
 *
 * $Id: WorkerThreadPool.java,v 1.7 2003/02/27 16:47:49 vanrogu Exp $
 *
 * @author G�nther Van Roey
 */
public class WorkerThreadPool extends ThreadGroup {

    /** Task Dispatcher thread associated with this threadpool. */
    protected DispatcherThread dispatcherThread;

    /** Array of threads in the pool. */
    //protected WorkerThread[] pool;
    protected ThreadPoolExecutor pool;

    /** Size of the pool. */
    protected int poolSize;
    
    

    /**
     * Public constructor
     * @param poolName name of the threadPool
     * @param threadName name for the worker Threads
     * @param poolSize number of threads in the pool
     */
    public WorkerThreadPool(String poolName, String threadName, int poolSize) {
        super(poolName);
        this.poolSize = poolSize;
        dispatcherThread = new DispatcherThread(this, threadName + " dispatcher", this);
        ThreadFactory threadFactory = new WorkerThreadFactory(this,threadName);
		pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize, threadFactory);
    }
    
    class WorkerThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        WorkerThreadFactory(ThreadGroup group,String threadName) {
        	this.group= group;
            this.namePrefix = threadName+" ";
        }
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,namePrefix + threadNumber.getAndIncrement(),0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
    

    /**
     * Assigns a worker task to the pool.  The threadPool will select a worker
     * thread to execute the task.
     * @param task the WorkerTask to be executed.
     */
    public synchronized void assign(WorkerTask task) {
        while (true) {
        	if(pool.getActiveCount() < poolSize){
        		pool.execute(task);
        		return;
        	}
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Assigns a DispatcherTask to the threadPool.  The dispatcher thread
     * associated with the threadpool will execute it.
     * @param task DispatcherTask that will keep the workers busy
     */
    public void assignGroupTask(DispatcherTask task) {
        dispatcherThread.assign(task);
    }

    /**
     * Returns the percentage of worker threads that are busy.
     * @return int value representing the percentage of busy workers
     */
    public int getBusyPercentage () {
        return (pool.getActiveCount() * 100) / poolSize;
    }

    public int getIdlePercentage ( ) {
    	return ((poolSize - pool.getActiveCount()) * 100) / poolSize;
    }

    /**
     * Causes all worker threads to die.
     */
    public void stopAll() {
    	//pool.shutdown();
		try {
			pool.awaitTermination(0L, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    /**
     * Returns the number of worker threads that are in the pool.
     * @return the number of worker threads in the pool
     */
    public int getSize ( ) {
        return poolSize;
    }

}
