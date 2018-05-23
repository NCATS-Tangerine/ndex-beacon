/**
 * 
 */
package bio.knowledge.server.ontology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.*;

import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.server.impl.Util;

/**
 * This class, segregated from the main nDex beacon code
 * is potentially generic for use in other beacons to attempt
 * proper semantic group labeling using some mundane heuristics.
 * 
 * @author Richard
 *
 */
public class NdexConceptCategoryService {
	
	private static Logger _logger = LoggerFactory.getLogger(NdexConceptCategoryService.class);	
	
	public enum NameSpace {
		
		NCBIGENE( "gene", new String[]{
				"http://identifiers.org/ncbigene",
				"https://www.ncbi.nlm.nih.gov/gene"
		} ),
		HGNC_SYMBOL("gene", new String[] {}),
		GENECARDS("gene", new String[] {"http://www.genecards.org/"} ),
		UNIPROT("protein", new String[] {
				"http://identifiers.org/uniprot",
				"http://www.uniprot.org/"
		} ),
		CHEBI("chemical substance", new String[] { "http://identifiers.org/chebi" } ),
		DRUGBANK("drug", new String[] {
				"http://identifiers.org/drugbank/",
				"http://bio2rdf.org/drugbank"
		} ),
		KEGG("pathway", 
				new String[] {
					"http://identifiers.org/kegg",
					"http://www.genome.jp/kegg/"
		} ), // Kyoto Encyclopedia of Genes and Genomes
		KEGG_PATHWAY("pathway", 
				new String[] {
					"http://identifiers.org/kegg",
					"http://www.genome.jp/kegg/"
		} ), // Kyoto Encyclopedia of Genes and Genomes
		PMID("information content entity", 
				new String[] {
					"http://identifiers.org/pubmed",
					"https://www.ncbi.nlm.nih.gov/pubmed"
				} ), // PubMed
		REACT("pathway", new String[] {"https://reactome.org/"} ),    // REACTome == pathways?
		REACTOME("pathway", new String[] {"https://reactome.org/"} ), // REACTOME == pathways?
		BP("pathway", new String[] {"http://www.biopax.org/"} ), // BioPAX
		PATHWAYCOMMONS("PHYS", 
				new String[] {
						"http://purl.org/pc2/7",
						"http://www.pathwaycommons.org/pc2"
				} ),  // Pathway Commons
		
		MIR("transcript", 
				new String[] {
						"http://identifiers.org/mirtarbase",
						"http://mirtarbase.mbc.nctu.edu.tw/#"
				}), // mirtarbase - micro RNA targets
		
		SMPDB("pathway", new String[] {"http://smpdb.ca/"} ),   // Small Molecular Pathway Database
		SIGNOR("pathway",new String[] {"https://signor.uniroma2.it/relation_result.php?id="}),  // signaling network open resource
		;
		
		static private Logger _logger = LoggerFactory.getLogger(NameSpace.class);	
		
		private  String[] uris;
		private String category;
		
		private NameSpace( String category, String[] uris ) {
			this.category = category;
			this.uris = uris ;
		}
		
