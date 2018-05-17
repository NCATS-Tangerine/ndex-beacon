/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
 *                       
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *-------------------------------------------------------------------------------
 */
package bio.knowledge.server.impl;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import bio.knowledge.server.model.BeaconPredicate;

/**
 * This class serves as an in-memory cache for nDex predicate relationships
 * discovered on the fly during Statement queries.
 * 
 * @author richard
 *
 */
@Component
public class PredicatesRegistry extends HashMap<String, BeaconPredicate> {
	
	private static final long serialVersionUID = -8061884198941425340L;

	public void indexPredicate( String id, String name, String definition ) {

		if( Util.nullOrEmpty(id) || Util.nullOrEmpty(name) ) return ; 

		BeaconPredicate p;
		
		if(!containsKey(id)) {
			
			/*
			 *  If a record by this name 
			 *  doesn't yet exist for this
			 *  predicate, then create it!
			 */
			p = new BeaconPredicate();
			
			// predicate resource CURIE
			p.setId(id);  
			p.setEdgeLabel(name);
			
			put(id, p);
			
		}
		

	}
}
