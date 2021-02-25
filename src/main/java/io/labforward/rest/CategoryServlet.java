package io.labforward.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import io.labforward.jpa.JPAUtils;
import io.labforward.jpa.TableContentEditor;
import io.labforward.jpa.TableCreator;
import io.labforward.model.CategoryDefinition;
import io.labforward.model.Item;

/**
 * Servlet for the handling of items' categories.
 */
@Path("/{category}")
public class CategoryServlet {

	/**
	 * The client wants to get all items in a category.
	 * <p>
	 * Error is returned if the category does not exist in the items store.
	 */
	@GET
	//@Produces(MediaType.APPLICATION_JSON) // FIXME JSON Writer not found
	@Produces(MediaType.TEXT_PLAIN)
	public Response getAllItems(String json, @PathParam("category") String cat) {
		
		Response resp;
		
		JSONObject result = JPAUtils.selectTuplesJSON(cat);
				
		if (null != result) {
			resp = Response.ok()
					.entity(result.toString())
					.build();
		} else {
			resp = Response.serverError()
					.build();
		}
		
		return resp;
	}
	
	/**
	 * The client wants to create a new category of items.
	 * <p>
	 * The definition of the category is sent as a JSON resource along with the request.
	 * <p>
	 * Error is returned if the category already exists in the items store.
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN/*MediaType.APPLICATION_JSON*/) // TODO: automatic cast to JSONObject with jettison
	@Produces(MediaType.TEXT_PLAIN)
	public Response createCategory(String json, @PathParam("category") String cat) {
		
		JSONObject jobj = new JSONObject(json); 
		CategoryDefinition catDef = new CategoryDefinition(cat, jobj);		
		
		TableCreator.Builder tcb = new TableCreator.Builder(catDef.getName());
		for (String attr : catDef.getAttributes()) {
				tcb.addField(attr, catDef.getAttributeType(attr));
		}
		TableCreator tc = tcb.build();
		
		Response resp;
		
		if (tc.create()) {
			resp = Response.ok()
					.entity("Category " + cat + " created.\n") // TODO HTML
					.build();
		} else {
			resp = Response.serverError()
					.entity("Category " + cat + " could not be created.\n")
					.build();
		}
		
		return resp;
	}
	
	/**
	 * Deletes a whole category from the persistence.
	 * <p>
	 * WARNING: this will delete all items registered under this category.
	 */
	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteCategory(@PathParam("category") String cat) {
		
		Response resp;

		if (JPAUtils.dropTable(cat)) {		
			resp = Response.ok()
					.entity("Category " + cat + " deleted.\n")
					.build();
		} else {
			resp = Response.serverError()
					.entity("Category " + cat + " could not be deleted.\n")
					.build();
		}

		return resp;
	}
}
