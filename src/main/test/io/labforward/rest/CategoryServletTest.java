package io.labforward.rest;


import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Tests the {@link CategoryServlet} class.
 *
 */
public class CategoryServletTest {
	
	public static final String CATEGORY = "foo";

	@Category(ServletTest.class)
	//@Test TODO "groups" exclusion not working in Maven Surfire
	public void testCreateCategory() throws IOException {
		
		JSONArray ja = new JSONArray();
		ja.put(fieldOf("substance", "varchar(64)"));
		ja.put(fieldOf("melting_point", "float8"));
		
		JSONObject mainJson = new JSONObject();
		mainJson.put("fields", ja);
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		int http_code = 0;

		try {
		    HttpPost request = new HttpPost("http://localhost:8080/LabForwardTask/rest/" + CATEGORY);
		    StringEntity params = new StringEntity(mainJson.toString());
		    request.addHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON);
		    request.setEntity(params);
		    
		    CloseableHttpResponse response = httpClient.execute(request);
		    try {
		    	http_code = response.getStatusLine().getStatusCode();
		        System.out.println(response.getStatusLine());
		    } finally {
		        response.close();
		    }		    

		} catch (Exception ex) {
		    System.out.println("Exception: " + ex.getMessage());
		
		} finally {
		    httpClient.close();
		}
		
		assertEquals("Result OK: ", HTTP_OK, http_code);
	}
	
	// misc
	private static final String CONTENT_TYPE = "content-type";
	private static final int HTTP_OK = 200;
	private static JSONObject fieldOf(String name, String type) {
		JSONObject field = new JSONObject();
		field .put(name, type);
		return field ;
	}
}
