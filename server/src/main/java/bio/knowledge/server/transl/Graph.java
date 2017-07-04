package bio.knowledge.server.transl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bio.knowledge.server.json.Aspect;
import bio.knowledge.server.json.Network;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.Node;

public class Graph {
	
	// todo: have graph do network annotation or creating nodeIds
	
	private Map<Long, Node> nodes = new HashMap<>();
	private Map<Long, Edge> edges = new HashMap<>();
	
	public Graph(Network network) {
	
		for (Aspect a : network.getData()) {
			add(a.getNodes());
			add(a.getEdges());
		}
		
	}
	
	private void add(Node[] n) {
		if (n.length == 0) return;
		Node node = n[0];
		nodes.put(node.getId(), node);
	}
	
	private void add(Edge[] e) {
		if (e.length == 0) return;
		
		Edge edge = e[0];
		Long source = edge.getSource();
		Long target = edge.getTarget();
		
		Node subject = nodes.get(source);
		Node object = nodes.get(target);
		
		edge.setSubject(subject);
		edge.setObject(object);
		
		edges.put(edge.getId(), edge);
	}
	
	public Collection<Edge> getEdges() {
		return edges.values();
	}

}
