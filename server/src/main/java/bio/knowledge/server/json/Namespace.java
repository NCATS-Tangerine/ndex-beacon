package bio.knowledge.server.json;

import java.util.HashMap;

public class Namespace extends HashMap<String, String> {

	/**
	 * HashMap to store namespace keys and values, found under a Ndex network's @context tag
	 * e.g. { "pubmed": "http://identifiers.org/pubmed/", "ensembl": "http://identifiers.org/ensembl/" }
	 */
	private static final long serialVersionUID = -385630247399873124L;

}
