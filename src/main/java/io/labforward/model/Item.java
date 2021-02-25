package io.labforward.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Utility class for validating a category definition.
 */
public class Item {
	
	private static final String VALUES_KEY = "values";
	
	private final String label;
	private final String catName;
	private Map<String, String> values = new LinkedHashMap<>();
	
	/**
	 * Constructor from a JSON string.
	 * <p>
	 * Example:
	 * <pre>
	 * {
	 *   "values":  [
     *     { "description" : "''" },
     *     { "uom" : "'F'" },
     *     { "value" : "47.2" }
     *   ]
     * }
     * </pre>
	 */
	// TODO other formats
	public Item(String catName, String itemName, JSONObject json) {
		this.label = itemName;
		this.catName = catName;
	
		// TODO handle exceptions
		JSONArray attrArr = (JSONArray) json.get(VALUES_KEY);
		for (Object obj : attrArr) {
			JSONObject attrObj = (JSONObject) obj;
			String attrName = attrObj.keys().next();
			String attrType = attrObj.getString(attrName);
			values.put(attrName, attrType);
		}
	}
	
	/** Gets the name of the item. */
	public String getLabel() {
		return label;
	}
	
	/** Gets the name of the category. */
	public String getCategoryName() {
		return catName;
	}
	
	/** Gets the set of attributes labels in the items definition. */
	public Set<String> getLabels() {
		return Collections.unmodifiableSet(values.keySet());
	}
	
	/** Gets the value of the given attribute ({@code null} in case the attribute does not belong to the definition). */
	public String getValue(String attr) {
		return values.get(attr);
	}
}
