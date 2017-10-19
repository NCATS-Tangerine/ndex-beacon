/**
 * 
 */
package bio.knowledge.server.impl;

import java.util.List;

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
	
	public static String makeSemGroup(String id, String name, List<String> properties ) { 
		
		// First heuristic: scan caller submitted property tags for clues?

		for (String tag : properties) {
			
			switch (tag.toLowerCase().replace(" ", "")) {	
				case "disease": return "DISO";
				case "protein": return "CHEM";
				case "smallmolecule": return "CHEM";
				case "smallmoleculedrug": return "CHEM";
				default:
					_logger.debug("SemanticGroup.makeSemGroup(): encountered unrecognized tag: "+tag);
			}
		}
		
		/* 
		 * Second heuristic: take a look at the node name to match common 
		 * "big picture" key words like 'disease' and 'cancer'. Do this first
		 * before scrutiny of CURIES (below) given polymorphism in the
		 * semantic groups of CURIE namespaces like KEGG, REACT and SMPDB
		 */
		if( 
				name.contains("cancer") ||
				name.contains("disease") ||
				name.contains("failure") ||
				name.contains("dysfunction") ||
				name.contains("disorder") ||
				name.contains("deficiency") ||
				name.contains("injury") ||
				name.contains("path") ||
				name.contains("rejection") ||
				name.contains("hypoplasia") ||
				name.contains("hyperplasia") ||
				name.contains("aciduria") ||
				name.contains("syndrome") 
				
		) return "DISO";
		
		/* 
		 * Fourth heuristic: continue to take a look at the node name to match 
		 * other common contextual key words or syllables 
		 */
		if( 
				name.contains("pathway") ||
				name.contains("signaling") ||
				name.contains("metabolism") ||
				name.contains("biosynthesis") ||
				name.contains("transcription") ||
				name.contains("translation") ||
				name.contains("secretion")
				
		)  return "PHYS";
		
		if( 
				name.contains("vaccine") ||
				name.contains("peptide") ||
				name.contains("protein") ||
				name.contains("microrna") ||
				
				// Common subgroups in names - are there others?
				name.contains("hydroxy") ||
				name.contains("methyl") ||
				name.contains("phenyl") ||
				name.contains("amino") ||
				name.contains("acid") ||
				
				/*
				 *  is there a better way to guess the nature of drugs...
				 *  maybe API to look up of names somewhere?
				 *  e.g. https://open.fda.gov/drug/label/reference/??
				 */
				name.endsWith("inib") ||
				name.endsWith("mab")
				
		) return "CHEM";

		
		if( 
				name.contains("therapy") ||
				name.contains("transplant")
				
		) return "PROC";
		
		/* 
		 * Third heuristic: to match on conceptId CURIE namespace prefix
		 * Only currently have a few known namespaces (above)
		 */
		if(id.contains(":")) {
			
			String[] namespace = id.toUpperCase().split(":");
			String sg = NameSpace.getSemanticGroup(namespace[0]);
			if(!sg.isEmpty()) return sg;
			
		} /*
		   * else, if I fall through here, continue 
		   * searching generically on some keywords
		   */
		
		if( 
				name.contains("cell") ||
				name.contains("tissue") ||
				name.contains("heart") ||
				name.contains("lung") ||
				name.contains("kidney") ||
				name.contains("skin") ||
				name.contains("brain")
				
		)  return "ANAT";
		
		// Give up for now...
		_logger.debug("SemanticGroup.makeSemGroup(): encountered semantically unclassified item called: "+name);			

		return "OBJC";
	}
}
