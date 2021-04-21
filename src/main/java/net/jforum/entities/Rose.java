/*
 * Copyright (c)Rafael Steil
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
 * Created on Feb 23, 2003 / 1:02:01 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import java.io.Serializable;
import java.util.Date;


import net.jforum.view.forum.common.ViewCommon;

/**
 * Represents every message post in the system.
 * 
 * @author Rafael Steil
 */
public class Rose extends Post
{
    private String description;
    private String rname;
    private String ename;
    private String synonyms;
    private String color;
    private String fragrance;
    private String petal;
    private String cluster;
    private String bloomPeriod;
    private String foliage;
    private String body;
    private String width;
    private String height;
    

	
	public Rose() { }
	
//	public Rose(int roseId)
//	{
//		this.id = roseId;
//	}
	
	/**
	 * Copy constructor
	 * 
	 * @param post The Post to make a copy from
	 */
	public Rose(Rose rose)
	{
		this.description = rose.getDescription();
		this.rname = rose.getRname();
		this.ename = rose.getEname();
		this.synonyms = rose.getSynonyms();
		this.color = rose.getColor();
		this.fragrance = rose.getFragrance();
		this.petal = rose.getPetal();
		this.cluster = rose.getCluster();
		this.bloomPeriod = rose.getBloomPeriod();
		this.foliage = rose.getFoliage();
		this.body = rose.getBody();
		this.width = rose.getWidth();
		this.height = rose.getHeight();
		
	}
	
	 
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public void setRname(String rname)
	{
		this.rname = rname;
	}
	
	public void setEname(String ename)
	{
		this.ename = ename;
	}
	
	public void setSynonyms(String synonyms)
	{
		this.synonyms = synonyms;
	}
	
	public void setColor(String color)
	{
		this.color = color;
	}

	public void setFragrance(String fragrance)
	{
		this.fragrance = fragrance;
	}
	
	public void setPetal(String petal)
	{
		this.petal = petal;
	}
	
	public void setCluster(String cluster)
	{
	    this.cluster = cluster;
	}
	
	public void setBloomPeriod(String bloomPeriod)
	{
		this.bloomPeriod = bloomPeriod;
	}
	
	public void setFoliage(String foliage)
	{
		this.foliage = foliage;
	}
	
	public void setBody(String body)
	{
		this.body = body;
	}
	
	public void setWidth(String width)
	{
		this.width = width;
	}
	
	public void setHeight(String height)
	{
		this.height = height;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public String getRname()
	{
		return this.rname;
	}
	
	public String getEname()
	{
		return this.ename;
	}
	
	public String getSynonyms()
	{
		return this.synonyms;
	}
	
	public String getColor()
	{
		return this.color;
	}

	public String getFragrance()
	{
		return this.fragrance;
	}
	
	public String getPetal()
	{
		return this.petal;
	}
	
	public String getCluster()
	{
		return this.cluster;
	}
	
	public String getBloomPeriod()
	{
		return this.bloomPeriod;
	}
	
	public String getFoliage()
	{
		return this.foliage;
	}
	
	public String getBody()
	{
		return this.body;
	}
	
	public String getWidth()
	{
		return this.width;
	}
	
	public String getHeight()
	{
		return this.height;
	}
	
	
}
