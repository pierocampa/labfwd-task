package io.labforward.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import io.labforward.jpa.TableContentEditor;
import io.labforward.model.Item;

/**
 * Servlet for the handling of items.
 */
@Path("/{category}/{item}")
public class ItemsServlet {

	/**
	 * The client wants to create a new item.
	 * <p>
	 * All non-nullable attributes of the item shall be specified in the request. 
	 * <p>
	 * Error is returned if the item already exists in the store.
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN/*MediaType.APPLICATION_JSON*/) // TODO: automatic cast to JSONObject with jettison
	@Produces(MediaType.TEXT_PLAIN)
	public Response createItem(String json, @PathParam("category") String cat, @PathParam("item") String itemLabel) {
		
		Response resp;
		
		JSONObject jobj = new JSONObject(json); 
		Item item = new Item(cat, itemLabel, jobj);		
		TableContentEditor tc = convert(item);
		
		if (tc.insert()) {
			resp = Response.ok()
					.entity("Item " + item.getLabel() + " created.\n") // TODO HTML
					.build();
		} else {
			resp = Response.serverError()
					.entity("Item " + item.getLabel() + " could not be created.\n")
					.build();
		}
		
		return resp;
	}
	
	/**
	 * The client wants to create or replace an item.
	 * <p>
	 * All non-nullable attributes of the item shall be specified in the request. 
	 * <p>
	 * Error is returned if the category does not exist in the items store.
	 */
	@PUT
	@Consumes(MediaType.TEXT_PLAIN/*MediaType.APPLICATION_JSON*/)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createOrReplaceItem(String json, @PathParam("category") String cat, @PathParam("item") String itemLabel) {
		
		Response resp;
		
		JSONObject jobj = new JSONObject(json); 
		Item item = new Item(cat, itemLabel, jobj);		
		TableContentEditor tc = convert(item);
				
		if (tc.insert() || tc.update()) {
			resp = Response.ok()
					.entity("Item " + item.getLabel() + " updated.\n") // TODO HTML
					.build();
		} else {
			resp = Response.serverError()
					.entity("Item " + item.getLabel() + " could not be updated.\n")
					.build();
		}
		
		return resp;
	}
	
	/**
	 * The client wants to update an item.
	 * <p>
	 * A subset of the attributes of the item can be specified here. 
	 * <p>
	 * Error is returned if the item does not exist in the store.
	 */
	@PATCH
	@Consumes(MediaType.TEXT_PLAIN/*MediaType.APPLICATION_JSON*/)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateItem(String json, @PathParam("category") String cat, @PathParam("item") String itemLabel) {
		
		Response resp;
		
		JSONObject jobj = new JSONObject(json); 
		Item item = new Item(cat, "'" + itemLabel + "'", jobj);		
		TableContentEditor tc = convert(item);
				
		if (tc.update()) {
			resp = Response.ok()
					.entity("Item " + item.getLabel() + " updated.\n") // TODO HTML
					.build();
		} else {
			resp = Response.serverError()
					.entity("Item " + item.getLabel() + " could not be created.\n")
					.build();
		}
		
		return resp;
	}
	
	/** Sub-routine for converting input JSON to internal persistence editor. */
	private static TableContentEditor convert(Item item) {
		TableContentEditor.Builder tcb = new TableContentEditor.Builder(item.getCategoryName(), item.getLabel());
		for (String attr : item.getLabels()) {
				tcb.addField(attr, item.getValue(attr));
		}
		return tcb.build();
	}
}
