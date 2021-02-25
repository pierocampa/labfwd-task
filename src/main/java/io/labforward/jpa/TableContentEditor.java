package io.labforward.jpa;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 * Class used to edit the content of a database table.
 * <p>
 * The modification can occur with an insertion, an update, or a deletion
 * of an element.
 */
public class TableContentEditor {	
	
	private final String tableName;
	private final String itemName;
	private final Map<String, String> values;
	
	/** Private constructor: use Builder. */
	private TableContentEditor(String tableName, String itemName) {
		this.tableName = tableName;
		this.itemName = itemName;
		values = new LinkedHashMap<>(); // keep insertion-order
	}
	
	/** Gets the name of the table associated with the editor. */
	public String getTableName() {
		return tableName;
	}
	
	/** Gets the name of the item associated with the editor. */
	public String getItemName() {
		return itemName;
	}
	
	/** Nested builder for correct construction. */
	public static class Builder {
		
		private final TableContentEditor tce;
		
		public Builder(String tableName, String itemName) {
			tce = new TableContentEditor(tableName, itemName);
		}
		
		public Builder addField(String name, String type) {
			tce.values.put(name, type);
			return this;
		}
		
		public TableContentEditor build() {
			return tce;
		}
	}	

	/**
	 * Creates the table onto the application's database.
	 * @return {@code true} for successful creation; {@code false} otherwise.
	 */
	public boolean insert() {
		
		boolean ret = true;
		String sqlString = insertToSQL();
	
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
	 * Updates the table item from the persistence.
	 * @param whereClause  the WHERE clause to be set (e.g. " label = 'L001' ")
	 * @return {@code true} for successful update; {@code false} otherwise.
	 */
	public boolean update() {
		
		boolean ret = true;
		String sqlString = updateToSQL();
	
		EntityManager em = JPAUtils.getEntityManager();
		try {
			em.getTransaction().begin();
			{
				int updates = em.createNativeQuery(sqlString).executeUpdate();
				ret = (updates > 0);
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
	 */
	public boolean delete() {
		
		boolean ret = true;
		String sqlString = deleteToSQL();
	
		EntityManager em = JPAUtils.getEntityManager();
		try {
			em.getTransaction().begin();
			{
				int deletes = em.createNativeQuery(sqlString).executeUpdate();
				ret = (deletes > 0);
			}
			em.getTransaction().commit();
			
		} catch (PersistenceException e) {
			ret = false;
			em.close();
		}
		
		return ret;
	}
	
	/** Creates the INSERT INTO statement. */
	private String insertToSQL() {
		
		StringBuilder fields = new StringBuilder();
		StringBuilder fvalues = new StringBuilder();
		
		for (String fname : values.keySet()) {
			fields.append(String.format("%s, ", fname));
			fvalues.append(String.format("%s, ", values.get(fname)));
	    }
		fields.delete(fields.length()-2, fields.length()); // drop comma
		fvalues.delete(fvalues.length()-2, fvalues.length());
		
	    return String.format("INSERT INTO %s ( %s ) VALUES ( %s )",
	    		getTableName(), fields.toString(), fvalues.toString());
	}
	
	/** Creates the UPDATE statement. */
	private String updateToSQL() {
		
		StringBuilder updates = new StringBuilder();

		for (String fname : values.keySet()) {
			updates.append(String.format("%s = %s, ", fname, values.get(fname)));
	    }
		updates.delete(updates.length()-2, updates.length()); // drop comma
		
		String whereClause = String.format("%s = %s", DBConstants.LABEL_KEY, getItemName());
		
	    return String.format("UPDATE %s SET %s WHERE %s ",
	    		getTableName(), updates.toString(), whereClause);
	}
	
	/** Creates the DELETE statement. */
    private String deleteToSQL() {		
		String whereClause = String.format("%s = %s", DBConstants.LABEL_KEY, getItemName());
	    return String.format("DELETE FROM %s WHERE %s ",
	    		getTableName(), whereClause);
	}	
	
	@Override
	public String toString() {
		return getTableName() + "-ContentEditor";
	}
}
