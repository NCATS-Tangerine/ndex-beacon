package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * BeaconStatementPredicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-17T12:46:02.920-07:00")

public class BeaconStatementPredicate   {
  @JsonProperty("edge_label")
  private String edgeLabel = null;

  @JsonProperty("relation")
  private String relation = null;

  public BeaconStatementPredicate edgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
    return this;
  }

   /**
   * minimal Biolink model predicate term
   * @return edgeLabel
  **/
  @ApiModelProperty(value = "minimal Biolink model predicate term")
  public String getEdgeLabel() {
    return edgeLabel;
  }

  public void setEdgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
  }

  public BeaconStatementPredicate relation(String relation) {
    this.relation = relation;
    return this;
  }

   /**
   * SHOULD be from maximal Biolink model predicate list, but beacon-specific extensions allowed. Preferred format is a CURIE, where one exists, but strings/labels acceptable. May be empty.
   * @return relation
  **/
  @ApiModelProperty(value = "SHOULD be from maximal Biolink model predicate list, but beacon-specific extensions allowed. Preferred format is a CURIE, where one exists, but strings/labels acceptable. May be empty.")
  public String getRelation() {
    return relation;
  }

  public void setRelation(String relation) {
    this.relation = relation;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconStatementPredicate beaconStatementPredicate = (BeaconStatementPredicate) o;
    return Objects.equals(this.edgeLabel, beaconStatementPredicate.edgeLabel) &&
        Objects.equals(this.relation, beaconStatementPredicate.relation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(edgeLabel, relation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconStatementPredicate {\n");
    
    sb.append("    edgeLabel: ").append(toIndentedString(edgeLabel)).append("\n");
    sb.append("    relation: ").append(toIndentedString(relation)).append("\n");
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

