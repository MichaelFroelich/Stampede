package org.stampede;

/**
 * An interface class designed to provide open/close functionality to stampede
 * @author Michael
 */
public interface RoleListener {
	
	/**
	 * Event in which a particular role has been assigned
	 * @param role
	 */
	public void onAssignment(String role);
	
	/**
	 * Event in which a particular role has been unassigned
	 * @param role
	 */
	public void onUnassignment(String role);

	/**
	 * Event in which a role is advertised
	 * @param role
	 */
	public void onAdvertise(String role);
	
	/**
	 * Event in which a role is unadvertised
	 * @param role
	 */
	public void onUnadvertise(String role);
	
	/**
	 * Checks if this role is currently active
	 * @param role
	 * @return
	 */
	public boolean hasRole(String role);
}
