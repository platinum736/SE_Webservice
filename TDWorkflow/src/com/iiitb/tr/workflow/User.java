package com.iiitb.tr.workflow;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.iiitb.tr.workflow.dao.UserVo;
import com.iiitb.tr.workflow.dao.WorkflowDao;
import com.iiitb.tr.workflow.dao.WorkflowDaoImpl;
import com.iiitb.tr.workflow.util.Constants;
import com.iiitb.tr.workflow.util.PasswordCreation;

@Path("/users")
public class User {

	@GET
	@Path("{auth}/{username}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getUserDetails(@PathParam("auth") String authToken,@PathParam("username") String author) {

		WorkflowDao dao = new WorkflowDaoImpl();
		UserVo vo = dao.authenticateUser(authToken);

		if (vo != null) {

			dao = new WorkflowDaoImpl();

			try {
				return dao.getUserDetail(author).toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			return Constants.INVALIDUSER;
		
		
		return Constants.INVALIDUSER;
	}
	
	
	@GET
	@Path("{auth}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getAllUsers(@PathParam("auth") String authToken) {

		WorkflowDao dao = new WorkflowDaoImpl();
		UserVo vo = dao.authenticateUser(authToken);

		if (vo != null && vo.getRole().equalsIgnoreCase(Constants.ADMIN)) {

			dao = new WorkflowDaoImpl();

			return dao.getAllUsers().toString();
		} else
			return Constants.INVALIDUSER;
	}
	

	@PUT
	@Path("{auth}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String modifyUser(JSONObject json , @PathParam("auth") String authToken) {

		WorkflowDao dao = new WorkflowDaoImpl();
		UserVo vo = dao.authenticateUser(authToken);
		
		try {
			if (vo != null && vo.getRole().equalsIgnoreCase(Constants.ADMIN)) {

				String userName = json.getString("username");
				String email;
				String role;
				
				try
				{
					email =  json.getString("email");
				}
				catch(JSONException e)
				{
					email =null;
				}
				try
				{
					role =  json.getString("role");
				}
				catch(JSONException e)
				{
					role =null;
				}
			
				
				if (dao.updateUser(userName,email,role) == 0) {
					return "<html> <body> <center> Sorry !!! Unable to update user :  <b>"
							+ userName + "</b></center></body></html>";
				} else {
					return "<html> <body> <center> Success !!! Updated user :  <b>"
							+ userName + "</b></center></body></html>";
				}
				
			}
		}
			catch (Exception e) {
				e.printStackTrace();
			}

			return Constants.INVALIDUSER;

	}

	@POST
	@Path("{auth}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String createUser(JSONObject json,
			@PathParam("auth") String authToken) {
		WorkflowDao dao = new WorkflowDaoImpl();
		UserVo vo = dao.authenticateUser(authToken);
		try {
			if (vo != null && vo.getRole().equalsIgnoreCase(Constants.ADMIN)) {

				String username = (String) json.get("username");
				String email = (String) json.get("email");
				String role = (String) json.get("role");

				PasswordCreation pc = new PasswordCreation();
				String password = pc.createPassword(username);

				if (dao.createUser(username, password, email, role) == 0)
					return "<html> <body> <center> Sorry !!! Unable to create new user</center></body></html>";

				else {
					return ("<html> <body> <center>Congratulations !!!!  Use the following authtoken to authorize future requests</center>"
							+ "<br/> <b> <u> <center>" + password + "</center></u></b> </body></html>");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Constants.INVALIDUSER;
	}

	@DELETE
	@Path("{auth}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String deleteUser(JSONObject json,
			@PathParam("auth") String authToken) {

		WorkflowDao dao = new WorkflowDaoImpl();
		UserVo vo = dao.authenticateUser(authToken);
		try {
			if (vo != null && vo.getRole().equalsIgnoreCase(Constants.ADMIN)) {

				String userName;

				userName = json.getString("username");

				if (dao.deleteUser(userName) == 0) {
					return "<html> <body> <center> Sorry !!! Unable to delete user :  <b>"
							+ userName + "</b></center></body></html>";
				} else {
					return "<html> <body> <center> Success !!! Deleted user :  <b>"
							+ userName + "</b></center></body></html>";
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Constants.INVALIDUSER;

	}
}
