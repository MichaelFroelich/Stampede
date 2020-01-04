package org.stampede;

import java.util.List;

/**
 * A factory class for getting instances
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
	
	public static List<Youngling> all() {
		return null;
	}
	
	/**
	 * Kit; A quiet, active implementation, only one instance per role with no config management
	 * @return Kit
	 */
	public Youngling adoptKit() {
		return null;
	}
	
	/**
	 * Pup; A loud, active implementation, only one instance per role with config management
	 * @return Pup
	 */
	public Youngling adoptPup() {
		return null;
	}
	
	/**
	 * Cub; A loud, passive implementation, multiple instance per role with config management
	 * @return Cub
	 */
	public Youngling adoptCub() {
		return null;
	}
	
	/**
	 * Calf; A quiet, passive implementation, multiple instance per role with no config management
	 * @return Calf
	 */
	public Youngling adoptCalf() {
		return null;
	}
}
