package org.stampede;

import java.util.ArrayList;
import java.util.List;

import org.stampede.youngling.Pup;

/**
 * A factory class for getting instances called a bairn
 * Kit; A quiet, active implementation, only one instance per role with no config management
 * Pup; A loud, active implementation, only one instance per role with config management
 * Cub; A loud, passive implementation, multiple instance per role with config management
 * Calf; A quiet, passive implementation, multiple instance per role with no config management
 * @author Michael
 */
public final class Barn {
	
	private final Stampede stampede;
	private List<Youngling> all;

	Barn(Stampede stampede) {
		all = new ArrayList<Youngling>();
		this.stampede = stampede;
	}
	
	public List<Youngling> all() {
		return this.all;
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
		Youngling y = new Pup(stampede);
		all.add(y);
		return y;
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
