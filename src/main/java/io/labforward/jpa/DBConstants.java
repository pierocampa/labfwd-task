package io.labforward.jpa;

/**
 * Constants associated with DB tables and tuples.
 */
public enum DBConstants {
	;
	
	public static final String ID_KEY = "id";
	public static final String LABEL_KEY = "label"; // used in URLs !

	public static final String SERIAL_TYPE = "serial PRIMARY KEY"; // DB OID
	public static final String LABEL_TYPE = "varchar(64) NOT NULL UNIQUE"; // readable identifier
}
