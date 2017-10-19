/**
 * 
 */
package bio.knowledge.server.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class, segregated from the main nDex beacon code
 * is potentially generic for use in other beacons to attempt
 * proper semantic group labeling using some mundane heuristics.
 * 
 * @author Richard
 *
 */
public class SemanticGroup {
	
	private static Logger _logger = LoggerFactory.getLogger(SemanticGroup.class);	
		
	public enum NameSpace {
		
		NCBIGENE("GENE"),
		GENECARDS("GENE"),
		UNIPROT("CHEM"),
		CHEBI("CHEM"),
		DRUGBANK("CHEM"),
		KEGG("PHYS"),
		KEGG_PATHWAY("PHYS"),
		SMPDB("PHYS"), // Small Molecular Pathway Database, http://smpdb.ca/
		REACT("PHYS"), // Reactome == pathways?
		;
		
		private static Logger _logger = LoggerFactory.getLogger(NameSpace.class);	
		
		private String semanticGroup;
		
		private NameSpace(String semanticGroup) {
			this.semanticGroup = semanticGroup;
		}
		
		public static String getSemanticGroup(String prefix) {
			
			if(Util.nullOrEmpty(prefix)) return "";
			
			prefix = prefix.toUpperCase().replaceAll("\\.", "_");
			try {
				NameSpace bns = NameSpace.valueOf(prefix);
				return bns.semanticGroup;
			} catch( IllegalArgumentException e) {
				_logger.info("nDexBio node id prefix '"+prefix.toString()+"' encountered is not yet mapped to a semantic group?");
				return "";
			}
		}
	}
	
	/*
	 * We can *globally* cache these semantic types for a given id
	 * since such concept identities may be accessed frequently,
	 * in particular, for the /statements endpoint. It is therefore
	 * wasteful to run this computation more than once for a given id.
	 */
	private static Map<String,String> semanticGroupCache = new HashMap<String,String>();
	
	/*
	 * functions to cache newly discovered semantic groups,
	 * indexed against id (and possibly name) on the fly...
	 */
	private static String assignedGroup(String id, String group) {
		semanticGroupCache.put(id, group);
		return group;
	}

	private static String assignedGroup(String id, String name, String group) {
		assignedGroup(id, group);
		semanticGroupCache.put(name, group);
		return group;
	}
	
