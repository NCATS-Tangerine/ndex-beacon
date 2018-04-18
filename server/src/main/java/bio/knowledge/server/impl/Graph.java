package bio.knowledge.server.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import bio.knowledge.server.json.Aspect;
import bio.knowledge.server.json.Attribute;
import bio.knowledge.server.json.Citation;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.EdgeCitation;
import bio.knowledge.server.json.EdgeSupport;
import bio.knowledge.server.json.Network;
import bio.knowledge.server.json.NetworkId;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.json.Support;

/**
 * Turns ID-based cross-references into traversable Java references.
 * 
 * @author Meera Godden
 *
 */
public class Graph {
		
	private Map<Long, Node> nodes = new HashMap<>();
	private Map<Long, Edge> edges = new HashMap<>();
	private Map<Long, Citation> citations = new HashMap<>();
	private Map<Long, Support> supports = new HashMap<>();

	public Graph(Network network) {

		// Add all nodes first, so that edges have something to connect to
		for (Aspect a : network.getData()) {
			if(a==null) continue;

			addNodes(a.getNodes());
		}
	
		for (Aspect a : network.getData()) {
			
			if(a==null) continue;
			
			addCitations(a.getCitations());
			
			annotateNodesWithNetworkId(a.getNdexStatus());
			connectAttributesToNodes(a.getNodeAttributes());
			connectAttributesToEdges(a.getEdgeAttributes());

			connectNodesAndEdges(a.getEdges());
			connectEdgesToCitations(a.getEdgeCitations());
			connectSupportsToCitations(a.getSupports());
			interconnectEdgesSupportsAndCitations(a.getEdgeSupports());
		}
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
	
	private void annotateNodesWithNetworkId(NetworkId i[]) {
		
		if ( i==null || i.length == 0 ) return;
		
		for (NetworkId id : i) {
			String networkId = id.getExternalId();
			nodes.values().forEach(n -> n.setNetworkId(networkId));
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
		node.addAttribute(attribute);
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
		edge.addAttribute(attribute);
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
		
		if(object!=null)
			object.addEdge(edge);
		
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
		
		List<Edge> relatedEdges = Util.map(edges::get, edgeIds);
		List<Citation> relatedCitations = Util.map(citations::get, citationIds);
		
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
		
		List<Edge> relatedEdges = Util.map(edges::get, edgeIds);
		List<Support> relatedSupports = Util.map(supports::get, supportIds);
		
		relatedEdges.forEach(e -> e.setSupports(relatedSupports));
	}
	
	private void inferCitationForSupports(EdgeSupport edgeSupport) {

		if(edgeSupport==null) return;
		
		Long[] edgeIds = edgeSupport.getId();
		
		List<Edge> relatedEdges = Util.map(edges::get, edgeIds);

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
		List<Support> relatedSupports = Util.map(supports::get, supportIds);
		
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
