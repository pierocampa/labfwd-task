package io.labforward.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

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
	
	/** See {@link EntityManagerFactory#close()}. */ 
	private static void close(){
		EMF.close();
	}
}
