package io.labforward.jpa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for the {@link TableContentEditor} class.
 */
public class TableContentEditorTest {

	private static final String TABLE_NAME = "foo_table";
	private static final String LABEL_COL = DBConstants.LABEL_KEY;
	private static final String UOM_COL = "uom";
	private static final String VALUE_COL = "value";
	
	private static TableCreator TC;
	private static TableContentEditor TCE;
	
	@BeforeClass
	public static void setup() {
		TC = new TableCreator.Builder(TABLE_NAME)
				.addField("id", "serial PRIMARY KEY")
				.addField(LABEL_COL,  DBConstants.LABEL_TYPE)
				.addField(UOM_COL, "varchar(32)")
				.addField(VALUE_COL, "float8 NOT NULL")
				.build();
		
		boolean success = TC.create();
		assertTrue("Table could not be created.", success);
	}
	
	@AfterClass
	public static void cleanup() {
		TC.drop();
	}
	
	@After
	public void testCleanup() {
		TCE.delete();
	}
	
	@Test
	public void testInsert() {
		
		final String itemLabel = "'TEST-01'";
		
		TCE = new TableContentEditor.Builder(TABLE_NAME, itemLabel)
				.addField(LABEL_COL, itemLabel)
				.addField(UOM_COL, "'cm'")
				.addField(VALUE_COL, "13.4")
				.build();
		
		boolean success = TCE.insert();		
		assertTrue("Item could not be created.", success);
		
		success = TCE.insert(); // again!		
		assertFalse("No error on insertion of namesake item.", success);
	}
	
	@Test
	public void testMissingMandatoryField() {
		
		final String itemLabel = "'TEST-02'";
		
		TCE = new TableContentEditor.Builder(TABLE_NAME, itemLabel)
				.addField(LABEL_COL, itemLabel)
				.addField(UOM_COL, "'cm'")
				.build();
		
		boolean success = TCE.insert();		
		assertFalse("Item could be created despite missing mandatory field.", success);
	}
	
	@Test
	public void testUpdate() {
		
		final String itemLabel = "'TEST-03'";
		
		TCE = new TableContentEditor.Builder(TABLE_NAME, itemLabel)
				.addField(LABEL_COL, itemLabel)
				.addField(UOM_COL, "'C'")
				.addField(VALUE_COL, "-28")
				.build();
		
		boolean success = TCE.insert();		
		assertTrue("Item could not be created.", success);
		
		TCE = new TableContentEditor.Builder(TABLE_NAME, itemLabel)
				.addField(VALUE_COL, "-35")
				.build();
		
		success = TCE.update();
		assertTrue("Item could not be updated.", success);
	}

	@Test
	public void testWrongColumnName() {
		
		final String itemLabel = "'TEST-04'";
		
		TCE = new TableContentEditor.Builder(TABLE_NAME, itemLabel)
				.addField(LABEL_COL, itemLabel)
				.addField(VALUE_COL, "123")
				.addField("NOT_A_COLUMN", "'cm'")
				.build();
		
		boolean success = TCE.insert();		
		assertFalse("Item could be created despite wrong column name.", success);
	}
}
