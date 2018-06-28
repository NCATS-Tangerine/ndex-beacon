package bio.knowledge.server.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.server.json.Attribute;
import bio.knowledge.server.json.Citation;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.NetworkProperty;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.model.BeaconConcept;
import bio.knowledge.server.model.BeaconConceptDetail;
import bio.knowledge.server.model.BeaconConceptWithDetails;
import bio.knowledge.server.model.BeaconStatement;
import bio.knowledge.server.model.BeaconStatementAnnotation;
import bio.knowledge.server.model.BeaconStatementCitation;
import bio.knowledge.server.model.BeaconStatementObject;
import bio.knowledge.server.model.BeaconStatementPredicate;
import bio.knowledge.server.model.BeaconStatementSubject;
import bio.knowledge.server.model.BeaconStatementWithDetails;
import bio.knowledge.server.ontology.NdexConceptCategoryService;
import bio.knowledge.server.ontology.NdexConceptCategoryService.NameSpace;
import bio.knowledge.server.ontology.OntologyService;

@Service
public class Translator {
	
	@Autowired AliasNamesRegistry aliasRegistry;

	@Autowired PredicatesRegistry predicateRegistry;
	
	@Autowired OntologyService ontology;
	
	@Autowired KnowledgeMapRegistry knowledgeMapRegistry;
	
	private static Logger _logger = LoggerFactory.getLogger(Translator.class);

	/*
	 *  Not sure if this is the best choice but better than a colon, 
	 *  which is a CURIE namespace delimiter whereas the hash is 
	 *  accepted in IRI parts of CURIES as a fragment delimiter, 
	 *  which in effect, a node/edge part of a network kind of is...
	 */
	public static final String    NETWORK_NODE_DELIMITER = "_";
	public static final Character NETWORK_NODE_DELIMITER_CHAR = '_';
	
	public static final String NDEX_NS = "NDEX:";
	public static final String DEFINED_BY = "http://starinformatics.com";

	private static final String NDEX_URL = "http://www.ndexbio.org/#/network/";

	Map<StatementTriple, Integer> subjectObjectRegistry = new HashMap<StatementTriple, Integer>();
	Map<String, Set<String>> categoryPrefixRegistry = new HashMap<String, Set<String>>();

	private String makeNdexId(Node node) {
		return NDEX_NS + node.getNetworkId() + NETWORK_NODE_DELIMITER + node.getId();
	}
	
	/**
	 * Returns node's CURIE if it exists, matches CURIE-like structure,
	 * registers corresponding ndex networkId+identifier into the aliasRegistry
	 * Otherwise returns a NdexId
	 * @param node
	 * @return CURIE or NDEX identifier
	 */
	public String makeId(Node node) {
		String represents = node.getRepresents();
		String nDexId = makeNdexId(node);
		if( represents != null && hasCurieStructure(represents) ) {
			if( ! aliasRegistry.containsKey(represents))
				aliasRegistry.indexAlias(represents, nDexId);
			return represents;
		} else 
			return nDexId;
	}
	
	/**
	 * Some CURIEs in the represents field are not actual CURIEs and are more like descriptions or
	 * even hyperlinks to resources
	 * This simple heuristic returns false for strings that have spaces and hyperlinks
	 * @param represents
	 * @return
	 */
	private boolean hasCurieStructure(String represents) {
		try {
			String reference = represents.split(":")[1];
			return (!(reference.contains(" ")) && (!(reference.contains("http"))));
		} catch (Exception e) {
			return false;
		}
	}

	private String makeId(Edge edge) {
		return makeNdexId(edge.getSubject()) + "_" + edge.getId();
	}
	
	/**
	 * Guesses type of a node if it has a recognized type-ish property.
	 * @param node
	 * @return
	 */
	public String inferConceptCategory(Node node) {
		String nodeId = makeId(node) ;
		return inferConceptCategory(nodeId,node) ;
	}
	
	public String inferConceptCategory( String conceptId, Node node ) { 
		// First heuristic: to match on recorded Node types?
		List<String> types = node.getByRegex("(?i).+type");
		String nodeName = node.getName();
		String category = NdexConceptCategoryService.inferConceptCategory( conceptId, nodeName, types );
		//return ontology.umlsToBiolinkCategory(category);
		return category; //  Biolink Model categories now directly inferred
	}
	
