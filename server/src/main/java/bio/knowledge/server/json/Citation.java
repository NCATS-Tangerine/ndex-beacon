package bio.knowledge.server.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import bio.knowledge.server.impl.Util;

/**
 * Contains citation data. Optionally contains citation text.
 * 
 * @author Meera Godden
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Citation extends IdentifiedObject {
	
	private String citationId;
	private String name;
	private String evidenceType;
	
	private List<Support> supports = new ArrayList<>();

	@JsonProperty("dc:identifier")
	public String getCitationId() {
		return citationId;
	}

	@JsonProperty("dc:identifier")
	public void setCitationId(String citationId) {
		this.citationId = citationId;
	}

	@JsonProperty("dc:title")
	public String getName() {
		return name;
	}

	@JsonProperty("dc:title")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("dc:type")
	public String getEvidenceType() {
		return evidenceType;
	}

	@JsonProperty("dc:type")
	public void setEvidenceType(String type) {
		this.evidenceType = type;
	}

	public List<Support> getSupports() {
		return supports;
	}
	
	public void addSupport(Support support) {
		supports.add(support);
	}
	
	public String getFullText() {
		List<String> texts = Util.map(Support::getText, supports);
		String text = String.join(" ", texts);
		return text;
	}

}
