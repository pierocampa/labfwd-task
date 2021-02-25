package io.labforward.jpa;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 * Class used to create a new database table definition.
 */
public class TableCreator {
	
	private final String name;
	private final Map<String, String> columns;
	
	/** Private constructor: use Builder. */
	private TableCreator(String tableName) {
		this.name = tableName;
		columns = new LinkedHashMap<>(); // keep insertion-order
	}
	
	/** Gets the name of the associated table. */
	public String getName() {
		return name;
	}
	
	/** Nested builder for correct construction. */
	public static class Builder {
		
		private final TableCreator tc;
		
		public Builder(String tableName) {
			tc = new TableCreator(tableName);
		}
		
		public Builder addField(String name, String type) {
			tc.columns.put(name, type);
			return this;
		}
		
		public TableCreator build() {
			return tc;
		}
	}	

	/**
	 * Creates the table onto the application's database.
	 * @return {@code true} for successful creation; {@code false} otherwise.
	 */
	public boolean create() {
		
		boolean ret = true;
		String sqlString = toSQL();
	
		EntityManager em = JPAUtils.getEntityManager();
		try {
			em.getTransaction().begin();
			{
				em.createNativeQuery(sqlString).executeUpdate();
			}
			em.getTransaction().commit();
			
		} catch (PersistenceException e) {
			ret = false;
			em.close();
		}
		
		return ret;
	}
	
	/** 
	 * Drops the table from the persistence.
	 * 
	 * @see {@link JPAUtils#dropTable(String)}
	 */
	public boolean drop() {
		return JPAUtils.dropTable(getName());
	}
	
	/** Utility to create the CREATE TABLE statement. */
	private String toSQL() {
		StringBuilder fields = new StringBuilder();
		for (String fname : columns.keySet()) {
			fields.append(String.format("%s %s, ", fname, columns.get(fname)));
		}
		fields.delete(fields.length()-2, fields.length()); // drop comma

		// TODO Hibernate Criteria?
	    return String.format("CREATE TABLE %s ( %s )",
	    		name, fields.toString());
	}	
	
	@Override
	public String toString() {
		return toSQL();
	}
}
