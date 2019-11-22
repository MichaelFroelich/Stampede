package org.stampede.socket;

public class ServerRunner implements Runnable {

    AbstractSocket socket;

    public ServerRunner(AbstractSocket socket) {
        this.socket = socket;
    }

    private volatile boolean cancelled;

    public void run() {
        
        try {
            socket.getClass().getMethod("serve").getAnnotations();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (!cancelled && !Thread.currentThread().isInterrupted()) {
            try {
                if(socket.serve()) { //If the 
                    Thread.sleep(0L);
                } else {
                    Thread.sleep(10L);
                }
            } catch (Exception ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
