package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bio.knowledge.server.json.Aspect;
import bio.knowledge.server.json.Attribute;
import bio.knowledge.server.json.Citation;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.EdgeCitation;
import bio.knowledge.server.json.EdgeSupport;
import bio.knowledge.server.json.Namespace;
import bio.knowledge.server.json.Network;
import bio.knowledge.server.json.NetworkProperty;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.json.Support;

/**
 * Turns ID-based cross-references into traversable Java references.
 * 
 * @author Meera Godden
 *
 */
public class Graph implements CachedEntity {
	
	private static Logger _logger = LoggerFactory.getLogger(Graph.class);
		
	private Map<Long, Node> nodes = new HashMap<>();
	private Map<Long, Edge> edges = new HashMap<>();
	private Map<Long, Citation> citations = new HashMap<>();
	private Map<Long, Support> supports = new HashMap<>();
	private Network network;

	public Graph(Network network) {

		this.network = network;
		// Add all nodes first, so that edges have something to connect to
		for (Aspect a : network.getData()) {
			if(a==null) continue;
			
			addNodes(a.getNodes());
			addCitations(a.getCitations());
			addSupports(a.getSupports());
		}
	
		for (Aspect a : network.getData()) {
			
			if(a==null) continue;
			addCitations(a.getCitations());
			connectNodesAndEdges(a.getEdges());
			annotateNodesWithNetworkId(network.getNetworkId());
			addUriToNodes(a.getNamespaces());
			
			connectAttributesToNodes(a.getNodeAttributes());
			connectAttributesToEdges(a.getEdgeAttributes());
			addNetworkReferenceToEdges(network.getProperties());

			connectSupportsToCitations(a.getSupports());
			connectEdgesToCitations(a.getEdgeCitations());
			interconnectEdgesSupportsAndCitations(a.getEdgeSupports());
		}
		
	}

	/**
	 * Adds uri information to each concept node, if the node contains the represents field and has a CURIE-like structure,
	 * by connecting the appropriate namespace information to the node
	 * @param ns
	 */
	private void addUriToNodes(Namespace[] ns) {
		if (ns==null || ns.length == 0) return;
		
		HashMap<String, String> namespaces = ns[0];
		
		for (Node node : nodes.values()) {
			String represents = node.getRepresents();
			if (represents != null) {
				if (Translator.hasCurieStructure(represents)) {
					String[] split = represents.split(":");
					String namespace = split[0];
					String id = split[1];
					if (namespaces.containsKey(namespace)) {
						String partialUri = namespaces.get(namespace);
						node.setUri(partialUri + id);
					} else {
						_logger.warn("Building graph - namespace information for " + represents + " doesn't exist; "
								+ "network: " + network.getNetworkId());
					}
				} else {
					Attribute a = new Attribute();
					a.setName("represents");
					a.setValues(Util.list(represents));
					node.addAttribute(a);
				}
			}
		}
		
	}

	private void addNetworkReferenceToEdges(NetworkProperty[] properties) {
		for (NetworkProperty property : properties) {
			if (property.getPredicateString().equalsIgnoreCase("reference")) {
				for (Edge edge : edges.values()) {
					edge.setNetworkReference(property);
				}
			}
		}
	}

	private void logWarningMessage(String message) {
		_logger.warn("Building graph - " + message);
	}


	private boolean hasCitation(Support support) {
		
		if(support==null) return false;
		
		return support.getCitation() != null;
	}
	
	private void addNodes(Node[] n) {
		
		if (n==null || n.length == 0) return;
		
		for (Node node : n) {
			nodes.put(node.getId(), node);
		}
	}
	
	private void addCitations(Citation[] c) {
		
		if ( c==null || c.length == 0) return;
		
		for (Citation citation : c)
			citations.put(citation.getId(), citation);		
	}
	

	private void addSupports(Support[] s) {
		if ( s==null || s.length == 0) return;
		
		for (Support support : s)
			supports.put(support.getId(), support);	
	}
	
	private void annotateNodesWithNetworkId(String i) {
	
		for (Node node : nodes.values()) {
			node.setNetworkId(i);
		}
	}
	

	private void connectAttributesToNodes(Attribute[] a) {
		
		if ( a==null || a.length == 0 ) return;
		
		for (Attribute attribute : a)
			connectAttributeToNode(attribute);
	}
	
	private void connectAttributeToNode(Attribute attribute) {
		
		if(attribute == null || nodes==null) return;
		
		Long id = attribute.getId();
		Node node = nodes.get(id);
		
		if (node == null) {
			logWarningMessage("AttributeToNode: node does not exist: " + attribute.getId());
		} else {
			node.addAttribute(attribute);
		}
		
	}
	
	private void connectAttributesToEdges(Attribute[] a) {
		
		if (a.length == 0) return;
		
		for (Attribute attribute : a)
			connectAttributeToEdge(attribute);
	}
	
	private void connectAttributeToEdge(Attribute attribute) {
		
		if(attribute==null || edges==null) return;
		
		Long id = attribute.getId();
		Edge edge = edges.get(id);
		
		if (edge == null) {
			logWarningMessage("AttributeToEdge: edge does not exist: " + attribute.getId());
		} else {
			edge.addAttribute(attribute);
		}
	}

