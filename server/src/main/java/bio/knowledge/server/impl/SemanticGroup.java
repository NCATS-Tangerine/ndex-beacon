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
		
		NCBIGENE( "GENE", new String[]{
				"http://identifiers.org/ncbigene",
				"https://www.ncbi.nlm.nih.gov/gene"
		} ),
		GENECARDS("GENE", new String[] {"http://www.genecards.org/"} ),
		UNIPROT("CHEM", new String[] {
				"http://identifiers.org/uniprot",
				"http://www.uniprot.org/"
		} ),
		CHEBI("CHEM", new String[] { "http://identifiers.org/chebi" } ),
		DRUGBANK("CHEM", new String[] {
				"http://identifiers.org/drugbank/",
				"http://bio2rdf.org/drugbank"
		} ),
		KEGG("PHYS", 
				new String[] {
					"http://identifiers.org/kegg",
					"http://www.genome.jp/kegg/"
		} ), // Kyoto Encyclopedia of Genes and Genomes
		KEGG_PATHWAY("PHYS", 
				new String[] {
					"http://identifiers.org/kegg",
					"http://www.genome.jp/kegg/"
		} ), // Kyoto Encyclopedia of Genes and Genomes
		PMID("CONC", // could also be "PROC"? 
				new String[] {
					"http://identifiers.org/pubmed",
					"https://www.ncbi.nlm.nih.gov/pubmed"
				} ), // PubMed
		REACT("PHYS", new String[] {"https://reactome.org/"} ),    // REACTome == pathways?
		REACTOME("PHYS", new String[] {"https://reactome.org/"} ), // REACTOME == pathways?
		BP("PHYS", new String[] {"http://www.biopax.org/"} ), // BioPAX
		PATHWAYCOMMONS("PHYS", 
				new String[] {
						"http://purl.org/pc2/7",
						"http://www.pathwaycommons.org/pc2"
				} ),  // Pathway Commons
		SMPDB("PHYS", new String[] {"http://smpdb.ca/"} )   // Small Molecular Pathway Database
		;
		
		static private Logger _logger = LoggerFactory.getLogger(NameSpace.class);	
		
		private  String[] uris;
		private String semanticGroup;
		
		private NameSpace( String semanticGroup, String[] uris ) {
			this.semanticGroup = semanticGroup;
			this.uris = uris ;
		}
		
		static public String getSemanticGroupByCurie(String id) {
			
			if(Util.nullOrEmpty(id)) return "";
			
			String[] namespace = id.toUpperCase().split(":");
			String prefix = namespace[0];
			
			prefix = prefix.toUpperCase().replaceAll("\\.", "_");
			
			try {
				
				NameSpace bns = NameSpace.valueOf(prefix);
				return bns.semanticGroup;
				
			} catch( IllegalArgumentException e) {
				/*
				 * Highlight newly encountered prefixes *other than* 
				 * NDEX (which is a locally assigned designation)
				 */
				if( ! prefix.toString().equals("NDEX"))
					_logger.debug("nDexBio node id prefix '"+prefix.toString()+
							      "' encountered is not yet mapped to a semantic group?");
				
				return "";
			}
		}

		/**
		 * Simplistic predicate to test whether or not the input id looks like a URI
		 * 
		 * @param id
		 * @return
		 */
		static public boolean isURI(String id) {
			if( id.toLowerCase().startsWith("http://") ||  
					id.toLowerCase().startsWith("https://") ) {
					return true;			 
				}
			return false;
		}
		
		/**
		 * 
		 * @param uri 'uniform resource identifier' source of the object id
		 * @param delimiter separating objectId from base URI path
		 * @return String baseline URI of the URI
		 */
		static public String getBaseUri(String uri,String delimiter) {
			int pathEnd = uri.lastIndexOf(delimiter) ;
			if(pathEnd>-1) {
				return uri.substring(0,pathEnd) ;
			} else
				return uri ; // just pass through?
		}
		
		/**
		 * Same as two argument method, with delimiter defaulting to "/"
		 * @param uri 'uniform resource identifier' source of the object id
		 * @return
		 */
		static public String getBaseUri(String uri) {
			return getBaseUri(uri,"/");
		}
		

		/**

		 * 

		 * @param accessionId

		 * @return

		 */

		static public String resolveUri(String id) {

			String baseuri = getBaseUri(id);
			
			for( NameSpace ns : NameSpace.values()) {
				for( String uri : ns.uris )
					if( uri.equals( baseuri.toLowerCase() ) )
						return ns.name()+":"+getObjectId(id);
			}
			
			/*
			 *  Just return the URI if I 
			 *  don't recognize the namespace
			 */
			return id;
		}
		
		static public String getObjectId(String uri,String delimiter) {
			int pathEnd = uri.lastIndexOf(delimiter) ;
			if(pathEnd>-1) {
				return uri.substring(pathEnd+1) ;
			} else
				return uri ; // just pass through?
		}
		
		/**
		 * Same as two argument method, with delimiter defaulting to "/"
		 * @param uri 'uniform resource identifier' source of the object id
		 * @return
		 */
		static public String getObjectId(String uri) {
			return getObjectId(uri,"/");
		}
		
		static public String getSemanticGroupByUri(String id) {
			
			if(Util.nullOrEmpty(id)) return "";
			
			String baseuri = getBaseUri(id);
			
			for( NameSpace ns : NameSpace.values()) {
				for( String uri : ns.uris )
					if( uri.equals( baseuri.toLowerCase() ) )
						return ns.semanticGroup;
			}
			
			// no URI mapping found?
			_logger.warn("SemanticGroup.NameSpace.getSemanticGroupByUri(): URI  '"+id+"' not recognized?");
			return "";
		}

		/**
		 * Simpleminded predicate for CURIE identifiers
		 * @param id
		 * @return
		 */
		static public boolean isCurie(String id) {
			
			if( id.toLowerCase().startsWith("http://") ||  
					id.toLowerCase().startsWith("https://") ) {
					return false;
					
			} else 
				/*
				 * Simpleminded test for CURIE
				 * Deficiency: Will not fail with identifiers
				 * which have more than one colon?
				 */
				if(id.contains(":"))
					return true;
				
			return false;
		}
		
		
		/**
		 * Infer a suitable name from a identifier
		 * @param id
		 * @return
		 */
		static public String makeName(String id) {
			
			if( NameSpace.isURI(id) )
				return NameSpace.resolveUri(id);
						
			else if( NameSpace.isCurie(id) ) 
				return NameSpace.getObjectId(id,":");
			
			// otherwise, just return the full id?
			_logger.debug("Translator.makeName(id): couldn't guess a canonical name for node id '"+id+"'?");
			return id;
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
	
	static public String makeSemGroup(String id, String name, List<String> properties ) {
		
		String group;
		
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
		
		String lcName = "";
		if(! ( name==null || name.isEmpty() ) )
			lcName = name.toLowerCase();
		else
			// What other option do I have here?
			lcName = NameSpace.makeName(id);

		// Some nodes are unnamed but have URI's?
		if( NameSpace.isURI(id) ) {
			
			group = NameSpace.getSemanticGroupByUri(id);
			
			if( !group.isEmpty() ) {
				return assignedGroup( id, lcName, group );
			}
		}
		
		/* 
		 * First heuristic: to match conceptId CURIE assuming that
		 * the namespace prefix implies a standard semantic group.
		 * Only currently have a few known namespaces (above)
		 */
		if(NameSpace.isCurie(id)) {
			
			group = NameSpace.getSemanticGroupByCurie(id);
			
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
			
			if(lcTag.endsWith(" target")) return assignedGroup(id, lcName, "CHEM");
			
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
					_logger.debug("SemanticGroup.makeSemGroup(): encountered unrecognized tag: '"+
									tag+"' in item '"+id+"' called '"+name+"'");
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
				lcName.contains("network") ||
				lcName.contains("signaling") ||
				lcName.contains("regulation") ||
				lcName.contains("metabolism") ||
				lcName.contains("biosynthesis") ||
				lcName.contains("degradation") ||
				lcName.contains("transcription") ||
				lcName.contains("translation") ||
				lcName.contains("secretion")
				
		)  return assignedGroup(id, lcName, "PHYS");
		
		if( 
				lcName.contains("receptor") ||
				lcName.contains("ligand") ||
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
		
		if( 
				lcName.contains("homo sapiens") ||
				lcName.contains("mus") 
				
		)  return assignedGroup(id, lcName, "LIVB");
		
		/*
		 *  Check the cache for indexing by exact name.
		 *  
		 *  Here, the caching is less a question of
		 *  performance than it is a question of
		 *  a penultimate last ditch inference of 
		 *  data type based on the name as a symbol.
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
		
		/*
		 * Final heuristic: some id's observed in nDex
		 * contain embedded substrings which suggest 
		 * a particular semantic group membership
		 */
		
		if( 
				id.toLowerCase().contains("biosource")
				
		)  return assignedGroup(id, lcName, "LIVB");
		
		if( 
				id.toLowerCase().contains("pathway") ||
				id.toLowerCase().contains("reactome")
				
		)  return assignedGroup(id, lcName, "PHYS");

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
