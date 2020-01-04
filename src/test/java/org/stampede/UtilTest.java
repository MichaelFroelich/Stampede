
package org.stampede;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilTest {

	@Test
	public void className() {
		assertTrue(Util.getCallingClassName().equals(this.getClass().getSimpleName()));
	}
}
