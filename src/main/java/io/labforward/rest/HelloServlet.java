package io.labforward.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Servlet implementation class HelloServlet
 */
@Path("/test")
public class HelloServlet {

	/**
	 * Test servlet.
	 */
	@GET
	@Path("hi")
	@Produces(MediaType.TEXT_PLAIN)
	public Response sayHi() {
		return Response.ok()
				.entity("Hi there!")
				.build();
	}	
}
