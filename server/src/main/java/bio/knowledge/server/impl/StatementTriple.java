package bio.knowledge.server.impl;


import java.util.Objects;

/**
 * Class to store subject, predicate (relation), object relationships
 * 
 * @author Imelda
 */

public class StatementTriple {
	
	
	private final String subject;
	private final String predicate;
	private final String object;

	
	public StatementTriple(String subj, String pred, String obj) {
		this.subject = subj;
		this.predicate = pred;
		this.object = obj;
	}
	
	/**
	 * @return the subject in a subject-predicate-object triple
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return the predicate in a subject-predicate-object triple
	 */
	public String getPredicate() {
		return predicate;
	}

	/**
	 * @return the object in a subject-predicate-object triple
	 */
	public String getObject() {
		return object;
	}
	
	public String toString() {
		return "(" + subject + ", " + predicate + ", " + object + ")";
		
	}
	
	@Override 
	public int hashCode() {
	     return Objects.hash(subject, predicate, object);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof StatementTriple) {
			StatementTriple triple = (StatementTriple) o;
			return triple.getSubject().equals(subject) && triple.getPredicate().equals(predicate) && triple.getObject().equals(object);
		}
		return false;
	}
	
	
}
