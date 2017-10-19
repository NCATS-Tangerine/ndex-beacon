package bio.knowledge.server.impl;


import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	@Autowired PredicatesRegistry predicateRegistry;

	/*
	 *  Not sure if this is the best choice but better than a colon, 
	 *  which is a CURIE namespace delimiter whereas the hash is 
	 *  accepted in IRI parts of CURIES as a fragment delimiter, 
	 *  which in effect, a node/edge part of a network kind of is...
	 */
	public static final String    NETWORK_NODE_DELIMITER = "#";
	public static final Character NETWORK_NODE_DELIMITER_CHAR = '#';
	
	public static final String NDEX_NS = "ndex:";

	private String makeNdexId(Node node) {
		return node.getNetworkId() + NETWORK_NODE_DELIMITER + node.getId();
	}
	
	private String makeId(Node node) {
		String represents = node.getRepresents();
		return represents == null? makeNdexId(node) : represents;
	}
	
	private String makeId(Edge edge) {
		return NDEX_NS+makeNdexId(edge.getSubject()) + "_" + edge.getId();
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
		String nodeName = node.getName().toLowerCase();
		
		return SemanticGroup.makeSemGroup( conceptId, nodeName, types );
	}
	
	public Concept nodeToConcept(Node node) {
		
		Concept concept = new Concept();
		
		String conceptId = makeId(node) ; 
		
		concept.setId(conceptId);
		concept.setName(node.getName());
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
		
		conceptDetails.setName(node.getName());
		
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
		
		subject.setName(node.getName());
		
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
		String pCurie = "ndex:"+edge.getName().trim().replaceAll("\\s", "_");
		predicateRegistry.indexPredicate( pCurie, pName, "" );
		
		predicate.setId(pCurie);
		predicate.setName(pName);
		
		return predicate;
	}
	
	private StatementObject nodeToObject(Node node) {
		
		StatementObject object = new StatementObject();
		
		String conceptId = makeId(node);
		object.setId(conceptId);
		
		object.setName(node.getName());
		
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
