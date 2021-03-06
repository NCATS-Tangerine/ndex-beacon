package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * BeaconKnowledgeMapPredicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-27T18:01:11.438Z")

public class BeaconKnowledgeMapPredicate   {
  @JsonProperty("edge_label")
  private String edgeLabel = null;

  @JsonProperty("relation")
  private String relation = null;

  @JsonProperty("negated")
  private Boolean negated = null;

  public BeaconKnowledgeMapPredicate edgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
    return this;
  }

   /**
   * Human readable name of the 'minimal' standard Biolink Model predicate relationship name.   See [Biolink Model](https://biolink.github.io/biolink-model)  for the full list of terms. 
   * @return edgeLabel
  **/
  @ApiModelProperty(value = "Human readable name of the 'minimal' standard Biolink Model predicate relationship name.   See [Biolink Model](https://biolink.github.io/biolink-model)  for the full list of terms. ")
  public String getEdgeLabel() {
    return edgeLabel;
  }

  public void setEdgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
  }

  public BeaconKnowledgeMapPredicate relation(String relation) {
    this.relation = relation;
    return this;
  }

   /**
   * Human readable name of a 'maximal' Biolink Model or  beacon-specific (or Reasoner-specific) predicate relationship name. 
   * @return relation
  **/
  @ApiModelProperty(value = "Human readable name of a 'maximal' Biolink Model or  beacon-specific (or Reasoner-specific) predicate relationship name. ")
  public String getRelation() {
    return relation;
  }

  public void setRelation(String relation) {
    this.relation = relation;
  }

  public BeaconKnowledgeMapPredicate negated(Boolean negated) {
    this.negated = negated;
    return this;
  }

   /**
   * Indicates whether edge statement is negated (i.e. is not true) 
   * @return negated
  **/
  @ApiModelProperty(value = "Indicates whether edge statement is negated (i.e. is not true) ")
  public Boolean getNegated() {
    return negated;
  }

  public void setNegated(Boolean negated) {
    this.negated = negated;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconKnowledgeMapPredicate beaconKnowledgeMapPredicate = (BeaconKnowledgeMapPredicate) o;
    return Objects.equals(this.edgeLabel, beaconKnowledgeMapPredicate.edgeLabel) &&
        Objects.equals(this.relation, beaconKnowledgeMapPredicate.relation) &&
        Objects.equals(this.negated, beaconKnowledgeMapPredicate.negated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(edgeLabel, relation, negated);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconKnowledgeMapPredicate {\n");
    
    sb.append("    edgeLabel: ").append(toIndentedString(edgeLabel)).append("\n");
    sb.append("    relation: ").append(toIndentedString(relation)).append("\n");
    sb.append("    negated: ").append(toIndentedString(negated)).append("\n");
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

