package com.robin.tail;
/**
 * A Listener which is called when "tail -f *.file" is changed
 * 
 * @author you.meng
 *
 */
public abstract interface LogFileTailerListener {
	public abstract void newLogFileLine(String line,long length);
}
