package bio.knowledge.server.json;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import bio.knowledge.server.impl.Util;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Network {

	// todo: if is not aspect list (eg error, not found) handle?
	
	private Aspect[] data = new Aspect[0];
	

	private void annotateNodes() {
		
		String networkId = Util.flatmap(Aspect::getNdexStatus, data).get(0).getExternalId();
		List<Node> nodes = Util.flatmap(Aspect::getNodes, data);
		
		Consumer<Node> setNetworkId = n -> n.setNetworkId(networkId);
		nodes.forEach(setNetworkId);
	}

	public Aspect[] getData() {
		return data;
	}

	public void setData(Aspect[] data) {
		System.out.println("123 got data: " + data.length);
		for (Aspect a : data)
			System.out.print(a + ", ");
		System.out.println();
		this.data = data;
		annotateNodes();
	}
	
	// todo: remove
//	public static <T> List<T> getAspects(Class<T> type, AspectList aspectList) {
//		
//		Aspect[] data = aspectList.getData();
//		List<T> matches = new ArrayList<>();
//		
//		for (Aspect aspect : data)
//			if (type.isInstance(aspect))
//				matches.add((T) aspect);
//		
//		return matches;
//	}
	
}