		static public String getConceptCategoryByCurie(String id) {
			
			if(Util.nullOrEmpty(id)) return "";
			
			String[] namespace = id.toUpperCase().split(":");
			String prefix = namespace[0];
			
			prefix = prefix.toUpperCase().replaceAll("\\.", "_");
			
			try {
				
				NameSpace bns = NameSpace.valueOf(prefix);
				return bns.category;
				
			} catch( IllegalArgumentException e) {
				/*
				 * Highlight newly encountered prefixes *other than* 
				 * NDEX (which is a locally assigned designation)
				 */
				if( ! prefix.toString().equals("NDEX"))
					_logger.debug("nDexBio node id prefix '"+prefix.toString()+
							      "' encountered is not yet mapped to a concept category?");
				
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
		
		static public String getConceptCategoryByUri(String id) {
			
			if(Util.nullOrEmpty(id)) return "";
			
			String baseuri;
			if(id.contains("#")) {
				baseuri = getBaseUri(id,"#"); // URI ending in a hash?
			} else {
				baseuri = getBaseUri(id);
			}
			
			for( NameSpace ns : NameSpace.values()) {
				for( String uri : ns.uris )
					if( uri.equals( baseuri.toLowerCase() ) )
						return ns.category;
			}
			
			// no URI mapping found?
			_logger.warn("NdexConceptCategoryService.NameSpace.getConceptCategoryByUri(): URI  '"+id+"' not recognized?");
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
	private static Map<String,String> conceptCategoryCache = new HashMap<String,String>();
	
	/*
	 * functions to cache newly discovered semantic groups,
	 * indexed against id (and possibly name) on the fly...
	 */
	private static String assignedCategory(String id, String group) {
		conceptCategoryCache.put(id, group);
		return group;
	}

	private static String assignedCategory(String id, String name, String group) {
		assignedCategory(id, group);
		conceptCategoryCache.put(name, group);
		return group;
	}
	
	/*
	 * Stock Regular Expression for Clinical Variance name detection
	 */
	static private Pattern variantNamePattern = Pattern.compile("c\\.(\\d+\\_)?\\d+(([atcg]{1,2}\\>[atcg]{1,2})|(del[atcg]+))");
	
	static public String inferConceptCategory(String id, String name, List<String> properties ) {
		
		String category;
		
		/*
		 *  Check the cache first...
		 *  Maybe already indexed by id...
		 *  This is a "hard" assignment
		 *  of semantic group to a given 
		 *  "concept" identifier.
		 */
		if(conceptCategoryCache.containsKey(id)) {
			category = conceptCategoryCache.get(id);
			if(! Util.nullOrEmpty(category) ) 
				return category; // return if found in cache
		} 
		
		/*
		 *  Otherwise, currently unknown semantic group: 
		 *  attempt to assign by various heuristics
		 */
		
		// Pre-process the node name here for uniform treatment
		String lcName = "";
		if(! Util.nullOrEmpty(name) )
			lcName = name.toLowerCase();
		else
			// What other option do I have here?
			lcName = NameSpace.makeName(id);

		
		// Special heuristics based on special node name patterns
		
		/* 
		 * Pattern #1 - Post-translational modified proteins of identified gene loci.
		 * 
		 * These node names have the pattern of a (HGNC?) gene symbol 
		 * followed by a space, then a specific code then followed by 
		 * another space then the single letter code for the 
		 * post-translationally conjugated amino acid residue which is
		 * immediately followed by its numeric position.
		 * 
		 * For example:
		 * 
		 *               SMCR8 p S498
		 * 
		 * designates the SMCR8 ("Smith-Magenis syndrome chromosomal region candidate gene 8 protein),
		 * where the 'p' code in between designates the phosphorylation, in this case,
		 * at the serine ('S') amino acid residue, at primary peptide sequence position 498
		 * (note that phosphorylation commonly occurs at S = serine or Y = tyrosine residues)
		 * 
		 * Another example:
		 * 
		 *               SLC25A5 ack K33
		 * 
		 * designates the SLC25A5 ("Solute Carrier Family 25 Member 5") where the 'ack' code in between
		 * designates the acetylation of a lysine (K) amino acid residue at peptide position 33.
		 * 
		 * Similarly
		 * 
		 *            ZC3H18 rme R239  // methylated arginine
		 * 
		 *            NUP98 kme K1128  // methylated lysine
		 * 
		 * nDex has networks encoding these kinds of post-translational modified proteins (ptmp)
		 */
		
		String[] ptmp = lcName.split("\\s");
		
		if(ptmp.length==3) {
			
			String modification = ptmp[1].toLowerCase();
			
			switch (modification) {	
				case "p":    // phosophylated protein
				case "ack":  // acetylated lysine
				case "rme":  // methylated arginine
				case "kme":  // methylated lysine
					return assignedCategory( id, lcName, BiolinkTerm.PROTEIN.getLabel() );
			}
		}
		
		/*
		 * Pattern # 2 - Genetic Variants
		 * 
		 * The node names of genetic variants ("ClinVar") also have a predictable format, namely:
		 * 
		 *                c.1415C>T
		 * 
		 * Where the 1415 nucleotide position has a Single Nucleotide Polymorphism, 
		 * in this case, a cytosine (C) to thymine (T) transition.
		 */
		Matcher isVariantName = variantNamePattern.matcher(lcName); 
		
		if(isVariantName.matches()) {
			return assignedCategory( id, lcName, BiolinkTerm.SEQUENCE_VARIANT.getLabel());
		}
		
		// Some nodes are unnamed but have URI's?
		if( NameSpace.isURI(id) ) {
			
			category = NameSpace.getConceptCategoryByUri(id);
			
			if( !category.isEmpty() ) {
				return assignedCategory( id, lcName, category );
			}
		}
		
		/* 
		 * First heuristic: to match conceptId CURIE assuming that
		 * the namespace prefix implies a standard semantic group.
		 * Only currently have a few known namespaces (above)
		 */
		if(NameSpace.isCurie(id)) {
			
			category = NameSpace.getConceptCategoryByCurie(id);
			
			if(!category.isEmpty()) 
				return assignedCategory( id, lcName, category );
			
		}

		/*
		 *  Second heuristic: scan caller-submitted 
		 *  property tags for clues?
		 */
		for (String tag : properties) {
			
			if(tag.equals("undefined")) continue; // ignore 'undefined' tags.. likely empty properties
			
			String lcTag = tag.toLowerCase();
			
			if(lcTag.endsWith(" gene")) return assignedCategory(id, lcName, BiolinkTerm.GENE.getLabel() );
			
			if(lcTag.endsWith(" protein")) return assignedCategory(id, lcName, BiolinkTerm.PROTEIN.getLabel());
			
			if(lcTag.endsWith(" target")) return assignedCategory(id, lcName, BiolinkTerm.CHEMICAL_SUBSTANCE.getLabel());
			
			lcTag = lcTag.replace(" ", "");
			
			switch (lcTag) {	
				case "disease": 
					return assignedCategory(id, lcName, BiolinkTerm.DISEASE.getLabel());
				case "gene":
				case "methyltransferase":
					return assignedCategory(id, lcName, BiolinkTerm.GENE.getLabel());
				case "pathway": 
					return assignedCategory(id, lcName, BiolinkTerm.PATHWAY.getLabel());
				case "protein": 
				case "proteinreference":
				case "transcriptionfactor":
					return assignedCategory(id, lcName, BiolinkTerm.PROTEIN.getLabel());
				case "rna": 
				case "mirna": 
					return assignedCategory(id, lcName, BiolinkTerm.TRANSCRIPT.getLabel());
				case "smallmolecule": 
					return assignedCategory(id, lcName, BiolinkTerm.MOLECULAR_ENTITY.getLabel());
				case "smallmoleculedrug": 
					return assignedCategory(id, lcName, BiolinkTerm.DRUG.getLabel());
				case "transcriptionregulator":
					return assignedCategory(id, lcName, BiolinkTerm.GENE_OR_GENE_PRODUCT.getLabel() );
				default:
					_logger.debug("NdexConceptCategoryService.makeSemGroup(): encountered unrecognized tag: '"+
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
				
		) return assignedCategory(id, lcName, BiolinkTerm.PROCEDURE.getLabel());
		
		if( 
				lcName.contains("pathway") 
				
		)  return assignedCategory(id, lcName, BiolinkTerm.PATHWAY.getLabel());
		
		if( 
				lcName.contains("network") ||
				lcName.contains("signaling") ||
				lcName.contains("regulation") ||
				lcName.contains("metabolism") ||
				lcName.contains("biosynthesis") ||
				lcName.contains("degradation") ||
				lcName.contains("transcription") ||
				lcName.contains("translation") ||
				lcName.contains("secretion")
				
		)  return assignedCategory(id, lcName, BiolinkTerm.PHYSIOLOGICAL_PROCESS.getLabel());
		
		if( 
				lcName.contains("microrna")
				
		) return assignedCategory(id, lcName, BiolinkTerm.TRANSCRIPT.getLabel());

		if( 
				lcName.contains("receptor") ||
				lcName.contains("peptide")  ||
				lcName.contains("protein")
			
		) return assignedCategory(id, lcName, BiolinkTerm.PROTEIN.getLabel());

		if( 
				lcName.contains("vaccine")
				
		) return assignedCategory(id, lcName, BiolinkTerm.DRUG.getLabel());

		if( 
				lcName.contains("ligand") ||
				
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
				lcName.endsWith("mab") ||
				lcName.endsWith("platin") ||
				lcName.endsWith("inib") ||
				lcName.endsWith("arib") ||
				lcName.endsWith("ane")
				
		) return assignedCategory(id, lcName, BiolinkTerm.CHEMICAL_SUBSTANCE.getLabel());

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
				
		) return assignedCategory(id, lcName, BiolinkTerm.DISEASE.getLabel());
		
		if( 
				lcName.contains("cell") ||
				lcName.contains("tissue") ||
				lcName.contains("heart") ||
				lcName.contains("lung") ||
				lcName.contains("kidney") ||
				lcName.contains("skin") ||
				lcName.contains("brain")
				
		)  return assignedCategory(id, lcName, BiolinkTerm.ANATOMICAL_ENTITY.getLabel());
		
		if( 
				lcName.contains("homo sapiens") ||
				lcName.contains("mus") 
				
		)  return assignedCategory(id, lcName, BiolinkTerm.INDIVIDUAL_ORGANISM.getLabel());
		
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
		if(conceptCategoryCache.containsKey(lcName)) {
			category = conceptCategoryCache.get(lcName);
			if(! Util.nullOrEmpty(category) ) { 
				/*  
				 * Return if found by name in the cache.
				 * Also index it against the current id
				 */
				return assignedCategory( id, category );
			}
		}
		
		/*
		 * Final heuristic: some id's observed in nDex
		 * contain embedded substrings which suggest 
		 * a particular semantic group membership
		 */
		
		if( 
				id.toLowerCase().contains("biosource")
				
		)  return assignedCategory(id, lcName, BiolinkTerm.BIOLOGICAL_ENTITY.getLabel());
		
		if( 
				id.toLowerCase().contains("pathway") ||
				id.toLowerCase().contains("reactome")
				
		)  return assignedCategory(id, lcName, BiolinkTerm.PATHWAY.getLabel());

		// Give up for now...
		_logger.debug("NdexConceptCategoryService.makeSemGroup(): encountered semantically "
				    + "unclassified item '"+id+"'called '"+name+"'");			

		/*
		 *  We hesitate to cache our default guess here
		 *  in the event that we discover a more sensible
		 *  mapping (i.e. to the concept name) by some
		 *  other means, in the future...
		 */
		return BiolinkTerm.NAMED_THING.getLabel();
	}
}
