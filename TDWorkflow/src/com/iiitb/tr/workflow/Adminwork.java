package com.iiitb.tr.workflow;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONObject;

import com.iiitb.tr.workflow.dao.UserVo;
import com.iiitb.tr.workflow.dao.WorkflowDao;
import com.iiitb.tr.workflow.dao.WorkflowDaoImpl;
import com.iiitb.tr.workflow.util.Constants;


@Path("/admin")
public class Adminwork {

	@POST
	@Path("{auth}/{UserID}/{TrID}")
	@Produces(MediaType.TEXT_PLAIN)
	
	/***********************************************************
	 *  This would add Reviewer specifed by UserID to a TR document specified by TrID
	 * @return Success message
	 */
	public String addReviewer(@PathParam("auth") String authToken,@PathParam("UserID") int UserId,
			@PathParam("TrID") int TrId){
		String message=null;
		WorkflowDao dao=new WorkflowDaoImpl();
		UserVo user=dao.authenticateUser(authToken);
		if(user!=null)
		{
		
		if(user.getRole().equalsIgnoreCase("Normal"))
			return("Sorry!!! You are not authorized to view the requested URI");
		else if(user.getRole().equalsIgnoreCase("Admin"))
		{
			message=dao.addReviewer(TrId,UserId,user.getUserId());
			
		}
	    return (message);
		}
		else
			return Constants.INVALIDUSER;
	}
	
	
/*	@GET
	@Path("{auth}/{TrID}")
	@Produces(MediaType.TEXT_PLAIN)
	
	*//***********************************************************
	 *  This would get all Reviewers assigned to a TR document 
	 * @return List of UserIDs
	 *//*
	public String getReviewer(@PathParam("auth") String authToken,@PathParam("TrID") int TrId){
		String message = null;
		WorkflowDao dao=new WorkflowDaoImpl();
		UserVo user=dao.authenticateUser(authToken);
	
		message=dao.getReviewers(TrId);
		
		return message;
	}*/
	
	
	
	@DELETE
	@Path("{auth}/{TrID}/{RevID}")
	@Produces(MediaType.TEXT_PLAIN)
	
	public String deleteReviewer(@PathParam("auth") String authToken,@PathParam("TrID") int TrId,
			@PathParam("RevID") int RevID){
		String message = null;
		WorkflowDao dao=new WorkflowDaoImpl();
		UserVo user=dao.authenticateUser(authToken);
		
		if(user!=null)
		{
		if(user.getRole().equalsIgnoreCase("Normal"))
			message="Not Authorised to delete Reviewer";
		if(user.getRole().equalsIgnoreCase("Admin"))
			message=dao.deleteReviewer(TrId,RevID);
		
		
		return message;
		}
		else
			return Constants.INVALIDUSER;
	}
	
	
	@PUT
	@Path("{auth}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String publishDoc(JSONObject json , @PathParam("auth") String authToken) {

		WorkflowDao dao = new WorkflowDaoImpl();
		UserVo vo = dao.authenticateUser(authToken);
		
		try {
			if (vo != null && vo.getRole().equalsIgnoreCase(Constants.ADMIN)) {

				int trId = json.getInt("trid");
				
			
			if((dao.getTrDetails(String.valueOf(trId)).getCurrentState().equalsIgnoreCase(Constants.RFP)))
			{	
				if (dao.setDocState(trId, 5) == 0) {
					return "<html> <body> <center> Sorry !!! Unable to publish doc :  <b>"
							+ trId + "</b></center></body></html>";
				} else {
					
					Date date = new Date();
					SimpleDateFormat df = new SimpleDateFormat("yyyy/M/dd");
					
					dao.updateTrModifyDate(String.valueOf(trId), df.format(date));
					
					return "<html> <body> <center> Success !!! Published Doc :  <b>"
							+ trId + "</b></center></body></html>";
				}
				
			}
			else
				return "Sorry!!! Current state of the document does not allow publish to be performed";
		}
			
			else
			{
				if(vo!=null)
				return "Sorry!!! You are not authorized to publish document";
			}
			
		}
			catch (Exception e) {
				e.printStackTrace();
			}

			return Constants.INVALIDUSER;

	}

}
