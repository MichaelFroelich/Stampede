package org.stampede.socket;

import java.util.concurrent.Executors;

public class ServerRunner implements Runnable {

	AbstractSocket socket;

	private Object lock;

	public ServerRunner(AbstractSocket socket) {
		this.socket = socket;
		this.lock = new Object();
	}

	private volatile boolean cancelled;

	public void run() {
		Runnable job = new Runnable() {
			@Override
			public void run() {
				synchronized (lock) {
					while (!cancelled && !Thread.currentThread().isInterrupted()) {
						try {
							if (!socket.serve()) { // If the current implementation is non blocking
								lock.wait();
							}
						} catch (Exception ex) {
							Thread.currentThread().interrupt();
						}
					}
				}
			}
		};
		Executors.newCachedThreadPool().execute(job);
	}

	public void cancel() {
		synchronized (lock) {
			cancelled = true;
			lock.notify();
		}
	}

	public boolean isCancelled() {
		return cancelled;
	}
}
