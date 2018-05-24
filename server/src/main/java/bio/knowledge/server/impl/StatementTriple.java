package bio.knowledge.server.impl;

import org.apache.commons.lang3.tuple.Triple;

public class StatementTriple extends Triple<String, String, String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1103382397340654177L;
	
	String subject;
	String predicate;
	String object;

	public StatementTriple(String subj, String pred, String obj) {
		this.subject = subj;
		this.predicate = pred;
		this.object = obj;
	}
	
	/**
	 * Same as getLeft()
	 * @return the subject in a subject-predicate-object triple
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Same as getMiddle()
	 * @return the predicate in a subject-predicate-object triple
	 */
	public String getPredicate() {
		return predicate;
	}

	/**
	 * Same as getRight()
	 * @return the object in a subject-predicate-object triple
	 */
	public String getObject() {
		return object;
	}
	

	@Override
	public String getLeft() {
		return subject;
	}

	@Override
	public String getMiddle() {
		return predicate;
	}

	@Override
	public String getRight() {
		return object;
	}
	
	
	
}