	/**
	 * Retrieve or infer a suitable name
	 * from the Node name or identifier
	 * 
	 * @param node
	 * @return
	 */
	public String makeName(Node node) {
		
		String name = node.getName();
		if(! Util.nullOrEmpty(name))
			return name;
		
		// Need to infer another name
		String id = makeId(node);
		
		return NameSpace.makeName(id);
	}

	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public BeaconConcept nodeToConcept(Node node) {
		
		BeaconConcept concept = new BeaconConcept();
		
		String conceptId = makeId(node) ; 
		
		concept.setId(conceptId);
		concept.setName(makeName(node));
		concept.addCategoriesItem(inferConceptCategory(conceptId, node));
//		concept.setSynonyms(node.getSynonyms());
		
		return concept;
	}

	
	private BeaconConceptDetail makeDetail(String name, String value) {
		BeaconConceptDetail detail = new BeaconConceptDetail();
		detail.setTag(name);
		detail.setValue(value);
		return detail;	
	}
	
	private List<BeaconConceptDetail> attributeToDetails(Attribute attribute) {
		
		Function<String, BeaconConceptDetail> valueToDetail = Util.curry(this::makeDetail, attribute.getName());
		List<BeaconConceptDetail> details = Util.map(valueToDetail, attribute.getValues());
		return details;
	}
	
	public BeaconStatementWithDetails edgeToStatementDetails(Edge edge, String statementId) {

		BeaconStatementWithDetails result = new BeaconStatementWithDetails();

		result.setId(statementId);
		result.setIsDefinedBy(DEFINED_BY);
		String networkId = statementId.split(Translator.NETWORK_NODE_DELIMITER)[0].replaceFirst(Translator.NDEX_NS, "");
		result.setProvidedBy(NDEX_URL + networkId);

		if (edge.getAttributes() != null) {
			for (Attribute a : edge.getAttributes()) {
				for (String value : a.getValues()) {
					BeaconStatementAnnotation annotation = new BeaconStatementAnnotation();
					annotation.setTag(a.getName());
					annotation.setValue(value);
					result.addAnnotationItem(annotation);
				}
			}
		}

		if (edge.getCitations() != null) {
			for (Citation citation : edge.getCitations()) {
				BeaconStatementCitation beaconC = new BeaconStatementCitation();
				beaconC.setId(citation.getCitationId());
				beaconC.setName(formatCitationName(citation.getName(), citation.getFullText()));
				beaconC.setEvidenceType(citation.getEvidenceType());
				result.addEvidenceItem(beaconC);
			}
		}
		
		if (edge.getNetworkReference() != null) {
			BeaconStatementCitation citation = getCitationFromNetworkReference(edge.getNetworkReference());
			if (citation != null) {
				result.addEvidenceItem(citation);
			}
		}

		return result;
	}

	/**
	 * Parses html text from network reference property, using simple regexes, to determine citation id, url, and name.
	 * Some code from https://stackoverflow.com/questions/600733/using-java-to-find-substring-of-a-bigger-string-using-regular-expression
	 * @param properties
	 * @return citation parsed from reference property in a network, or null if reference could not be parsed
	 */
	private BeaconStatementCitation getCitationFromNetworkReference(NetworkProperty networkReference) {
		try {
			BeaconStatementCitation citation = new BeaconStatementCitation();
			String html = networkReference.getValue();
			final String URL_REGEX = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
			Matcher urlMatcher = Pattern.compile(URL_REGEX).matcher(html);
			if (urlMatcher.find()) {
				String url = urlMatcher.group();
				citation.setUri(url);
				citation.setId(url);
			}
			
//			final String DOI_REGEX = "doi:[-a-zA-Z0-9/. ]+";
			final String DOI_REGEX = "doi:.*";
			Matcher doiMatcher = Pattern.compile(DOI_REGEX).matcher(html);
			if (doiMatcher.find()) {
				String title = doiMatcher.group().replaceAll("<[^>]*>", "").replaceAll("&nbsp;", "");
				citation.setId(title);
			}
			
			final String TITLE_REGEX = "<b>.*</b>";
			Matcher titleMatcher = Pattern.compile(TITLE_REGEX).matcher(html);
			if (titleMatcher.find()) {
				String title = titleMatcher.group().replaceAll("<[^>]*>", "");
				citation.setName(title);
			} else {
				String fullText = html.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", "");
				citation.setName(fullText);
			}
			
			return citation;
			
		} catch (Exception e) {
			_logger.warn("Could not change networkReference into Citation: " + networkReference.getValue());
			return null;
		}
	}

