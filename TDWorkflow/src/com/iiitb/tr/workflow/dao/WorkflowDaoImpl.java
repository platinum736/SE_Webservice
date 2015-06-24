package com.iiitb.tr.workflow.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;





















import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.iiitb.tr.workflow.util.ConnectionPool;
import com.iiitb.tr.workflow.util.Constants;

public class WorkflowDaoImpl implements WorkflowDao {
	
	
	public static final String TR_DOC_LIST = "select t.TrID,t.CreationDate,t.ModifyDate,t.Description,t.ReviewerCount,t.CurrentCount,s.StateName from trdocument t,state s where t.StateID = s.StateID";
	public static final String USER_CREATION = "insert into user (UserName,UserEmail,Password,Role) values (?,?,?,?)";
	public static final String USER_DELETION = "delete from user where UserName =?";
	public static final String USER_EMAIL_UPDATION = "update user set UserEmail =? where UserName =?";
	public static final String USER_ROLE_UPDATION = "update user set Role =? where UserName =?";
	public static final String USER_UPDATION = "update user set UserEmail =? and Role =? where UserName =?";
	public static final String TR_CREATION = "insert into trdocument (CreationDate,ModifyDate,StateID,Description,ReviewerCount,CurrentCount) values (?,?,?,?,?,?)";
	public static final String TR_ID = "select TrID from trdocument where Description = ?";
	public static final String TR_DETAILS= "select t.TrID,t.CreationDate,t.ModifyDate,t.Description,t.ReviewerCount,t.CurrentCount,s.StateName from trdocument t,state s where t.StateID = s.StateID and t.TrID=?";
	public static final String DOC_AUTH_UPDATE = "insert into doc_auth (TrID,UserID) values (?,?)";
	
	public static final String TR_DOC_DELETION = "delete from trdocument where TrID =?";
	public static final String TR_DOC_AUTH_DELETION = "delete from doc_auth where TrID =?";
	public static final String TR_DOC_REVIEW_DELETION = "delete from doc_review where TrID =?";
	public static final String USER_DETAILS = "select * from user where UserName like ?";
	public static final String REVIEWER_DETAILS = "select u.UserID,u.UserName,u.Role,u.UserEmail from doc_review d , user u where d.UserID = u.UserID";
	public static final String REVIEWER_DETAILS_DOC = "select t.TrID,t.CreationDate,t.ModifyDate,t.Description,s.StateName from trdocument t,state s,doc_review d where t.StateID = s.StateID and d.TrID = t.TrID and d.UserID = ?";
	
	
	public static final String DOC_STATE = "update trdocument set StateID = ? where trID = ?";
	public static final String UPDATE_TR_DESCRIPTION = "update trdocument set Description = ? where TrID=?";
	public static final String CHECK_DOC_AUTH = "select * from doc_auth where TrID = ? and UserID = ?";
	public static final String UPDATE_TR_CURRENT_COUNT = "update trdocument set CurrentCount = ? where TrID=?";
	public static final String UPDATE_TR_MODIFY_DATE = "update trdocument set ModifyDate = ? where TrID=?";
	
	
	public static final String COMMENTS_DOC_REVIEW = "insert into notification_doc_review (DocRevID,Message,NotificationDate) values (?,?,?)";
	private static final String ASSIGN_REVIEWER = "Insert into reviewer_task (TrID,UserID) values(?,?)";
	
	
	
