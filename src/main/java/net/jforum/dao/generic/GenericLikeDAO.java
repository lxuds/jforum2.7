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
 * Created on Jan 11, 2005 11:22:19 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.jforum.JForumExecutionContext;
import net.jforum.entities.Like;
import net.jforum.entities.LikeStatus;
import net.jforum.entities.User;
import net.jforum.exceptions.DatabaseException;
import net.jforum.util.DbUtils;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id$
 */
public class GenericLikeDAO implements net.jforum.dao.LikeDAO
{
	/**
	 * @see net.jforum.dao.LikeDAO#addLike(net.jforum.entities.Like)
	 */
	@Override public void addLike(final Like like)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("LikeModel.add"));
			pstmt.setInt(1, like.getPostId());
			pstmt.setInt(2, like.getPostUserId());
			pstmt.setInt(3, like.getFromUserId());
			pstmt.setInt(4, like.getPoints());
			pstmt.setInt(5, like.getTopicId());
			pstmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			pstmt.executeUpdate();

			this.updateUserLike(like.getPostUserId());
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.LikeDAO#getUserLike(int)
	 */
	@Override public LikeStatus getUserLike(final int userId)
	{
		final LikeStatus status = new LikeStatus();

		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = JForumExecutionContext.getConnection()
					.prepareStatement(SystemGlobals.getSql("LikeModel.getUserLike"));
			pstmt.setInt(1, userId);

			resultSet = pstmt.executeQuery();
			if (resultSet.next()) {
				status.setLikePoints(Math.round(resultSet.getDouble("user_like")));
			}

			return status;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.LikeDAO#updateUserLike(int)
	 */
	@Override public void updateUserLike(final int userId)
	{
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("LikeModel.getUserLikePoints"));
			pstmt.setInt(1, userId);

			int totalRecords = 0;
			double totalPoints = 0;
			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {
				final int points = resultSet.getInt("points");
				final int votes = resultSet.getInt("votes");

				totalPoints += ((double) points / votes);
				totalRecords++;
			}

			resultSet.close();
			pstmt.close();

			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("LikeModel.updateUserLike"));

			double likePoints = totalPoints / totalRecords;

			if (Double.isNaN(likePoints)) {
				likePoints = 0;
			}

			pstmt.setDouble(1, likePoints);
			pstmt.setInt(2, userId);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}
	}	

	/**
	 * @see net.jforum.dao.LikeDAO#update(net.jforum.entities.Like)
	 */
	@Override public void update(final Like like)
	{
		
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("LikeModel.update"));
			pstmt.setInt(1, like.getPoints());
			pstmt.setInt(2, like.getId());
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.LikeDAO#getPostLike(int)
	 */
	@Override public LikeStatus getPostLike(final int postId)
	{
		final LikeStatus like = new LikeStatus();

		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = JForumExecutionContext.getConnection()
					.prepareStatement(SystemGlobals.getSql("LikeModel.getPostLike"));
			pstmt.setInt(1, postId);

			resultSet = pstmt.executeQuery();
			if (resultSet.next()) {
				like.setLikePoints(Math.round(resultSet.getDouble(1)));
			}

			return like;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}
	}

    /**
     * @see net.jforum.dao.LikeDAO#deletePostLike(int)
     */
    @Override public void deletePostLike(final int postId)
    {
        PreparedStatement pstmt = null;
        try {
        	pstmt = JForumExecutionContext.getConnection()
                    .prepareStatement(SystemGlobals.getSql("LikeModel.deletePostLike"));
        	pstmt.setInt(1, postId);
        	pstmt.executeUpdate();
        }
        catch (SQLException e) {
        	throw new DatabaseException(e);
        }
        finally {
        	DbUtils.close(pstmt);
        }
    }
    
	/**
	 * @see net.jforum.dao.LikeDAO#userCanAddLike(int, int)
	 */
	@Override public boolean userCanAddLike(final int userId, final int postId)
	{
		boolean status = true;

		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("LikeModel.userCanAddLike"));
			pstmt.setInt(1, postId);
			pstmt.setInt(2, userId);

			resultSet = pstmt.executeQuery();
			if (resultSet.next()) {
				status = resultSet.getInt(1) < 1;
			}

			return status;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.LikeDAO#getUserLikes(int, int)
	 */
	@Override public Map<Integer, Integer> getUserLikes(final int topicId, final int userId)
	{
		final Map<Integer, Integer> map = new ConcurrentHashMap<Integer, Integer>();

		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = JForumExecutionContext.getConnection()
					.prepareStatement(SystemGlobals.getSql("LikeModel.getUserLikes"));
			pstmt.setInt(1, topicId);
			pstmt.setInt(2, userId);

			resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				map.put(Integer.valueOf(resultSet.getInt("post_id")), Integer.valueOf(resultSet.getInt("points")));
			}

			return map;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}
	}

	@Override public void getUserTotalLike(final User user)
	{
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("LikeModel.getUserTotalVotes"));
			pstmt.setInt(1, user.getId());

			resultSet = pstmt.executeQuery();

			user.setLike(new LikeStatus());

			if (resultSet.next()) {
				user.getLike().setTotalPoints(resultSet.getInt("points"));
				user.getLike().setVotesReceived(resultSet.getInt("votes"));
			}

			if (user.getLike().getVotesReceived() != 0) {
				// prevetns division by zero.
				user.getLike().setLikePoints(user.getLike().getTotalPoints() / (double)user.getLike().getVotesReceived());
			}
			this.getVotesGiven(user);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}
	}

	private void getVotesGiven(final User user)
	{
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("LikeModel.getUserGivenVotes"));
			pstmt.setInt(1, user.getId());

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				user.getLike().setVotesGiven(resultSet.getInt("votes"));
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.LikeDAO#getMostRatedUserByPeriod(int, java.util.Date, java.util.Date,
	 *      String)
	 */
	@Override public List<User> getMostRatedUserByPeriod(final int start, final Date firstPeriod, final Date lastPeriod, final String orderField)
	{
		String sql = SystemGlobals.getSql("LikeModel.getMostRatedUserByPeriod");
		sql = new StringBuilder(sql).append(" ORDER BY ").append(orderField).append(" DESC").toString();

		return this.getMostRatedUserByPeriod(sql, firstPeriod, lastPeriod);
	}

	/**
	 * 
	 * @param sql String
	 * @param firstPeriod Date
	 * @param lastPeriod Date
	 * @return List
	 */
	protected List<User> getMostRatedUserByPeriod(final String sql, final Date firstPeriod, final Date lastPeriod)
	{
		if (firstPeriod.after(lastPeriod)) {
			throw new DatabaseException("First Date needs to be before the Last Date");
		}

		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(sql);
			pstmt.setTimestamp(1, new Timestamp(firstPeriod.getTime()));
			pstmt.setTimestamp(2, new Timestamp(lastPeriod.getTime()));

			resultSet = pstmt.executeQuery();
			return this.fillUser(resultSet);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}
	}

	protected List<User> fillUser(final ResultSet resultSet) throws SQLException
	{
		final List<User> usersAndPoints = new ArrayList<User>();
		LikeStatus like = null;
		while (resultSet.next()) {
			final User user = new User();
			like = new LikeStatus();
			like.setTotalPoints(resultSet.getInt("total"));
			like.setVotesReceived(resultSet.getInt("votes_received"));
			like.setLikePoints(resultSet.getDouble("user_like"));
			like.setVotesGiven(resultSet.getInt("votes_given"));
			user.setUsername(resultSet.getString("username"));
			user.setId(resultSet.getInt("user_id"));
			user.setLike(like);
			usersAndPoints.add(user);
		}
		return usersAndPoints;
	}
}