	/**
	 * Formats citation name so any information from dc:title is separated from any 
	 * information from Supports with a | character
	 * @param name
	 * @param supportsText
	 * @return
	 */
	private String formatCitationName(String name, String supportsText) {
		if (name == null) {
			return supportsText;
		} else if (supportsText == null || supportsText.trim().isEmpty()) {
			return name;
		} else {
			return name + " | " + supportsText;
		}
	}

	public BeaconConceptWithDetails nodeToConceptDetails(Node node) {
		
		BeaconConceptWithDetails conceptDetails = new BeaconConceptWithDetails();
		
		String conceptId = makeId(node) ; 
		
		conceptDetails.setId(conceptId);
		conceptDetails.setUri(null);
		conceptDetails.setName(makeName(node));
		conceptDetails.setSymbol(null);
		conceptDetails.addCategoriesItem(inferConceptCategory(conceptId,node));

//		Consumer<String> addSynonym = s -> conceptDetails.addSynonymsItem(s);
//		node.getSynonyms().forEach(addSynonym);

//		List<BeaconConceptDetail> details = Util.flatmap(this::attributeToDetails, node.getAttributes());

		List<String> aliases = new ArrayList<>();
		List<BeaconConceptDetail> details = new ArrayList<>();
		
		for (Attribute attribute : node.getAttributes()) {
			if (attribute.getName().equals("alias")) {
				aliases.addAll(attribute.getValues());
			} else {
				details.addAll(attributeToDetails(attribute));
			}
		}

		conceptDetails.setExactMatches(aliases);
		conceptDetails.setDetails(details);
		
		return conceptDetails;
	}

	private BeaconStatementSubject nodeToSubject(Node node) {
		
		BeaconStatementSubject subject = new BeaconStatementSubject();
		
		String conceptId = makeId(node);
		subject.setId(conceptId);
		
		subject.setName(makeName(node));
		
		subject.addCategoriesItem(inferConceptCategory(conceptId,node));
		
		return subject;
	}
	
	// TODO: update biolink predicate matching and negated 
	private BeaconStatementPredicate edgeToPredicate(Edge edge) {
		
		BeaconStatementPredicate predicate = new BeaconStatementPredicate();
		
		/*
		 * Harvest the Predicate here? 
		 * Until you have a better solution, just
		 * convert the name into a synthetic CURIE
		 */
		String pName  = edge.getName();
		if(pName==null ||pName.isEmpty()) {
			pName = "related_to";
		}
		pName = pName.toLowerCase();
		String biolinkName = ontology.predToBiolinkEdgeLabel(pName);
		String pCurie = "";
		if(NameSpace.isCurie(pName))
			/*
			 *  The edgename looks like a 
			 *  CURIE so use it directly
			 */
			pCurie = pName;
		else
			// Treat as an nDex defined CURIE
			pCurie = ontology.convertToSnakeCase(NDEX_NS+edge.getName());
		
		predicateRegistry.indexPredicate( pCurie, biolinkName, "" );
		
		predicate.setRelation(pCurie);
		//predicate.setEdgeLabel(pName);
		predicate.setEdgeLabel(biolinkName);
		
		return predicate;
	}
	
	private BeaconStatementObject nodeToObject(Node node) {
		
		BeaconStatementObject object = new BeaconStatementObject();
		
		String conceptId = makeId(node);
		object.setId(conceptId);
		
		object.setName(makeName(node));
		
		object.addCategoriesItem(inferConceptCategory(conceptId,node));
		
		return object;
	}
	
	public BeaconStatement edgeToStatement(Edge edge) {
		
		BeaconStatement statement = new BeaconStatement();
		
		statement.setId(makeId(edge));
		statement.setSubject(nodeToSubject(edge.getSubject()));
		statement.setPredicate(edgeToPredicate(edge));
		statement.setObject(nodeToObject(edge.getObject()));
		
		knowledgeMapRegistry.indexKnowledgeMapEntry(statement);	
		return statement;
	}

}
