/*
 * Created on 04/09/2006 22:07:16
 */
package net.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.jforum.JForumExecutionContext;
import net.jforum.dao.ApiDAO;
import net.jforum.exceptions.DatabaseException;
import net.jforum.util.DbUtils;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: GenericApiDAO.java,v 1.3 2006/10/10 00:49:03 rafaelsteil Exp $
 */
public class GenericApiDAO implements ApiDAO
{
	/**
	 * @see net.jforum.dao.ApiDAO#isValid(java.lang.String)
	 */
	public boolean isValid(final String apiKey)
	{
		boolean status = false;
		
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("ApiModel.isValid"));
			pstmt.setString(1, apiKey);
			
			resultSet = pstmt.executeQuery();
			status = resultSet.next();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}
		
		return status;
	}
}
