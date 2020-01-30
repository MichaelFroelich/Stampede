package org.stampede;

/**
 * A singleton factory class for getting instances
 * Kit; A quiet, active implementation, only one instance per role with no config management
 * Pup; A loud, active implementation, only one instance per role with config management
 * Cub; A loud, passive implementation, multiple instance per role with config management
 * Calf; A quiet, passive implementation, multiple instance per role with no config management
 * @author Michael
 */
public final class Barn {
	
	private final Stampede stampede;

	Barn(Stampede stampede) {
		this.stampede = stampede;
	}
	
	/**
	 * Kit; A quiet, active implementation, only one instance per role with no config management
	 * @return Kit
	 */
	public Object adoptKit() {
		return null;
	}
	
	/**
	 * Pup; A loud, active implementation, only one instance per role with config management
	 * @return Pup
	 */
	public Object adoptPup() {
		return null;
	}
	
	/**
	 * Cub; A loud, passive implementation, multiple instance per role with config management
	 * @return Cub
	 */
	public Object adoptCub() {
		return null;
	}
	
	/**
	 * Calf; A quiet, passive implementation, multiple instance per role with no config management
	 * @return Calf
	 */
	public Object adoptCalf() {
		return null;
	}
}
