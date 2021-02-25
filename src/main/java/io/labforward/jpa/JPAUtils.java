package io.labforward.jpa;

import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.json.JSONObject;

/**
 * Persistence utilities.
 */
public class JPAUtils {
	
	public static final String PERSISTENCE_UNIT = "LabForwardTask";

	private static final EntityManagerFactory EMF;
	static {
		EMF = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				JPAUtils.close();
			}
		});
	}

	/**
	 * Creates an instance of JPA entity manager.
	 */
	public static EntityManager getEntityManager(){
		return EMF.createEntityManager();
	}

	/**
	 * Drops a table from the persistence.
	 * @param tableName
	 */
	public static boolean dropTable(String tableName) {
		boolean ret = true;
		
		EntityManager em = getEntityManager();
		String sqlString = String.format("DROP TABLE %s", tableName);
		
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
	 * Gets the tuples of a table.
	 * @param tableName
	 * @return a JSON representation of the list of results ({@code null} if table does not exist
	 */
	public static JSONObject selectTuplesJSON(String tableName) {
		
		JSONObject json = null;
		
		EntityManager em = getEntityManager();
//		String sqlString = String.format("SELECT row_to_json(%s) FROM %s", tableName, tableName);// FIXME ERROR No Dialect Mapping
		String sqlString = String.format("SELECT * FROM %s", tableName); // TODO ORDER-BY label ASC

		
		StringBuilder jsonBuilder = new StringBuilder("{ \"items\":[");
		
		try {
			boolean empty = true;
			em.getTransaction().begin();
			
			{
				@SuppressWarnings("unchecked")
				List<Object[]> tuples = em.createNativeQuery(sqlString).getResultList();
				empty = tuples.isEmpty();
				
				// List to JSON manual formatting (FIXME)
				for (Object[] tuple : tuples) {
					jsonBuilder.append("[");
					for(int i=0; i<tuple.length; ++i) {
						jsonBuilder.append(String.format("\"%s\"", tuple[i].toString()))
						.append((i == (tuple.length-1)) ? "" : ", ");
					}
					jsonBuilder.append("], ");
				}
			}
			em.getTransaction().commit();
			
			if (!empty) {
				jsonBuilder.delete(jsonBuilder.length()-2, jsonBuilder.length()); // remove last comma
			}
			jsonBuilder.append(" ]}");
			
			json = new JSONObject(jsonBuilder.toString());
			
		} catch (PersistenceException e) {
			em.close();
		}
		
		return json;
	}
	
	/** See {@link EntityManagerFactory#close()}. */ 
	private static void close(){
		EMF.close();
	}
}
