package bio.knowledge.server.impl;


import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import bio.knowledge.server.json.Attribute;
import bio.knowledge.server.json.Citation;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.model.Concept;
import bio.knowledge.server.model.ConceptsconceptIdDetails;
import bio.knowledge.server.model.InlineResponse2001;
import bio.knowledge.server.model.InlineResponse2004;
import bio.knowledge.server.model.Statement;
import bio.knowledge.server.model.StatementsObject;
import bio.knowledge.server.model.StatementsPredicate;
import bio.knowledge.server.model.StatementsSubject;

@Service
public class Translator {
	
	/*
	 *  Not sure if this is the best choice but better than a colon, 
	 *  which is a CURIE namespace delimiter whereas the hash is 
	 *  accepted in IRI parts of CURIES as a fragment delimiter, 
	 *  which in effect, a node/edge part of a network kind of is...
	 */
	public static final String NETWORK_NODE_DELIMITER = "#";
	public static final Character NETWORK_NODE_DELIMITER_CHAR = '#';
	
	public static final String NDEX_NS = "ndex:";
	
	private Logger _logger = LoggerFactory.getLogger(Translator.class);	
	
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
	
	public String makeSemGroup(String conceptId, Node node) { 
		
		// First heuristic: to match on recorded Node types?
		List<String> types = node.getByRegex("(?i).+type");
		
		for (String type : types) {
			switch (type.toLowerCase().replace(" ", "")) {
				case "disease": return "DISO";
				case "protein": return "CHEM";
				case "smallmolecule": return "CHEM";
				case "smallmoleculedrug": return "CHEM";
			}
		}
		
		// Second heuristic: to match on conceptId namespace prefix
		// Only have a few known namespaces...need to catalog the others?
		if(conceptId.contains(":")) {
			String[] namespace = conceptId.toUpperCase().split(":");
			switch(namespace[0]) {
			case "NCBIGENE":
			case "GENECARDS":
				return "GENE";
			case "UNIPROT":
				return "CHEM";
			case "KEGG":
			case "KEGG.PATHWAY":
				return "PHYS";
			default:
				_logger.info("'"+namespace[0]+"' nDexBio node id prefix is not yet mapped?");
				break;
			}
		}
		return "OBJC";
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

	
	private ConceptsconceptIdDetails makeDetail(String name, String value) {
		
		ConceptsconceptIdDetails detail = new ConceptsconceptIdDetails();
		detail.setTag(name);
		detail.setValue(value);
		return detail;	
	}
	
	private List<ConceptsconceptIdDetails> attributeToDetails(Attribute attribute) {
		
		Function<String, ConceptsconceptIdDetails> valueToDetail = Util.curry(this::makeDetail, attribute.getName());
		List<ConceptsconceptIdDetails> details = Util.map(valueToDetail, attribute.getValues());
		return details;
	}
	
	public InlineResponse2001 nodeToConceptDetails(Node node) {
		
		InlineResponse2001 conceptDetails = new InlineResponse2001();
		
		String conceptId = makeId(node) ; 
		
		conceptDetails.setId(conceptId);
		conceptDetails.setName(node.getName());
		conceptDetails.setSemanticGroup(makeSemGroup(conceptId,node));
		
		Consumer<String> addSynonym = s -> conceptDetails.addSynonymsItem(s);
		node.getSynonyms().forEach(addSynonym);

		List<ConceptsconceptIdDetails> details = Util.flatmap(this::attributeToDetails, node.getAttributes());
		conceptDetails.setDetails(details);
		
		return conceptDetails;
	}
	
	
	private StatementsSubject nodeToSubject(Node node) {
		
		StatementsSubject subject = new StatementsSubject();
		subject.setId(makeId(node));
		subject.setName(node.getName());
		return subject;
	}
	
	private StatementsPredicate edgeToPredicate(Edge edge) {
		
		StatementsPredicate predicate = new StatementsPredicate();
		predicate.setName(edge.getName());
		return predicate;
	}
	
	private StatementsObject nodeToObject(Node node) {
		
		StatementsObject object = new StatementsObject();
		
		object.setId(makeId(node));
		object.setName(node.getName());
		
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

	
	public InlineResponse2004 citationToEvidence(Citation citation) {
		
		InlineResponse2004 evidence = new InlineResponse2004();
		evidence.setId(citation.getCitationId());
		evidence.setLabel(citation.getFullText());
		return evidence;
	}

	
}
