package bio.knowledge.server.ontology;

import java.util.HashSet;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import bio.knowledge.ontology.BeaconBiolinkModel;
import bio.knowledge.ontology.BiolinkClass;
import bio.knowledge.ontology.BiolinkSlot;
import bio.knowledge.ontology.mapping.InheritanceLookup;
import bio.knowledge.ontology.mapping.ModelLookup;

@Service
public class OntologyService {
	
	private final static String UMLSSG_PREFIX = "UMLSSG:";
	private final static String DEFAULT_SLOT_NAME= "related to";
	private final static String DEFAULT_CLASS = (UMLSSG_PREFIX + "OBJC");
	
	private static Logger _logger = LoggerFactory.getLogger(OntologyService.class);
	
	BeaconBiolinkModel biolinkModel;
	ModelLookup<BiolinkClass> classLookup;
	ModelLookup<BiolinkSlot> slotLookup;
	InheritanceLookup<BiolinkClass> classInheritance;
	InheritanceLookup<BiolinkSlot> slotInheritance;
	
	HashSet<String> mappedBiolinkCategories;
	HashSet<String> unmappedBiolinkCategories;
	
	
	@PostConstruct
	private void init() {
		Optional<BeaconBiolinkModel> optional = BeaconBiolinkModel.load();
		
		if (optional.isPresent()) {
			biolinkModel = optional.get();
		} else {
			throw new RuntimeException("Biolink model did not load");
		}
		
		slotInheritance = new InheritanceLookup<BiolinkSlot>(biolinkModel.getSlots());
		classInheritance = new InheritanceLookup<BiolinkClass>(biolinkModel.getClasses());
		
		slotLookup = new ModelLookup<BiolinkSlot>(biolinkModel.getSlots(), slotInheritance);
		classLookup = new ModelLookup<BiolinkClass>(biolinkModel.getClasses(), classInheritance);
		
		mappedBiolinkCategories = new HashSet<String>();
		unmappedBiolinkCategories = new HashSet<String>();
	}
	
	public String umlsToBiolinkCategory(String umlsCategory) {
		if (!umlsCategory.startsWith(UMLSSG_PREFIX)) {
			umlsCategory = UMLSSG_PREFIX + umlsCategory;
		}
		
		BiolinkClass biolinkClass = classLookup.lookup(umlsCategory);
		
		if (biolinkClass == null) {
			biolinkClass = classLookup.lookup(DEFAULT_CLASS);
		}
		
		return biolinkClass.getName();
	}
	
	public String umlsToBiolinkPredicate(String predicateLabel) {
		return null;
	}

	/**
	 * Simple heuristic for creating Biolink edge label from simple string
	 * @param pName
	 * @return
	 */
	public String predToBiolinkEdgeLabel(String pName) {
		
		String edgeLabel;
		
		if (exactlyMatchesBiolink(pName)) {
			mappedBiolinkCategories.add(pName);
			edgeLabel = convertToSnakeCase(pName);
		} else {
			if (unmappedBiolinkCategories.add(pName)) {
				_logger.info("new unknown category added: " + pName);
				_logger.info("Known Predicates: " + String.join(", ", mappedBiolinkCategories));
				_logger.info("Unknown Predicates: " + String.join(", ", unmappedBiolinkCategories));
			}
			
			edgeLabel = convertToSnakeCase(DEFAULT_SLOT_NAME);
		}
		
		return edgeLabel;
		
	}
	
	private Boolean exactlyMatchesBiolink(String pName) {
		return (!(slotLookup.reverseLookup(pName).isEmpty()));
	}

	private String convertToSnakeCase(String name) {
		return name.trim().replaceAll("\\s", "_");
	}

}
