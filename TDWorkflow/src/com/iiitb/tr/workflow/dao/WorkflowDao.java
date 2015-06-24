package com.iiitb.tr.workflow.dao;

import java.util.ArrayList;

import org.codehaus.jettison.json.JSONObject;

public interface WorkflowDao {

	public ArrayList<String> getAllUsers();
	public ArrayList<String> getUserDetail(String userName);
	

	public UserVo authenticateUser(String authToken);
	
	public ArrayList<Object> trList(String role);
	
	public int createUser(String userName,String password,String email,String role);
	
	public int deleteUser(String userName);
	
	public int updateUser(String userName,String email,String role);
	
	
	public String newTrCreation(String fileName,UserVo vo,String authors);
	
	public String getTrId(String fileName);
	
	public TrDocumentVo getTrDetails(String trId);
	
	
	public int docAuthUpdate(String trId,String userId);
	
	public int deleteTrDocument(String trId);
	
	
	
	public int docAuthDeletion(String trId);
	
	public int docReviewDeletion(String trId);
	
	public ArrayList<String> getReviewerDetails();
	
	public ArrayList<String> getReviewerDocDetails(String reviewerId);
	
	public int setDocState(int trId,int state);
	
	public void addCommentsDocReview(String trId , String comments);
	
	public int updateTrDescription(String updateDocId, String Description);
	
	
	public int updateTrCurrentCount(String updateDocId, int reviewerCount);
	public int updateTrModifyDate(String updateDocId, String format);
	
	//RISHABHS DAO
	
	JSONObject getComments(int UserId, String userRole, int TrID);
	String postComment(int userId, String userRole, JSONObject requestBody);
	String deleteComment(int userId, String role, int commentId);
	String addReviewer(int trId, int userId, int userId2);
	String getReviewers(int trId);
	String deleteReviewer(int TrID, int RevID);
	String acceptTask(int userId, int trId);
	String showTasks(int userId);
	String rejectTask(int userId, int trId);
	
	
	
	
	
}
