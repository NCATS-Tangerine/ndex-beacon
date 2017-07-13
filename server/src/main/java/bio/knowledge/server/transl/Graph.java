package bio.knowledge.server.transl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import bio.knowledge.server.impl.Util;
import bio.knowledge.server.json.Aspect;
import bio.knowledge.server.json.Citation;
import bio.knowledge.server.json.Network;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.EdgeCitation;
import bio.knowledge.server.json.EdgeSupport;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.json.Support;

public class Graph {
	
	// todo: use different types for graph parts
	
	// todo: have graph do network annotation or creating nodeIds
	
	private Map<Long, Node> nodes = new HashMap<>();
	private Map<Long, Edge> edges = new HashMap<>();
	private Map<Long, Citation> citations = new HashMap<>();
	private Map<Long, Support> supports = new HashMap<>();

	public Graph(Network network) {
	
		for (Aspect a : network.getData()) {
			addAll(a.getNodes());
			addAll(a.getEdges());
			addAll(a.getCitations());
			addAll(a.getEdgeCitations());
			addAll(a.getSupports());
			addAll(a.getEdgeSupports());
		}
		
	}
	
	private void addAll(Node[] n) {
		if (n.length == 0) return;
		for (Node node : n)
			nodes.put(node.getId(), node);
	}
	
	private void addAll(Edge[] e) {
		if (e.length == 0) return;
		for (Edge edge : e)
			add(edge);
	}
	
	private void add(Edge edge) {
		
		Long source = edge.getSource();
		Long target = edge.getTarget();
		
		Node subject = nodes.get(source);
		Node object = nodes.get(target);
		
		edge.setSubject(subject);
		edge.setObject(object);
		
		edges.put(edge.getId(), edge);		
	}
	
	private void addAll(Citation[] c) {
		if (c.length == 0) return;
		for (Citation citation : c)
			citations.put(citation.getId(), citation);		
	}
	
	private void addAll(EdgeCitation[] c) {
		if (c.length == 0) return;
		for (EdgeCitation edgeCitation : c) {
			add(edgeCitation);
		}
	}
	
	private void add(EdgeCitation edgeCitation) {
		
		Long[] edgeIds = edgeCitation.getId(); // todo: rename meth to pl
		Long[] citationIds = edgeCitation.getCitations();
		
		List<Edge> relatedEdges = Util.map(edges::get, edgeIds);
		List<Citation> relatedCitations = Util.map(citations::get, citationIds);
		
		relatedEdges.forEach(e -> e.setCitations(relatedCitations));
	}
	
	private void addAll(Support[] s) {
		if (s.length == 0) return;
		for (Support support : s)
			add(support);
		

	}
	
	private void add(Support support) {
		
		Long citationId = support.getCitationId();
		Citation citation = citations.getOrDefault(citationId, null);
		support.setCitation(citation);
		
		supports.put(support.getId(), support);
	}
	
	private void addAll(EdgeSupport[] s) {
		if (s.length == 0) return;
		for (EdgeSupport edgeSupport : s) {
			add(edgeSupport);
		}
	}
	
	private void add(EdgeSupport edgeSupport) {
		
		Long[] edgeIds = edgeSupport.getId();
		Long[] supportIds = edgeSupport.getSupports();
		
		List<Edge> relatedEdges = Util.map(edges::get, edgeIds);
		List<Support> relatedSupports = Util.map(supports::get, supportIds);
		
		relatedEdges.forEach(e -> e.setSupports(relatedSupports));

		Predicate<Support> hasCitation = s -> s.getCitation() != null;
		Predicate<Edge> hasImpliedCitation = e -> e.getCitations().size() == 1 && Util.filter(hasCitation, e.getSupports()).isEmpty(); // todo: ...
		
		Consumer<Edge> inferCitations = e -> {
			if (hasImpliedCitation.test(e))
				e.getSupports().forEach(s -> s.setCitation(e.getCitations().get(0)));
		};
				
		Consumer<Support> addSupport = s -> {
			if (hasCitation.test(s))
				s.getCitation().addSupport(s);
		};
				
		relatedEdges.forEach(inferCitations);
		relatedSupports.forEach(addSupport);
	}
	
	public Collection<Node> getNodes() {
		return nodes.values();
	}
	
	public Collection<Edge> getEdges() {
		return edges.values();
	}
	
}
