/*
 * Copyright (c) JForum Team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer.
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * Created on Aug 31, 2007 7:21:02 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.oracle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import net.jforum.dao.generic.GenericModerationLogDAO;
import net.jforum.entities.ModerationLog;
import net.jforum.exceptions.DatabaseException;
import net.jforum.util.DbUtils;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id$
 */
public class OracleModerationLogDAO extends GenericModerationLogDAO 
{
	/**
	 * @see net.jforum.dao.generic.GenericModerationLogDAO#add(net.jforum.entities.ModerationLog)
	 */
	public void add(final ModerationLog log) 
	{
		PreparedStatement pstmt = null;
		
		try {
			pstmt = this.getStatementForAutoKeys("ModerationLog.addNew");
			
			pstmt.setInt(1, log.getUser().getId());
			pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			pstmt.setInt(3, log.getType());
			pstmt.setInt(4, log.getPostId());
			pstmt.setInt(5, log.getTopicId());
			pstmt.setInt(6, log.getPosterUser().getId());
			
			if (log.getOriginalMessage() == null) {
				log.setOriginalMessage("");
			}

			this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("ModerationLog.lastGeneratedModerationLogId"));
			
			final int logId = this.executeAutoKeysQuery(pstmt);
			
			log.setId(logId);
			
			OracleUtils.writeBlobUTF16BinaryStream(SystemGlobals.getSql("ModerationLog.setDescription"), 
				log.getId(), log.getDescription());
			
			OracleUtils.writeBlobUTF16BinaryStream(SystemGlobals.getSql("ModerationLog.setOriginalMessage"), 
				log.getId(), log.getOriginalMessage());
		}
		catch (Exception e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}
	
	/**
	 * @see net.jforum.dao.generic.GenericModerationLogDAO#selectAll(int, int)
	 */
	public List<ModerationLog> selectAll(final int start, final int count) 
	{
		return super.selectAll(start, start + count);
	}
	
	/**
	 * @see net.jforum.dao.generic.GenericModerationLogDAO#readDesriptionFromResultSet(java.sql.ResultSet)
	 */
	protected String readDesriptionFromResultSet(final ResultSet resultSet) throws SQLException 
	{
		return OracleUtils.readBlobUTF16BinaryStream(resultSet, "log_description");
	}
	
	/**
	 * @see net.jforum.dao.generic.GenericModerationLogDAO#readOriginalMessageFromResultSet(java.sql.ResultSet)
	 */
	protected String readOriginalMessageFromResultSet(final ResultSet resultSet) throws SQLException 
	{
		return OracleUtils.readBlobUTF16BinaryStream(resultSet, "log_original_message");
	}
}