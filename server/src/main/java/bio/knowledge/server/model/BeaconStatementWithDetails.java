package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.BeaconStatementAnnotation;
import bio.knowledge.server.model.BeaconStatementCitation;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
/**
 * BeaconStatementWithDetails
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-12T19:09:25.899Z")

public class BeaconStatementWithDetails   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("is_defined_by")
  private String isDefinedBy = null;

  @JsonProperty("provided_by")
  private String providedBy = null;

  @JsonProperty("qualifiers")
  private List<String> qualifiers = new ArrayList<String>();

  @JsonProperty("annotation")
  private List<BeaconStatementAnnotation> annotation = new ArrayList<BeaconStatementAnnotation>();

  @JsonProperty("evidence")
  private List<BeaconStatementCitation> evidence = new ArrayList<BeaconStatementCitation>();

  public BeaconStatementWithDetails id(String id) {
    this.id = id;
    return this;
  }

   /**
   * Statement identifier of the statement made in an edge (echoed back) 
   * @return id
  **/
  @ApiModelProperty(value = "Statement identifier of the statement made in an edge (echoed back) ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BeaconStatementWithDetails isDefinedBy(String isDefinedBy) {
    this.isDefinedBy = isDefinedBy;
    return this;
  }

   /**
   * A CURIE/URI for the translator group that wrapped this knowledge source ('beacon') that publishes the statement made in an edge. 
   * @return isDefinedBy
  **/
  @ApiModelProperty(value = "A CURIE/URI for the translator group that wrapped this knowledge source ('beacon') that publishes the statement made in an edge. ")
  public String getIsDefinedBy() {
    return isDefinedBy;
  }

  public void setIsDefinedBy(String isDefinedBy) {
    this.isDefinedBy = isDefinedBy;
  }

  public BeaconStatementWithDetails providedBy(String providedBy) {
    this.providedBy = providedBy;
    return this;
  }

   /**
   * A CURIE prefix, e.g. Pharos, MGI, Monarch. The group that  curated/asserted the statement made in an edge. 
   * @return providedBy
  **/
  @ApiModelProperty(value = "A CURIE prefix, e.g. Pharos, MGI, Monarch. The group that  curated/asserted the statement made in an edge. ")
  public String getProvidedBy() {
    return providedBy;
  }

  public void setProvidedBy(String providedBy) {
    this.providedBy = providedBy;
  }

  public BeaconStatementWithDetails qualifiers(List<String> qualifiers) {
    this.qualifiers = qualifiers;
    return this;
  }

  public BeaconStatementWithDetails addQualifiersItem(String qualifiersItem) {
    this.qualifiers.add(qualifiersItem);
    return this;
  }

   /**
   * (Optional) terms representing qualifiers that modify or qualify the meaning of the statement made in an edge. 
   * @return qualifiers
  **/
  @ApiModelProperty(value = "(Optional) terms representing qualifiers that modify or qualify the meaning of the statement made in an edge. ")
  public List<String> getQualifiers() {
    return qualifiers;
  }

  public void setQualifiers(List<String> qualifiers) {
    this.qualifiers = qualifiers;
  }

  public BeaconStatementWithDetails annotation(List<BeaconStatementAnnotation> annotation) {
    this.annotation = annotation;
    return this;
  }

  public BeaconStatementWithDetails addAnnotationItem(BeaconStatementAnnotation annotationItem) {
    this.annotation.add(annotationItem);
    return this;
  }

   /**
   * Extra edge properties, generally compliant with Translator Knowledge Graph Standard Specification 
   * @return annotation
  **/
  @ApiModelProperty(value = "Extra edge properties, generally compliant with Translator Knowledge Graph Standard Specification ")
  public List<BeaconStatementAnnotation> getAnnotation() {
    return annotation;
  }

  public void setAnnotation(List<BeaconStatementAnnotation> annotation) {
    this.annotation = annotation;
  }

  public BeaconStatementWithDetails evidence(List<BeaconStatementCitation> evidence) {
    this.evidence = evidence;
    return this;
  }

  public BeaconStatementWithDetails addEvidenceItem(BeaconStatementCitation evidenceItem) {
    this.evidence.add(evidenceItem);
    return this;
  }

   /**
   * Get evidence
   * @return evidence
  **/
  @ApiModelProperty(value = "")
  public List<BeaconStatementCitation> getEvidence() {
    return evidence;
  }

  public void setEvidence(List<BeaconStatementCitation> evidence) {
    this.evidence = evidence;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconStatementWithDetails beaconStatementWithDetails = (BeaconStatementWithDetails) o;
    return Objects.equals(this.id, beaconStatementWithDetails.id) &&
        Objects.equals(this.isDefinedBy, beaconStatementWithDetails.isDefinedBy) &&
        Objects.equals(this.providedBy, beaconStatementWithDetails.providedBy) &&
        Objects.equals(this.qualifiers, beaconStatementWithDetails.qualifiers) &&
        Objects.equals(this.annotation, beaconStatementWithDetails.annotation) &&
        Objects.equals(this.evidence, beaconStatementWithDetails.evidence);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, isDefinedBy, providedBy, qualifiers, annotation, evidence);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconStatementWithDetails {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    isDefinedBy: ").append(toIndentedString(isDefinedBy)).append("\n");
    sb.append("    providedBy: ").append(toIndentedString(providedBy)).append("\n");
    sb.append("    qualifiers: ").append(toIndentedString(qualifiers)).append("\n");
    sb.append("    annotation: ").append(toIndentedString(annotation)).append("\n");
    sb.append("    evidence: ").append(toIndentedString(evidence)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

