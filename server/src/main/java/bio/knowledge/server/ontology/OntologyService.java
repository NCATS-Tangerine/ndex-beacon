package bio.knowledge.server.ontology;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.ontology.BeaconBiolinkModel;
import bio.knowledge.ontology.BiolinkClass;
import bio.knowledge.ontology.BiolinkSlot;
import bio.knowledge.ontology.mapping.InheritanceLookup;
import bio.knowledge.ontology.mapping.ModelLookup;

@Service
public class OntologyService {
	
	private final static String UMLSSG_PREFIX = "UMLSSG:";
	
	BeaconBiolinkModel biolinkModel;
	ModelLookup<BiolinkClass> classLookup;
	ModelLookup<BiolinkSlot> slotLookup;
	InheritanceLookup<BiolinkClass> classInheritance;
	InheritanceLookup<BiolinkSlot> slotInheritance;
	
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
	}
	
	public String umlsToBiolinkCategory(String umlsCategory) {
		if (!umlsCategory.startsWith(UMLSSG_PREFIX)) {
			umlsCategory = UMLSSG_PREFIX + umlsCategory;
		}
		
		BiolinkClass biolinkClass = classLookup.lookup(umlsCategory);
		
		if (biolinkClass == null) {
			biolinkClass = classLookup.lookup(UMLSSG_PREFIX + "OBJC");
		}
		
		return biolinkClass.getName();
	}
	
	public String umlsToBiolinkPredicate(String predicateLabel) {
		return null;
	}

}
