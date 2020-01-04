package org.stampede;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

/**
 * Listens to other nodes on the network
 * @author Michael
 *
 */
public class ConnectionListener implements AutoCloseable, ConnectionStateListener {
	
	public Object getApplicationData() {
		return null;
	}
	
	public Object getAllMonitoredApplications() {
		return null;
	}
	
	public Object getRunningStatus() {
		return null;
	}
	
	public Object getHealthStatus() {
		return null;
	}

	@Override
	public void stateChanged(CuratorFramework client, ConnectionState newState) {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