	public static String makeSemGroup(String id, String name, List<String> properties ) {
		
		String group;
		
		String lcName = name.toLowerCase();
		
		/*
		 *  Check the cache first...
		 *  Maybe already indexed by id...
		 *  This is a "hard" assignment
		 *  of semantic group to a given 
		 *  "concept" identifier.
		 */
		if(semanticGroupCache.containsKey(id)) {
			group = semanticGroupCache.get(id);
			if(! Util.nullOrEmpty(group) ) 
				return group; // return if found in cache
		} 
		
		/*
		 *  Otherwise, currently unknown semantic group: 
		 *  attempt to assign by various heuristics
		 */  
		
		/* 
		 * First heuristic: to match conceptId CURIE assuming that
		 * the namespace prefix implies a standard semantic group.
		 * Only currently have a few known namespaces (above)
		 */
		if(id.contains(":")) {
			
			String[] namespace = id.toUpperCase().split(":");
			group = NameSpace.getSemanticGroup(namespace[0]);
			
			if(!group.isEmpty()) 
				return assignedGroup( id, lcName, group );
			
		}

		/*
		 *  Second heuristic: scan caller-submitted 
		 *  property tags for clues?
		 */
		for (String tag : properties) {
			
			String lcTag = tag.toLowerCase();
			
			if(lcTag.endsWith(" gene")) return assignedGroup(id, lcName, "GENE");
			
			lcTag = lcTag.replace(" ", "");
			
			switch (lcTag) {	
				case "disease": 
					return assignedGroup(id, lcName, "DISO");
				case "gene": 
					return assignedGroup(id, lcName, "GENE");
				case "pathway": 
					return assignedGroup(id, lcName, "PHYS");
				case "protein": 
				case "proteinreference": 
				case "rna": 
				case "mirna": 
				case "smallmolecule": 
				case "smallmoleculedrug": 
					return assignedGroup(id, lcName, "CHEM");
				default:
					_logger.debug("SemanticGroup.makeSemGroup(): encountered unrecognized tag: "+
								   tag+" in item '"+id+"'called '"+name+"'");
			}
		}
		
		/* Third heuristic: take a look at the node name to match common 
		 * "big picture" keywords like 'therapy' and 'cancer'.
		 *  
		 * Do this first, since this is a strong class of concepts in 
		 * Translator Knowledge Beacons and the other heuristics which 
		 * follow may be less discriminating and semantically polymorphic
		 */

		if( 
				lcName.contains("therapy") ||
				lcName.contains("agent") ||
				lcName.contains("transplant")
				
		) return assignedGroup(id, lcName, "PROC");
		
		if( 
				lcName.contains("pathway") ||
				lcName.contains("signaling") ||
				lcName.contains("metabolism") ||
				lcName.contains("biosynthesis") ||
				lcName.contains("transcription") ||
				lcName.contains("translation") ||
				lcName.contains("secretion")
				
		)  return assignedGroup(id, lcName, "PHYS");
		
		if( 
				lcName.contains("vaccine") ||
				lcName.contains("peptide") ||
				lcName.contains("protein") ||
				lcName.contains("microrna") ||
				
				// Common subgroups in names - are there others?
				lcName.contains("hydroxy") ||
				lcName.contains("methyl") ||
				lcName.contains("phenyl") ||
				lcName.contains("amino") ||
				lcName.contains("acid") ||
				
				/*
				 *  is there a better way to guess the nature of drugs...
				 *  maybe API to look up of names somewhere?
				 *  e.g. https://open.fda.gov/drug/label/reference/??
				 */
				lcName.endsWith("inib") ||
				lcName.endsWith("mab")
				
		) return assignedGroup(id, lcName, "CHEM");

		if( 
				lcName.contains("cancer") ||
				lcName.contains("asthma") ||
				lcName.contains("anemia") ||
				lcName.contains("disease") ||
				lcName.contains("failure") ||
				lcName.contains("dysfunction") ||
				lcName.contains("disorder") ||
				lcName.contains("deficiency") ||
				lcName.contains("injury") ||
				lcName.contains("pathy") ||
				lcName.contains("pathic") ||
				lcName.contains("rejection") ||
				lcName.contains("hypoplasia") ||
				lcName.contains("hyperplasia") ||
				lcName.contains("aciduria") ||
				lcName.contains("syndrome") ||
				
				// Are there other common disease suffixes?
				lcName.endsWith("itis")
				
		) return assignedGroup(id, lcName, "DISO");
		
		if( 
				lcName.contains("cell") ||
				lcName.contains("tissue") ||
				lcName.contains("heart") ||
				lcName.contains("lung") ||
				lcName.contains("kidney") ||
				lcName.contains("skin") ||
				lcName.contains("brain")
				
		)  return assignedGroup(id, lcName, "ANAT");
		
		/*
		 *  Check the cache for indexing by exact name.
		 *  
		 *  Here, the caching is less a question of
		 *  performance than it is a question of
		 *  last ditch inference of data type based 
		 *  on the name as a symbol.
		 *  
		 *  Assumption here is that gene symbols 
		 *  and (some) concept names may be universal.
		 * 
		 */ 
		if(semanticGroupCache.containsKey(lcName)) {
			group = semanticGroupCache.get(lcName);
			if(! Util.nullOrEmpty(group) ) { 
				/*  
				 * Return if found by name in the cache.
				 * Also index it against the current id
				 */
				return assignedGroup( id, group );
			}
		}

		// Give up for now...
		_logger.debug("SemanticGroup.makeSemGroup(): encountered semantically "
				    + "unclassified item '"+id+"'called '"+name+"'");			

		/*
		 *  We hesitate to cache our default guess here
		 *  in the event that we discover a more sensible
		 *  mapping (i.e. to the concept name) by some
		 *  other means, in the future...
		 */
		return "OBJC";
	}
}
