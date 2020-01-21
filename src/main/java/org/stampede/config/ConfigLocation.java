package org.stampede.config;

/**
 * Please make your own
 * @author Michael
 *
 */
public abstract class ConfigLocation {
/*
	ZOOKEEPER {
		
	},
	
	LOCAL {
		
	},
	
	GIT {
		
	},
	
	FTP {
		
	};
*/
	
	/**
	 * 
	 * @param uri some sort of protocol or path combination
	 * @return
	 */
	public abstract boolean watchChanges(String uri);
}
