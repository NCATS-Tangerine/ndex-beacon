package bio.knowledge.server.impl;


import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.server.impl.SemanticGroup.NameSpace;
import bio.knowledge.server.json.Attribute;
import bio.knowledge.server.json.Citation;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.model.Annotation;
import bio.knowledge.server.model.Concept;
import bio.knowledge.server.model.ConceptDetail;
import bio.knowledge.server.model.ConceptWithDetails;
import bio.knowledge.server.model.Statement;
import bio.knowledge.server.model.StatementObject;
import bio.knowledge.server.model.StatementPredicate;
import bio.knowledge.server.model.StatementSubject;

@Service
public class Translator {
	
	@Autowired AliasNamesRegistry aliasRegistry;

	@Autowired PredicatesRegistry predicateRegistry;

	/*
	 *  Not sure if this is the best choice but better than a colon, 
	 *  which is a CURIE namespace delimiter whereas the hash is 
	 *  accepted in IRI parts of CURIES as a fragment delimiter, 
	 *  which in effect, a node/edge part of a network kind of is...
	 */
	public static final String    NETWORK_NODE_DELIMITER = "#";
	public static final Character NETWORK_NODE_DELIMITER_CHAR = '#';
	
	public static final String NDEX_NS = "NDEX:";

	private String makeNdexId(Node node) {
		return NDEX_NS + node.getNetworkId() + NETWORK_NODE_DELIMITER + node.getId();
	}
	
	public String makeId(Node node) {
		String represents = node.getRepresents();
		String nDexId = makeNdexId(node);
		if( represents != null ) {
			if( ! aliasRegistry.containsKey(represents))
				aliasRegistry.indexAlias(represents, nDexId);
			return represents;
		} else 
			return nDexId;
	}
	
	private String makeId(Edge edge) {
		return makeNdexId(edge.getSubject()) + "_" + edge.getId();
	}
	
	/**
	 * Guesses type of a node if it has a recognized type-ish property.
	 * @param node
	 * @return
	 */
	public String makeSemGroup(Node node) {
		String nodeId = makeId(node) ;
		return makeSemGroup(nodeId,node) ;
	}
	
	public String makeSemGroup( String conceptId, Node node ) { 
		
		// First heuristic: to match on recorded Node types?
		List<String> types = node.getByRegex("(?i).+type");
		String nodeName = node.getName();
		
		return SemanticGroup.makeSemGroup( conceptId, nodeName, types );
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
	public Concept nodeToConcept(Node node) {
		
		Concept concept = new Concept();
		
		String conceptId = makeId(node) ; 
		
		concept.setId(conceptId);
		concept.setName(makeName(node));
		concept.setSemanticGroup(makeSemGroup(conceptId, node));
		concept.setSynonyms(node.getSynonyms());
		
		return concept;
	}

	
	private ConceptDetail makeDetail(String name, String value) {
		ConceptDetail detail = new ConceptDetail();
		detail.setTag(name);
		detail.setValue(value);
		return detail;	
	}
	
	private List<ConceptDetail> attributeToDetails(Attribute attribute) {
		
		Function<String, ConceptDetail> valueToDetail = Util.curry(this::makeDetail, attribute.getName());
		List<ConceptDetail> details = Util.map(valueToDetail, attribute.getValues());
		return details;
	}
	
	public ConceptWithDetails nodeToConceptDetails(Node node) {
		
		ConceptWithDetails conceptDetails = new ConceptWithDetails();
		
		String conceptId = makeId(node) ; 
		
		conceptDetails.setId(conceptId);
		
		conceptDetails.setName(makeName(node));
		
		conceptDetails.setSemanticGroup(makeSemGroup(conceptId,node));
		
		Consumer<String> addSynonym = s -> conceptDetails.addSynonymsItem(s);
		node.getSynonyms().forEach(addSynonym);

		List<ConceptDetail> details = Util.flatmap(this::attributeToDetails, node.getAttributes());
		conceptDetails.setDetails(details);
		
		return conceptDetails;
	}
	
	
	private StatementSubject nodeToSubject(Node node) {
		
		StatementSubject subject = new StatementSubject();
		
		String conceptId = makeId(node);
		subject.setId(conceptId);
		
		subject.setName(makeName(node));
		
		subject.setSemanticGroup(makeSemGroup(conceptId,node));
		
		return subject;
	}
	
	private StatementPredicate edgeToPredicate(Edge edge) {
		
		StatementPredicate predicate = new StatementPredicate();
		
		/*
		 * Harvest the Predicate here? 
		 * Until you have a better solution, just
		 * convert the name into a synthetic CURIE
		 */
		String pName  = edge.getName();
		String pCurie = "";
		if(NameSpace.isCurie(pName))
			/*
			 *  The edgename looks like a 
			 *  CURIE so use it directly
			 */
			pCurie = pName;
		else
			// Treat as an nDex defined CURIE
			pCurie = NDEX_NS+edge.getName().trim().replaceAll("\\s", "_");
		
		predicateRegistry.indexPredicate( pCurie, pName, "" );
		
		predicate.setId(pCurie);
		predicate.setName(pName);
		
		return predicate;
	}
	
	private StatementObject nodeToObject(Node node) {
		
		StatementObject object = new StatementObject();
		
		String conceptId = makeId(node);
		object.setId(conceptId);
		
		object.setName(makeName(node));
		
		object.setSemanticGroup(makeSemGroup(conceptId,node));
		
		return object;
	}
	
	public Statement edgeToStatement(Edge edge) {
		
		Statement statement = new Statement();
		
		statement.setId(makeId(edge));
		statement.setSubject(nodeToSubject(edge.getSubject()));
		statement.setPredicate(edgeToPredicate(edge));
		statement.setObject(nodeToObject(edge.getObject()));
		
		return statement;
	}

	
	public Annotation citationToEvidence(Citation citation) {
		
		Annotation evidence = new Annotation();
		evidence.setId(citation.getCitationId());
		evidence.setLabel(citation.getFullText());
		return evidence;
	}

	
}