	private void connectNodesAndEdges(Edge[] e) {
		if (e.length == 0) return;
		for (Edge edge : e)
			connectEdgeToSourceAndTarget(edge);
	}
	
	private void connectEdgeToSourceAndTarget(Edge edge) {
		
		if(edge==null || nodes == null) return;
		
		Long source = edge.getSource();
		Long target = edge.getTarget();
		
		Node subject = nodes.get(source);
		Node object = nodes.get(target);
		
		edge.setSubject(subject);
		edge.setObject(object);
		
		if(subject!=null)
			subject.addEdge(edge);
		else
			logWarningMessage("EdgeToSource: node does not exist: " + source);
		
		if(object!=null)
			object.addEdge(edge);
		else
			logWarningMessage("EdgeToTarget: node does not exist: " + target);
		
		
		edges.put(edge.getId(), edge);		
	}

	
	private void connectEdgesToCitations(EdgeCitation[] c) {
		
		if (c==null || c.length == 0) return;
		
		for (EdgeCitation edgeCitation : c) {
			connectEdgeToCitation(edgeCitation);
		}
	}
	
	private void connectEdgeToCitation(EdgeCitation edgeCitation) {
		
		if(edgeCitation==null) return;
		
		Long[] edgeIds = edgeCitation.getId();
		Long[] citationIds = edgeCitation.getCitations();
		
		List<Edge> relatedEdges = findRelatedIfExists(edgeIds, edges);
		List<Citation> relatedCitations = findRelatedIfExists(citationIds, citations);
		
		relatedEdges.forEach(e -> e.setCitations(relatedCitations));
	}
	
	private void connectSupportsToCitations(Support[] s) {
		
		if(s==null || s.length == 0) return;
		
		for (Support support : s)
			connectSupportToCitation(support);
	}
	
	private void connectSupportToCitation(Support support) {
		
		if(support==null) return;
		
		Long citationId = support.getCitationId();
		Citation citation = citations.getOrDefault(citationId, null);
		support.setCitation(citation);
		
		supports.put(support.getId(), support);
	}
	
	
	private void interconnectEdgesSupportsAndCitations(EdgeSupport[] s) {
		
		if (s==null || s.length == 0) return;
		
		for (EdgeSupport edgeSupport : s) {
			connectEdgeToSupports(edgeSupport);
			inferCitationForSupports(edgeSupport);
			connectCitationsToSupports(edgeSupport);
		}
	}

	private void connectEdgeToSupports(EdgeSupport edgeSupport) {
		
		if(edgeSupport==null) return;
		
		Long[] edgeIds = edgeSupport.getId();
		Long[] supportIds = edgeSupport.getSupports();
		
		List<Edge> relatedEdges = findRelatedIfExists(edgeIds, edges);
		List<Support> relatedSupports = findRelatedIfExists(supportIds, supports);
		
		relatedEdges.forEach(e -> e.setSupports(relatedSupports));
	}
	
	
	/**
	 * Produces list of related items from map using ids
	 * Logs warning message if id does not already exist in map
	 * @param ids 
	 * @param map - likely will be one of nodes, edges, citations, or supports
	 * @return list of related items of type T
	 */
	private <T> List<T> findRelatedIfExists(Long[] ids, Map<Long, T> map) {
		List<T> related = new ArrayList<>();
		
		for (Long id : ids) {
			T item = map.get(id);
			if (item == null) 
				logWarningMessage("trying to connect to graph, "+ "but id does not exist: " + id + " for network: " + network.getNetworkId());
			else 
				related.add(item); 
		}
		
		return related;
	}
	


	private void inferCitationForSupports(EdgeSupport edgeSupport) {

		if(edgeSupport==null) return;
		
		Long[] edgeIds = edgeSupport.getId();

		List<Edge> relatedEdges = findRelatedIfExists(edgeIds, edges);

		Predicate<Edge> hasImpliedCitation = e -> e.getCitations().size() == 1 && Util.filter(this::hasCitation, e.getSupports()).isEmpty();
		
		Consumer<Edge> inferCitations = e -> {
			if (hasImpliedCitation.test(e))
				e.getSupports().forEach(s -> s.setCitation(e.getCitations().get(0)));
		};
				
		relatedEdges.forEach(inferCitations);
	}
	
	public void connectCitationsToSupports(EdgeSupport edgeSupport) {

		if(edgeSupport==null) return;
		
		Long[] supportIds = edgeSupport.getSupports();
		List<Support> relatedSupports = findRelatedIfExists(supportIds, supports);
//		List<Support> relatedSupports = Util.map(supports::get, supportIds);
		
		Consumer<Support> addSupport = s -> {
			if (hasCitation(s))
				s.getCitation().addSupport(s);
		};
		
		relatedSupports.forEach(addSupport);
	}
	
	
	public Collection<Node> getNodes() {
		
		if(nodes==null) return null;
			
		return nodes.values();
	}
	
	public Collection<Edge> getEdges() {
		
		if(edges==null) return null;
		
		return edges.values();
	}
	
}