	@Override
	public ArrayList<String> getAllUsers() {
		// TODO Auto-generated method stub

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		ArrayList<String> retList = null;
		ObjectMapper mapper = new ObjectMapper();

		try {
			conn = ConnectionPool.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("select * from user");
			UserVo vo;
			retList = new ArrayList<String>();
			while (rs.next()) {
				vo = new UserVo();

				vo.setUserId(rs.getInt("UserID"));

				vo.setUserName(rs.getString("UserName"));
				vo.setEmail(rs.getString("UserEmail"));
				vo.setRole(rs.getString("Role"));
				
				retList.add(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vo));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();

				if (st != null)
					st.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return retList;
	}
	
	@Override
	public UserVo authenticateUser(String authToken) {
		// TODO Auto-generated method stub
		Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		ResultSet rs = null;
		UserVo vo= null;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn.prepareStatement("select * from user where Password=?");
			pstmt.setString(1, authToken);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				vo = new UserVo();

				vo.setUserId(rs.getInt("UserID"));

				vo.setUserName(rs.getString("UserName"));
				vo.setEmail(rs.getString("UserEmail"));
				vo.setRole(rs.getString("Role"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();

				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return vo;

	}
	
	
	@Override
	public ArrayList<String> getUserDetail(String userName) {
		// TODO Auto-generated method stub

		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<String> retList = null;
		ObjectMapper mapper = new ObjectMapper();

		try {
			conn = ConnectionPool.getConnection();
			st = conn.prepareStatement(USER_DETAILS);
			st.setString(1, "%"+userName+"%");
			rs = st.executeQuery();
			UserVo vo;
			retList = new ArrayList<String>();
			while (rs.next()) {
				vo = new UserVo();

				vo.setUserId(rs.getInt("UserID"));

				vo.setUserName(rs.getString("UserName"));
				vo.setEmail(rs.getString("UserEmail"));
				vo.setRole(rs.getString("Role"));
				
				retList.add(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vo));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();

				if (st != null)
					st.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return retList;
	}
	

	@Override
	public ArrayList<Object> trList(String role) {
		Connection conn = null;
		Statement pstmt = null;
		Statement pstmt1 = null;
		Statement pstmt2 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		TrDocumentVo vo= null;
		List<String> authList =null;
		List<String> reviewerList =null;
		
		List<UserVo> authors =null;
		List<UserVo> reviewers =null;
		
		List<Object> retListAdmin = null;
		List<Object> retListNormal = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn.createStatement();
			pstmt1 = conn.createStatement();
			pstmt2 = conn.createStatement();
			
			rs = pstmt.executeQuery(TR_DOC_LIST);
			retListAdmin = new ArrayList<Object>();
			retListNormal = new ArrayList<Object>();
			
			
			while (rs.next()) {
				vo = new TrDocumentVo();
				authList = new ArrayList<String>();
				reviewerList=new ArrayList<String>();
				
				authors = new ArrayList<UserVo>();
				reviewers=new ArrayList<UserVo>();
				
				vo.setDocumentId(rs.getInt("TrID"));
				
				rs1 =  pstmt1.executeQuery("select u.UserName,u.UserID,u.UserEmail,u.Role from user u , doc_auth d where d.trID="+rs.getInt("TrID")+" and d.UserID = u.UserID");
				UserVo userVo;
				while (rs1.next()) 
				{
					
					userVo = new UserVo();
					userVo.setUserId(rs1.getInt("UserID"));
					userVo.setUserName(rs1.getString("UserName"));
					userVo.setEmail(rs1.getString("UserEmail"));
					userVo.setRole(rs1.getString("Role"));
					
					authList.add(rs1.getString("UserName"));
					//authors.add(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userVo));
					authors.add(userVo);
				}
				vo.setAuthList(authList);
				vo.setAuthors(authors);
				
				
				
				
				rs2 =  pstmt2.executeQuery("select u.UserName,u.UserID,u.UserEmail,u.Role from user u , doc_review d where d.trID="+rs.getInt("TrID")+" and d.UserID = u.UserID");
				while (rs2.next()) 
				{
					userVo = new UserVo();
					userVo.setUserId(rs2.getInt("UserID"));
					userVo.setUserName(rs2.getString("UserName"));
					userVo.setEmail(rs2.getString("UserEmail"));
					userVo.setRole(rs2.getString("Role"));
					
					reviewerList.add(rs2.getString("UserName"));
					//reviewers.add(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userVo));
					reviewers.add(userVo);
					
				}
				vo.setReviewerList(reviewerList);
				vo.setReviewers(reviewers);
				
				
				vo.setCreation(rs.getDate("CreationDate"));
				vo.setModifyDate(rs.getDate("ModifyDate"));
				vo.setDescription(rs.getString("Description"));
				vo.setCurrentState(rs.getString("StateName"));
				
				vo.setReviewerCount(rs.getInt("ReviewerCount"));
				vo.setCurrentCount(rs.getInt("CurrentCount"));
				
				if(role.equalsIgnoreCase(Constants.ADMIN))
				retListAdmin.add(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vo));
				else
					retListNormal.add(vo);
					
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if(rs2!=null)
					rs1.close();
				
				if(rs1!=null)
					rs1.close();
				
				if (rs != null)
					rs.close();

				if (pstmt2 != null)
					pstmt1.close();
				
				if (pstmt1 != null)
					pstmt1.close();
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(role.equalsIgnoreCase(Constants.ADMIN))
		return ((ArrayList<Object>)retListAdmin);
		else
			return ((ArrayList<Object>)retListNormal);
	}

	@Override
	public String newTrCreation(String fileName,UserVo vo,String authors) {
		// TODO Auto-generated method stub
		Connection conn = null;
		PreparedStatement pstmt = null;
		int retVal=0;
		String trId=null;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn.prepareStatement(TR_CREATION);
			Date dNow = new Date( );
		      SimpleDateFormat ft = 
		      new SimpleDateFormat ("yyyy/M/dd");
		     
		    pstmt.setString(1,ft.format(dNow));
			pstmt.setString(2, ft.format(dNow));
			pstmt.setInt(3, 1);
			pstmt.setString(4, fileName);
			pstmt.setInt(5, 0);
			pstmt.setInt(6, 0);
			retVal = pstmt.executeUpdate();
			
			if(retVal!=0)
			{
				
				trId = getTrId(fileName);
				
			}
			
			
			if(trId!=null)
			{
				docAuthUpdate(trId,String.valueOf(vo.getUserId()));
				String tempAuthors[] = authors.split(",");
				for(String temp : tempAuthors)
				{
					docAuthUpdate(trId,temp);
				}
				
			}
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return trId;
		
		
	}

	@Override
	public String getTrId(String fileName) {
		// TODO Auto-generated method stub

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String retVal=null;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn.prepareStatement(TR_ID);
			pstmt.setString(1, fileName);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				retVal = rs.getString("TrID");
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				if(rs!=null)
					rs.close();
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	@Override
	public int createUser(String userName, String password, String email,
			String role){
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		int retVal=0;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn.prepareStatement(USER_CREATION);
			pstmt.setString(1, userName);
			pstmt.setString(2, email);
			pstmt.setString(3, password);
			pstmt.setString(4, role);
			retVal = pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	@Override
	public int deleteUser(String userName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int retVal=0;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn.prepareStatement(USER_DELETION);
			pstmt.setString(1, userName);
			
			retVal = pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	@Override
	public int updateUser(String userName, String email, String role) {
		// TODO Auto-generated method stub
		Connection conn = null;
		PreparedStatement pstmt = null;
		int retVal=0;

		try {
			conn = ConnectionPool.getConnection();
			
			if(email!=null && role!=null)
			{
			pstmt = conn.prepareStatement(USER_UPDATION);
			pstmt.setString(1, email);
			pstmt.setString(2, role);
			pstmt.setString(3, userName);
			}
			
			else if(email!=null)
			{
			pstmt = conn.prepareStatement(USER_EMAIL_UPDATION);
			pstmt.setString(1, email);
			
			pstmt.setString(2, userName);
			}
			
			else
			{
				pstmt = conn.prepareStatement(USER_ROLE_UPDATION);
				pstmt.setString(1, role);
				
				pstmt.setString(2, userName);
			}
			
			retVal = pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	
	}

	@Override
	public TrDocumentVo getTrDetails(String trId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		Statement pstmt1 = null;
		Statement pstmt2 = null;
		
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		
		TrDocumentVo vo= null;
		List<UserVo> authors =null;
		List<UserVo> reviewers =null;
		
		List<String> authList =null;
		List<String> reviewerList =null;
		//ObjectMapper mapper = new ObjectMapper();
		
		try {
			conn = ConnectionPool.getConnection();
			
			pstmt1 = conn.createStatement();
			pstmt2 = conn.createStatement();
			
			pstmt = conn.prepareStatement(TR_DETAILS);
			pstmt.setString(1, trId);
			rs = pstmt.executeQuery();
		
			
			while (rs.next()) {
				vo = new TrDocumentVo();
				authList = new ArrayList<String>();
				reviewerList = new ArrayList<String>();
				
				authors = new ArrayList<UserVo>();
				reviewers = new ArrayList<UserVo>();
				
				vo.setDocumentId(rs.getInt("TrID"));
				
				rs1 =  pstmt1.executeQuery("select u.UserName,u.UserID,u.UserEmail,u.Role from user u , doc_auth d where d.trID="+rs.getInt("TrID")+" and d.UserID = u.UserID");
				UserVo userVo;
				while (rs1.next()) 
				{
					userVo = new UserVo();
					userVo.setUserId(rs1.getInt("UserID"));
					userVo.setUserName(rs1.getString("UserName"));
					userVo.setEmail(rs1.getString("UserEmail"));
					userVo.setRole(rs1.getString("Role"));
					
					authList.add(rs1.getString("UserName"));
					authors.add(userVo);
					/*try {
						authors.add(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userVo));
					} catch (JsonGenerationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				}
				vo.setAuthList(authList);
				vo.setAuthors(authors);
				
				rs2 =  pstmt2.executeQuery("select u.UserName,u.UserID,u.UserEmail,u.Role from user u , doc_review d where d.trID="+rs.getInt("TrID")+" and d.UserID = u.UserID");
				while (rs2.next()) 
				{
					userVo = new UserVo();
					userVo.setUserId(rs2.getInt("UserID"));
					userVo.setUserName(rs2.getString("UserName"));
					userVo.setEmail(rs2.getString("UserEmail"));
					userVo.setRole(rs2.getString("Role"));
					
					
					reviewerList.add(rs2.getString("UserName"));
					
					reviewers.add(userVo);
					
					
				/*	try {
						reviewers.add(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userVo));
					} catch (JsonGenerationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				}
			
				vo.setReviewerList(reviewerList);
				vo.setReviewers(reviewers);
				
				vo.setCreation(rs.getDate("CreationDate"));
				vo.setModifyDate(rs.getDate("ModifyDate"));
				if(rs.getString("Description")!= null && rs.getString("Description")!="")
				{
					if(rs.getString("Description").split("\\.").length==2)
					vo.setDescription(rs.getString("Description").split("\\.")[1]);
				}
				vo.setCurrentState(rs.getString("StateName"));
				vo.setReviewerCount(rs.getInt("ReviewerCount"));
				vo.setCurrentCount(rs.getInt("CurrentCount"));
				
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   finally {

			try {
				if(rs2!=null)
					rs1.close();
				
				if(rs1!=null)
					rs1.close();
				
				if (rs != null)
					rs.close();

				if (pstmt2 != null)
					pstmt1.close();
				
				if (pstmt1 != null)
					pstmt1.close();
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return vo;
	}

	@Override
	public int docAuthUpdate(String trId, String userId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		boolean check = true;
		int retVal=0;

		try {
			conn = ConnectionPool.getConnection();
			
			
	
			pstmt1 = conn.prepareStatement(CHECK_DOC_AUTH);
			pstmt1.setString(1, trId);
			pstmt1.setString(2, userId);
			rs = pstmt1.executeQuery();
		
			
			while(rs.next())
			{
				check = false;
			}
				
				
				if(check)
				{
				pstmt = conn.prepareStatement(DOC_AUTH_UPDATE);
				pstmt.setString(1, trId);
				pstmt.setString(2, userId);
				retVal = pstmt.executeUpdate();
				}
				
			
			
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				if (rs != null)
					rs.close();
				
				
				if (pstmt1 != null)
					pstmt1.close();
				
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	@Override
	public int deleteTrDocument(String trId) {
		// TODO Auto-generated method stub
		Connection conn = null;
		PreparedStatement pstmt = null;
		int retVal=0;

		try {
			
			int temp = docAuthDeletion(trId);
			 docReviewDeletion(trId);
			
			
			// Document may or may not have reviewer
			 
			if(temp!=0)
			{
			conn = ConnectionPool.getConnection();
			pstmt = conn.prepareStatement(TR_DOC_DELETION);
			pstmt.setString(1, trId);
			
			
			
			retVal = pstmt.executeUpdate();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	@Override
	public int docAuthDeletion(String trId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int retVal=0;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn.prepareStatement(TR_DOC_AUTH_DELETION);
			pstmt.setString(1, trId);
			
			retVal = pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	@Override
	public int docReviewDeletion(String trId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int retVal=0;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn.prepareStatement(TR_DOC_REVIEW_DELETION);
			pstmt.setString(1, trId);
			
			retVal = pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	@Override
	public ArrayList<String> getReviewerDetails() {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<String> retList = null;
		ObjectMapper mapper = new ObjectMapper();

		try {
			conn = ConnectionPool.getConnection();
			st = conn.prepareStatement(REVIEWER_DETAILS);
			rs = st.executeQuery();
			UserVo vo;
			retList = new ArrayList<String>();
			while (rs.next()) {
				vo = new UserVo();

				vo.setUserId(rs.getInt("UserID"));

				vo.setUserName(rs.getString("UserName"));
				vo.setEmail(rs.getString("UserEmail"));
				vo.setRole(rs.getString("Role"));
				
				retList.add(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vo));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();

				if (st != null)
					st.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return retList;
	}

	@Override
	public ArrayList<String> getReviewerDocDetails(String reviewerId) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		Statement pstmt1 = null;
		Statement pstmt2 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		TrDocumentVo vo= null;
		List<String> authList =null;
		List<String> reviewerList =null;
		
		List<String> retList = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn.prepareStatement(REVIEWER_DETAILS_DOC);
			pstmt.setString(1, reviewerId);
			pstmt1 = conn.createStatement();
			pstmt2 = conn.createStatement();
			
			rs = pstmt.executeQuery();
			
			retList= new ArrayList<String>();
			reviewerList=new ArrayList<String>();;
			
			while (rs.next()) {
				vo = new TrDocumentVo();
				authList = new ArrayList<String>();
				vo.setDocumentId(rs.getInt("TrID"));
				
				rs1 =  pstmt1.executeQuery("select u.UserName from user u , doc_auth d where d.trID="+rs.getInt("TrID")+" and d.UserID = u.UserID");
				while (rs1.next()) 
				{
					authList.add(rs1.getString("UserName"));
				}
				vo.setAuthList(authList);
				
				rs2 =  pstmt2.executeQuery("select u.UserName from user u , doc_review d where d.trID="+rs.getInt("TrID")+" and d.UserID = u.UserID");
				while (rs2.next()) 
				{
					reviewerList.add(rs2.getString("UserName"));
				}
				vo.setReviewerList(reviewerList);
				
				vo.setCreation(rs.getDate("CreationDate"));
				vo.setModifyDate(rs.getDate("ModifyDate"));
				vo.setDescription(rs.getString("Description"));
				vo.setCurrentState(rs.getString("StateName"));
				
				retList.add(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vo));
				
					
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if(rs2!=null)
					rs1.close();
				
				if(rs1!=null)
					rs1.close();
				
				if (rs != null)
					rs.close();

				if (pstmt2 != null)
					pstmt1.close();
				
				if (pstmt1 != null)
					pstmt1.close();
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ((ArrayList<String>)retList);
		

	}

	@Override
	public int setDocState(int trId,int state) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int retVal=0;

		try {
			conn = ConnectionPool.getConnection();
			
			
			pstmt = conn.prepareStatement(DOC_STATE);
			pstmt.setInt(1, state);
			pstmt.setInt(2, trId);
			
			
			retVal = pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	@Override
	public void addCommentsDocReview(String trId, String comments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int updateTrDescription(String updateDocId,String description) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int retVal=0;

		try {
			conn = ConnectionPool.getConnection();
			
			
			pstmt = conn.prepareStatement(UPDATE_TR_DESCRIPTION);
			pstmt.setString(1, description);
			pstmt.setString(2, updateDocId);
			
			
			retVal = pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	@Override
	public int updateTrCurrentCount(String updateDocId, int reviewerCount) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int retVal=0;

		try {
			conn = ConnectionPool.getConnection();
			
			
			pstmt = conn.prepareStatement(UPDATE_TR_CURRENT_COUNT);
			pstmt.setInt(1, reviewerCount);
			pstmt.setString(2, updateDocId);
			
			
			retVal = pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	@Override
	public int updateTrModifyDate(String updateDocId, String date) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int retVal=0;

		try {
			conn = ConnectionPool.getConnection();
			
			
			pstmt = conn.prepareStatement(UPDATE_TR_MODIFY_DATE);
			pstmt.setString(1, date);
			pstmt.setString(2, updateDocId);
			
			
			retVal = pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try {
			
				
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}
	
	/*
	 * RISHABH'S DAOIMPL
	 * 
	 * */
	
	@Override
	public JSONObject getComments(int UserId, String userRole, int TrID) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONObject result = null;
		JSONObject mainObject = null;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn
					.prepareStatement("select CommentID,UserID,CommentDesc,CommentDate from doc_review d "
							+ "join comment c on d.DocRevID=c.DocRevID"
							+ " where TrID=?");
			pstmt.setInt(1, TrID);
			rs = pstmt.executeQuery();
			mainObject = new JSONObject();
			while (rs.next()) {
				result = new JSONObject();
				result.put("CommentID", rs.getInt("CommentID"));
				result.put("UserId", rs.getInt("UserID"));
				result.put("Comment Desc", rs.getString("CommentDesc"));
				result.put("Comment Date", rs.getDate("CommentDate"));
				mainObject.accumulate("Comments", result);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();

				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mainObject;
	}
	
	@Override
	public String postComment(int userId, String userRole,
			JSONObject requestBody) {
		
		int TrID = 0;
		String CommentDesc = null;
		try {
			TrID = requestBody.getInt("TrID");
			CommentDesc = requestBody.getString("CommentDesc");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (isReviewer(userId, TrID)) {

			Connection conn = null;
			PreparedStatement pstmt = null;
			PreparedStatement preparedStatement = null;
			PreparedStatement update = null;
			PreparedStatement select = null;
			PreparedStatement changeState = null, changeReviewerCommentState = null;
			ResultSet rs = null;
			ResultSet rs1 = null;
			int DocRevID = 0;
			int message = 0;
			try {
				conn = ConnectionPool.getConnection();
				pstmt = conn
						.prepareStatement("select DocRevID from doc_review where TrID=? and UserID=?");
				pstmt.setInt(1, TrID);
				pstmt.setInt(2, userId);
				rs = pstmt.executeQuery();

				while (rs.next()) {
					DocRevID = rs.getInt(1);
				}
				preparedStatement = conn
						.prepareStatement("Insert into comment (CommentDesc,CommentDate,DocRevID)"
								+ "values(?,?,?)");
				preparedStatement.setString(1, CommentDesc);
				java.util.Date today = new java.util.Date();
				preparedStatement.setTimestamp(2,
						new java.sql.Timestamp(today.getTime()));
				preparedStatement.setInt(3, DocRevID);
				message = preparedStatement.executeUpdate();

				/*
				 * Change the state of document assigned to a reviewer to
				 * Reviewed state after he posts comment
				 */
				changeReviewerCommentState = conn
						.prepareStatement("Update doc_review set StateID=3 where DocRevID=?");
				changeReviewerCommentState.setInt(1, DocRevID);
				changeReviewerCommentState.executeUpdate();

				/*
				 * Message=1 mean 1 row inserted into the table comment In this
				 * case need to decrement current count in trdocument table
				 */

				if (message == 1) {

					update = conn
							.prepareStatement("update trdocument set CurrentCount=CurrentCount-1 "
									+ "where TrID=1 and CurrentCount>0;");
					update.executeUpdate();

					select = conn
							.prepareStatement("Select CurrentCount from trdocument where TrID=?");
					select.setInt(1, TrID);
					rs1 = select.executeQuery();
					int CurrentCount = 0;
					while (rs1.next()) {
						CurrentCount = rs1.getInt("CurrentCount");
					}
					if (CurrentCount == 0) {
						/*
						 * Change state of document
						 */

						changeState = conn
								.prepareStatement("Update trdocument set StateID=3 where TrID=?");
						changeState.setInt(1, TrID);
						changeState.executeUpdate();
					}

				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				try {
					if (rs != null)
						rs.close();

					if (rs1 != null)
						rs1.close();

					if (pstmt != null)
						pstmt.close();

					if (preparedStatement != null)
						preparedStatement.close();

					if (changeReviewerCommentState != null)
						changeReviewerCommentState.close();

					if (update != null)
						update.close();

					if (select != null)
						select.close();

					if (changeState != null)
						changeState.close();

					if (conn != null)
						conn.close();

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return (message + " Comment Posted");
		}

		else {
			return ("Comments can be posted only by Reviewer of TR Document");
		}
	}
	
	
	private boolean isReviewer(int userId, int trID) {
		

		/*
		 * Check if userId is one of the Reviewer of trId
		 */

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn
					.prepareStatement("select count(*) from doc_review where TrID=? and UserID=?");
			pstmt.setInt(1, trID);
			pstmt.setInt(2, userId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();

				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (count > 0) {
			return (true);
		}

		return (false);

	}

	@Override
	public String deleteComment(int userId, String role, int commentId) {
		

		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		int RevID = 0;
		int message = 0;
		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn
					.prepareStatement("select UserID from doc_review where DocRevID="
							+ "(select DocRevID from comment where CommentID=?)");
			pstmt.setInt(1, commentId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				RevID = rs.getInt(1);
			}

			if (RevID == 0) {
				return ("Comment does not exist");
			}

			if (RevID != userId && role.equalsIgnoreCase("Normal")) {
				return ("Cannot delete comment for other reviewer");
			}

			preparedStatement = conn
					.prepareStatement("Delete from comment where CommentID=?");
			preparedStatement.setInt(1, commentId);
			message = preparedStatement.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();

				if (pstmt != null)
					pstmt.close();

				if (preparedStatement != null)
					preparedStatement.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return (message + " Comment Deleted");
	}

	
	private boolean checkifAuthor(int trId, int userId, int userId2) {
		/*
		 * Check if userId1 is one of the author of trId userId2 is admin's Id
		 */
		// TODO Auto-generated method stub
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn
					.prepareStatement("select count(*) from doc_auth where TrID=? and UserID=?");
			pstmt.setInt(1, trId);
			pstmt.setInt(2, userId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();

				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (count > 0) {
			return (true);
		}

		return (false);
	}

	@Override
	public String addReviewer(int trId, int userId, int userId2) {
		
		if (checkifAuthor(trId, userId, userId2)) {
			return ("Cannot assign one of the Authors as Reviewer");
		}
	
		Connection conn = null;
		PreparedStatement pstmt = null;
		int rs = 0;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn.prepareStatement(ASSIGN_REVIEWER);
			pstmt.setInt(1, trId);
			pstmt.setInt(2, userId);
			rs = pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			/*
			 * If same TR is being assigned to same UserID more than once It
			 * would result in a Unique Constraint violation in reviewer_task
			 * table
			 */

			return ("Work already allocated to the Reviewer");
		} finally {

			try {
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "Reviewer Assignment Successfull";
	}

	@Override
	public String getReviewers(int trId) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONObject result = new JSONObject();

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn
					.prepareStatement("Select UserName from doc_review d,User u where TrID=? and d.UserID=u.UserID");
			pstmt.setInt(1, trId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				try {
					result.accumulate("Reviewer Name", rs.getString("UserName"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result.toString();
	}

	@Override
	public String deleteReviewer(int TrID, int RevID) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn
					.prepareStatement("Delete from doc_review where TrID=? and UserID=?");
			pstmt.setInt(1, TrID);
			pstmt.setInt(2, RevID);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (result == 0)
			return "ERROR:Could not remove Reviewer";
		else if (result > 0)
			return "Reviewer removed Successfully";
		return null;
	}

	@Override
	public String acceptTask(int userId, int trId) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		//PreparedStatement pstmt2 = null;
		//PreparedStatement pstmt3 = null;
		PreparedStatement pstmt4 = null;
		int rs;
		int rs1;
		int rs4;
		if(ishisTask(userId,trId))
		{

		try {
			conn = ConnectionPool.getConnection();
			
			pstmt = conn
					.prepareStatement("update trdocument set ReviewerCount=ReviewerCount+1, "
							+ "CurrentCount=CurrentCount+1 where trId=?");
			pstmt.setInt(1, trId);
			rs = pstmt.executeUpdate();

			pstmt1 = conn
					.prepareStatement("Insert into doc_review (StateID,UserID,TrID) values(2,?,?)");
			pstmt1.setInt(1, userId);
			pstmt1.setInt(2, trId);
			rs1 = pstmt1.executeUpdate();

			/*
			 * pstmt3=conn.prepareStatement(
			 * "select RevTaskID from doc_review where ");
			 * pstmt3.setInt(1,userId); pstmt3.setInt(2, trId);
			 * rs1=pstmt3.executeUpdate();
			 * 
			 * pstmt2=conn.prepareStatement(
			 * "Insert into notification_review_task StateID,UserID,TrID values(2,?,?)"
			 * ); pstmt2.setInt(1,userId); pstmt2.setInt(2, trId);
			 * rs1=pstmt1.executeUpdate();
			 */

			pstmt4 = conn.prepareStatement("Delete from reviewer_task where TrID=? and UserID=?");
			pstmt4.setInt(1, trId);
			pstmt4.setInt(2, userId);
			rs4 = pstmt4.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return("Already assigned task");
		} finally {

			try {
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

				if (pstmt1 != null)
					pstmt1.close();
				
				if (pstmt4 != null)
					pstmt1.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "Task Accepted";
		}
		
		else
			return "The specified trdocument not assigned to you"; 
	}

	private boolean ishisTask(int userId, int trId) {
		// TODO Auto-generated method stub
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn
					.prepareStatement("select count(*) from reviewer_task where UserID=? and TrID=?");
			pstmt.setInt(1, userId);
			pstmt.setInt(2, trId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int count=rs.getInt(1);
				if(count==0)
					return false;
				else
					return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

				if (rs != null)
					rs.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		return false;
	}

	@Override
	public String showTasks(int userId) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		JSONObject result = new JSONObject();

		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn
					.prepareStatement("select t.TrID,Description from doc_review d join trdocument t"
							+ " on(t.TrID=d.TrID) where UserID=? and d.StateID=2");
			pstmt.setInt(1, userId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				try {
					JSONObject results = new JSONObject();
					results.put("TrID", rs.getInt("t.TrID"));
					results.put("Description", rs.getString("Description"));
					results.put("Status", "Review Pending");
					result.accumulate("Tasks", results);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			pstmt1 = conn
					.prepareStatement("select t.TrID,Description from reviewer_task r join trdocument t"
							+ " on(t.TrID=r.TrID) where UserID=?");
			pstmt1.setInt(1, userId);
			rs1 = pstmt1.executeQuery();
			while (rs1.next()) {
				try {
					JSONObject results = new JSONObject();
					results.put("TrID", rs1.getInt("t.TrID"));
					results.put("Description", rs1.getString("Description"));
					results.put("Status", "Acceptance Pending");
					result.accumulate("Tasks", results);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();

				if (pstmt1 != null)
					pstmt1.close();

				if (rs != null)
					rs.close();

				if (rs1 != null)
					rs1.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result.toString();
	}
	
	@Override
	public String rejectTask(int userId, int trId) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
	/*	PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;*/
		PreparedStatement pstmt4 = null;
		int rs;
		int rs1;
		int rs4;

		try {
			conn = ConnectionPool.getConnection();
			
			/*
			 * pstmt3=conn.prepareStatement(
			 * "select RevTaskID from doc_review where ");
			 * pstmt3.setInt(1,userId); pstmt3.setInt(2, trId);
			 * rs1=pstmt3.executeUpdate();
			 * 
			 * pstmt2=conn.prepareStatement(
			 * "Insert into notification_review_task StateID,UserID,TrID values(2,?,?)"
			 * ); pstmt2.setInt(1,userId); pstmt2.setInt(2, trId);
			 * rs1=pstmt1.executeUpdate();
			 */

			pstmt4 = conn.prepareStatement("Delete from reviewer_task where TrID=? and UserID=?");
			pstmt4.setInt(1, trId);
			pstmt4.setInt(2, userId);
			rs4 = pstmt4.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (pstmt != null)
					pstmt.close();

				if (conn != null)
					conn.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "Task Rejected";

	}


	

}
