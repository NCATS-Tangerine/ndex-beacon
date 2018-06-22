package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.server.model.BeaconKnowledgeMapObject;
import bio.knowledge.server.model.BeaconKnowledgeMapPredicate;
import bio.knowledge.server.model.BeaconKnowledgeMapStatement;
import bio.knowledge.server.model.BeaconKnowledgeMapSubject;
import bio.knowledge.server.model.BeaconStatement;

@Component
public class KnowledgeMapRegistry extends HashMap<StatementTriple, BeaconKnowledgeMapStatement>{

	private static final long serialVersionUID = 7950158694784178467L;

	private static Logger _logger = LoggerFactory.getLogger(KnowledgeMapRegistry.class);

	@Autowired PredicatesRegistry predicateRegistry;
	Map<String, List<String>> prefixes = new HashMap<>();
	
	public void indexKnowledgeMapEntry(BeaconStatement statement) {
		StatementTriple triple = getTriple(statement);
		String subj = triple.getSubject();
		String obj = triple.getObject();
		
		String newObjPrefix = addToPrefixes(obj, statement.getObject().getId());
		String newSubjPrefix = addToPrefixes(subj, statement.getSubject().getId()); 
		
		if (!containsKey(triple)) {
			String pred = triple.getPredicate();
			_logger.info("New subj-pred-obj encountered - "
					+ "Subject: " + subj + "; "
					+ "Relation: " + pred + "; "
					+ "Object: " + obj);
			
			BeaconKnowledgeMapStatement s = new BeaconKnowledgeMapStatement();
			s.setObject(createObject(obj));
			s.setSubject(createSubject(subj));
			s.setPredicate(createPredicate(pred));
			s.setDescription(subj + " - " + pred.replaceFirst(Translator.NDEX_NS, "") + " - " + obj);
			s.setFrequency(1);
			put(triple, s);
		} else {
			BeaconKnowledgeMapStatement s = get(triple);
			s.setFrequency(s.getFrequency()+1);
			updateObjectPrefix(s, newObjPrefix);
			updateSubjectPrefix(s, newSubjPrefix);
			put(triple, s);
		}
	}

	private void updateObjectPrefix(BeaconKnowledgeMapStatement s, String prefix) {
		if (prefix != null) {
			s.getObject().addPrefixesItem(prefix);
		}
	}

	private void updateSubjectPrefix(BeaconKnowledgeMapStatement s, String prefix) {
		if (prefix != null) {
			s.getSubject().addPrefixesItem(prefix);
		}
	}

	/**
	 * Adds CURIE like identifiers to possible prefix list if doesn't
	 * already exist under the given category
	 * @param category
	 * @param nodeId
	 * @return new prefix if any was added, or null if none was added
	 */
	private String addToPrefixes(String category, String nodeId) {
		
		if (nodeId.startsWith(Translator.NDEX_NS)) return null;
		
		String prefix = nodeId.split(":")[0];
		List<String> set; 
		if (prefixes.containsKey(category)) {
			set = prefixes.get(category);
			if (!(set.contains(prefix))) {
				set.add(prefix);
				prefixes.put(category, set);
				return prefix;
			} else {
				return null;
			}
		} else {
			set = new ArrayList<>();
			set.add(prefix);
			prefixes.put(category, set);
			return prefix;
		}
		
	}

	private BeaconKnowledgeMapPredicate createPredicate(String predicateRelation) {
		BeaconKnowledgeMapPredicate pred = new BeaconKnowledgeMapPredicate();
		
		if (predicateRegistry.containsKey(predicateRelation)) {
			String edgeLabel = predicateRegistry.get(predicateRelation).getEdgeLabel();
			pred.setEdgeLabel(edgeLabel);
		}
		
		pred.setRelation(predicateRelation);
		
		return pred;
	}

	private BeaconKnowledgeMapSubject createSubject(String category) {
		BeaconKnowledgeMapSubject subj = new BeaconKnowledgeMapSubject();
		subj.setCategory(category);
		if (prefixes.containsKey(category)) {
			subj.setPrefixes(prefixes.get(category));
		}
		return subj;
	}

	private BeaconKnowledgeMapObject createObject(String category) {
		BeaconKnowledgeMapObject obj = new BeaconKnowledgeMapObject();
		obj.setCategory(category);
		if (prefixes.containsKey(category)) {
			obj.setPrefixes(prefixes.get(category));
		}
		return obj;
	}

	private StatementTriple getTriple(BeaconStatement statement) {
		String subj = statement.getSubject().getCategory();
		String obj = statement.getObject().getCategory();
		String pred = statement.getPredicate().getRelation();
		return new StatementTriple(subj, pred, obj);
		
	}
}
