package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.BeaconKnowledgeMapObject;
import bio.knowledge.server.model.BeaconKnowledgeMapPredicate;
import bio.knowledge.server.model.BeaconKnowledgeMapSubject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * BeaconKnowledgeMapStatement
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-17T12:46:02.920-07:00")

public class BeaconKnowledgeMapStatement   {
  @JsonProperty("subject")
  private BeaconKnowledgeMapSubject subject = null;

  @JsonProperty("predicate")
  private BeaconKnowledgeMapPredicate predicate = null;

  @JsonProperty("object")
  private BeaconKnowledgeMapObject object = null;

  @JsonProperty("frequency")
  private Integer frequency = null;

  @JsonProperty("description")
  private String description = null;

  public BeaconKnowledgeMapStatement subject(BeaconKnowledgeMapSubject subject) {
    this.subject = subject;
    return this;
  }

   /**
   * Get subject
   * @return subject
  **/
  @ApiModelProperty(value = "")
  public BeaconKnowledgeMapSubject getSubject() {
    return subject;
  }

  public void setSubject(BeaconKnowledgeMapSubject subject) {
    this.subject = subject;
  }

  public BeaconKnowledgeMapStatement predicate(BeaconKnowledgeMapPredicate predicate) {
    this.predicate = predicate;
    return this;
  }

   /**
   * Get predicate
   * @return predicate
  **/
  @ApiModelProperty(value = "")
  public BeaconKnowledgeMapPredicate getPredicate() {
    return predicate;
  }

  public void setPredicate(BeaconKnowledgeMapPredicate predicate) {
    this.predicate = predicate;
  }

  public BeaconKnowledgeMapStatement object(BeaconKnowledgeMapObject object) {
    this.object = object;
    return this;
  }

   /**
   * Get object
   * @return object
  **/
  @ApiModelProperty(value = "")
  public BeaconKnowledgeMapObject getObject() {
    return object;
  }

  public void setObject(BeaconKnowledgeMapObject object) {
    this.object = object;
  }

  public BeaconKnowledgeMapStatement frequency(Integer frequency) {
    this.frequency = frequency;
    return this;
  }

   /**
   * the frequency of statements of the specified relationship within the given beacon 
   * @return frequency
  **/
  @ApiModelProperty(value = "the frequency of statements of the specified relationship within the given beacon ")
  public Integer getFrequency() {
    return frequency;
  }

  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
  }

  public BeaconKnowledgeMapStatement description(String description) {
    this.description = description;
    return this;
  }

   /**
   * a description of the nature of the relationship 
   * @return description
  **/
  @ApiModelProperty(value = "a description of the nature of the relationship ")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconKnowledgeMapStatement beaconKnowledgeMapStatement = (BeaconKnowledgeMapStatement) o;
    return Objects.equals(this.subject, beaconKnowledgeMapStatement.subject) &&
        Objects.equals(this.predicate, beaconKnowledgeMapStatement.predicate) &&
        Objects.equals(this.object, beaconKnowledgeMapStatement.object) &&
        Objects.equals(this.frequency, beaconKnowledgeMapStatement.frequency) &&
        Objects.equals(this.description, beaconKnowledgeMapStatement.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, predicate, object, frequency, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconKnowledgeMapStatement {\n");
    
    sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
    sb.append("    predicate: ").append(toIndentedString(predicate)).append("\n");
    sb.append("    object: ").append(toIndentedString(object)).append("\n");
    sb.append("    frequency: ").append(toIndentedString(frequency)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

