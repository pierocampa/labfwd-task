package io.labforward.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import io.labforward.jpa.DBConstants;

/**
 * Utility class for validating a category definition.
 */
public class CategoryDefinition {
	
	private static final String ATTR_KEY = "attributes";
	
	private final String name;
	private Map<String, String> attributes = new LinkedHashMap<>();
	
	/**
	 * Constructor from a JSON string.
	 * <p>
	 * Example:
	 * <pre>
	 * {
	 *   "attributes": [
     *      "description" : "varchar",
     *      "uom" : "varchar",
     *      "value" : "float8"
     *   ]
     * }
     * </pre>
	 */
	// TODO other formats
	public CategoryDefinition(String catName, JSONObject json) {
		this.name = catName;
		
		addMandatoryFields();
		
		// TODO handle exceptions
		JSONArray attrArr = (JSONArray) json.get(ATTR_KEY);
		for (Object obj : attrArr) {
			JSONObject attrObj = (JSONObject) obj;
			String attrName = attrObj.keys().next();
			String attrType = attrObj.getString(attrName);
			attributes.put(attrName, attrType);
		}
	}
	
	/** Gets the name of the category. */
	public String getName() {
		return name;
	}
	
	/** Gets the (ordered) set of attributes labels in the category definition. */
	public Set<String> getAttributes() {
		return Collections.unmodifiableSet(attributes.keySet());
	}
	
	/** Gets the type definition of the given attribute ({@code null} in case the attribute does not belong to the definition). */
	public String getAttributeType(String attr) {
		return attributes.get(attr);
	}
	
	/** Adds the mandatory fields of an items' category definition. */
	private void addMandatoryFields() {	
		attributes.put(DBConstants.ID_KEY, DBConstants.SERIAL_TYPE);
		attributes.put(DBConstants.LABEL_KEY, DBConstants.LABEL_TYPE);
	}
}
