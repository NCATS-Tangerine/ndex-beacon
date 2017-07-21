package bio.knowledge.server.impl;


import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import bio.knowledge.server.json.Attribute;
import bio.knowledge.server.json.Citation;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.json.Support;
import bio.knowledge.server.model.ConceptsconceptIdDetails;
import bio.knowledge.server.model.InlineResponse2001;
import bio.knowledge.server.model.InlineResponse2002;
import bio.knowledge.server.model.InlineResponse2003;
import bio.knowledge.server.model.InlineResponse2004;
import bio.knowledge.server.model.StatementsObject;
import bio.knowledge.server.model.StatementsPredicate;
import bio.knowledge.server.model.StatementsSubject;

@Service
public class Translator {
		
	
	private String makeNdexId(Node node) {
		return node.getNetworkId() + ":" + node.getId();
	}
	
	private String makeId(Node node) {
		String represents = node.getRepresents();
		return represents == null? makeNdexId(node) : represents;
	}
	
	private String makeId(Edge edge) {
		return makeNdexId(edge.getSubject()) + "_" + edge.getId();
	}
	
	public String makeSemGroup(Node node) { 
		
		List<String> types = node.getByRegex("(?i).+type");
		
		for (String type : types) {
			switch (type.toLowerCase().replace(" ", "")) {
				case "disease": return "DISO";
				case "protein": return "CHEM";
				case "smallmolecule": return "CHEM";
				case "smallmoleculedrug": return "CHEM";
			}
		}
		
		return "OBJC";
	}
	
	
	public InlineResponse2002 nodeToConcept(Node node) {
		
		InlineResponse2002 concept = new InlineResponse2002();
		
		concept.setId(makeId(node));
		concept.setName(node.getName());
		concept.setSemanticGroup(makeSemGroup(node));
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
		
		System.out.println("123 attr name: " + attribute.getName());
		System.out.println("123 attr values: " + attribute.getValues());
		Function<String, ConceptsconceptIdDetails> valueToDetail = Util.curry(this::makeDetail, attribute.getName());
		List<ConceptsconceptIdDetails> details = Util.map(valueToDetail, attribute.getValues());
		return details;
	}
	
	public InlineResponse2001 nodeToConceptDetails(Node node) {
		
		InlineResponse2001 conceptDetails = new InlineResponse2001();
		
		
		conceptDetails.setId(makeId(node));
		conceptDetails.setName(node.getName());
		conceptDetails.setSemanticGroup(makeSemGroup(node));
		
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
	
	public InlineResponse2003 edgeToStatement(Edge edge) {
		
		InlineResponse2003 statement = new InlineResponse2003();
		
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
