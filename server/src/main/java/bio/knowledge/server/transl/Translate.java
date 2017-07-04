package bio.knowledge.server.transl;

import java.util.Arrays;
import java.util.stream.Collectors;

import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.NetworkSummary;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.model.InlineResponse2002;
import bio.knowledge.server.model.InlineResponse2003;
import bio.knowledge.server.model.StatementsObject;
import bio.knowledge.server.model.StatementsPredicate;
import bio.knowledge.server.model.StatementsSubject;

public class Translate {
		
	private static String createId(Node node) {
		return node.getNetworkId() + ":" + node.getId();
	}
	
	private static StatementsSubject nodeToSubject(Node node) {
		
		StatementsSubject subject = new StatementsSubject();
		
		subject.setId(createId(node));
		subject.setName(node.getName());
		
		return subject;
	}
	
	private static StatementsPredicate edgeToPredicate(Edge edge) {
		
		StatementsPredicate predicate = new StatementsPredicate();
		
		predicate.setId(edge.getId().toString()); // todo: need proper ID
		predicate.setName(edge.getName());
		
		return predicate;
	}
	
	private static StatementsObject nodeToObject(Node node) {
		
		StatementsObject object = new StatementsObject();
		
		object.setId(createId(node));
		object.setName(node.getName());
		
		return object;
	}

	public static InlineResponse2002 nodeToConcept(Node node) {
		
		InlineResponse2002 concept = new InlineResponse2002();
		
		concept.setId(createId(node));
		concept.setName(node.getName());
		concept.setSemanticGroup("OBJC");
		
		return concept;
	}
	
	public static InlineResponse2003 edgeToStatement(Edge edge) {
	
		InlineResponse2003 statement = new InlineResponse2003();
		
		statement.setId(edge.getId().toString());
		statement.setSubject(nodeToSubject(edge.getSubject()));
		statement.setPredicate(edgeToPredicate(edge));
		statement.setObject(nodeToObject(edge.getObject()));
		
		return statement;
	}

}
