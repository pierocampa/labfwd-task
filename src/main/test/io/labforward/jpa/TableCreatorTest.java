package io.labforward.jpa;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for the {@link TableCreator} class.
 */
public class TableCreatorTest {

	@Test
	public void testCreate() {
		
		TableCreator tc = new TableCreator.Builder("foo_table")
				.addField("label", "varchar(64) NOT NULL")
				.addField("measure", "float8 NOT NULL")
				.build();
		
		boolean success = tc.create();		
		assertTrue("Table could not be created.", success);
		
		tc.drop(); // reset state
	}
}
