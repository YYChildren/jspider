package net.javacoding.jspider.api.event.monitor;

/**
 * $Id: ThreadPoolMonitorEvent.java,v 1.3 2003/03/28 17:26:26 vanrogu Exp $
 */
public class ThreadPoolMonitorEvent extends MonitorEvent {

    protected String name;
    protected int idlePct;
    protected int busyPct;
    protected int size;

    public ThreadPoolMonitorEvent ( String name, int idlePct, int busyPct, int size ) {
        this.name = name;
        this.idlePct = idlePct;
        this.busyPct = busyPct;
        this.size = size;
    }

    public String toString() {
        return "ThreadPool " + getName() + "% [idle: " + getIdlePct() + "%, blocked: " + "%, busy: " + getBusyPct() + "%], size: " + getSize();
    }

    public String getComment() {
        return toString();
    }

    public String getName() {
        return name;
    }

    public int getIdlePct() {
        return idlePct;
    }

    public int getBusyPct() {
        return busyPct;
    }

    public int getSize() {
        return size;
    }

}
