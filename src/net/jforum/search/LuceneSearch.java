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
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
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
 * Created on 18/07/2007 22:05:37
 * 
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.jforum.entities.Post;
import net.jforum.exceptions.SearchException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

/**
 * @author Rafael Steil
 * @version $Id: LuceneSearch.java,v 1.38 2007/10/05 03:22:37 rafaelsteil Exp $
 */
public class LuceneSearch implements NewDocumentAdded
{
	private static final Logger LOGGER = Logger.getLogger(LuceneSearch.class);
	
	private IndexSearcher searcher;
	private LuceneSettings settings;
	private LuceneResultCollector contentCollector;
	
	public LuceneSearch(LuceneSettings settings, 
		LuceneResultCollector contentCollector)
	{
		this.settings = settings;
		this.contentCollector = contentCollector;
		
		this.openSearch();
	}
	
	/**
	 * @see net.jforum.search.NewDocumentAdded#newDocumentAdded()
	 */
	public void newDocumentAdded()
	{
		try {
			this.searcher.close();
			this.openSearch();
		}
		catch (Exception e) {
			throw new SearchException(e);
		}
	}
	
	/**
	 * @return the search result
	 */
	public SearchResult search(SearchArgs args)
	{
		return this.performSearch(args, this.contentCollector, null);
	}
	
	public Document findDocumentByPostId(int postId)
	{
		Document doc = null;
		
		try {			
	        TopDocs results = searcher.search(new TermQuery(
					new Term(SearchFields.Keyword.POST_ID, String.valueOf(postId))),  null, 1);	        
	        ScoreDoc[] hits = results.scoreDocs;
	        for (ScoreDoc hit : hits) {
	            doc = this.searcher.doc(hit.doc);
	        }
		}
		catch (IOException e) {
			throw new SearchException(e);
		}
		
		return doc;
	}

	private SearchResult performSearch(SearchArgs args, LuceneResultCollector resultCollector, Filter filter)
	{
		SearchResult result;
		
		try {
			StringBuffer criteria = new StringBuffer(256);
			
			this.filterByForum(args, criteria);
			this.filterByKeywords(args, criteria);
			this.filterByDateRange(args, criteria);
			
			if (criteria.length() == 0) {
				return new SearchResult(new ArrayList<Post>(), 0);
			}
			
			Query query = new QueryParser(Version.LUCENE_30, "", this.settings.analyzer()).parse(criteria.toString());
			
	        TopDocs results = this.searcher.search(query, filter, 10, this.getSorter(args));
	        if (results.totalHits > 0) {
				result = new SearchResult(resultCollector.collect(args, results, query), results.totalHits);
			}
			else {				
				result = new SearchResult(new ArrayList<Post>(), 0);
			}
		}
		catch (Exception e) {
			throw new SearchException(e);
		}
		
		return result;
	}
	
	private Sort getSorter(SearchArgs args)
	{
		Sort sort = Sort.RELEVANCE;
		
		if ("time".equals(args.getOrderBy())) {
			//sort = new Sort(SearchFields.Keyword.POST_ID, "DESC".equals(args.getOrderDir()));
			sort = new Sort(new SortField(SearchFields.Keyword.POST_ID, SortField.INT, "DESC".equals(args.getOrderDir())));
		}

		return sort;
	}
	
	private void filterByDateRange(SearchArgs args, StringBuffer criteria)
	{
		if (args.getFromDate() != null) {
			criteria.append('(')
			.append(SearchFields.Keyword.DATE)
			.append(": [")
			.append(this.settings.formatDateTime(args.getFromDate()))
			.append(" TO ")
			.append(this.settings.formatDateTime(args.getToDate()))
			.append("])");
		}
	}
	
	private void filterByKeywords(SearchArgs args, StringBuffer criteria)
	{
		String[] keywords = this.analyzeKeywords(args.rawKeywords());
		
		for (int i = 0; i < keywords.length; i++) {
			if (args.shouldMatchAllKeywords()) {
				criteria.append(" +");
			}
			
			criteria.append('(')
			.append(SearchFields.Indexed.CONTENTS)
			.append(':')
			.append(QueryParser.escape(keywords[i]))
			.append(") ");
		}
	}

	private void filterByForum(SearchArgs args, StringBuffer criteria)
	{
		if (args.getForumId() > 0) {
			criteria.append("+(")
				.append(SearchFields.Keyword.FORUM_ID)
				.append(':')
				.append(args.getForumId())
				.append(") ");
		}
	}
	
	private String[] analyzeKeywords(String contents)
	{
		try {
			TokenStream stream = this.settings.analyzer().tokenStream("contents", new StringReader(contents));
			stream.addAttribute(TermAttribute.class);
			List<String> tokens = new ArrayList<String>();

            stream.reset();
			while (stream.incrementToken()) {
                    TermAttribute token = stream.getAttribute(TermAttribute.class);
                    if (token == null) {
    					break;
    				}
                    LOGGER.debug("{" + token.term() + "}");
                    tokens.add(new String(token.termBuffer(), 0, token.termLength()));
            }
			
			/*while (true) {
				Token token = stream.next();

				if (token == null) {
					break;
				}
				
				tokens.add(new String(token.termBuffer(), 0, token.termLength()));
			}*/
			
			return (String[])tokens.toArray(new String[tokens.size()]);
		}
		catch (IOException e) {
			throw new SearchException(e);
		}
	}
	
	private void openSearch()
	{
		try {
			this.searcher = new IndexSearcher(this.settings.directory(), true);
		}
		catch (IOException e) {
			throw new SearchException(e.toString(), e);
		}
	}
}
