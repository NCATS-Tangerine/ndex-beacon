package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import bio.knowledge.server.impl.CachedEntity;
import io.swagger.annotations.ApiModelProperty;
/**
 * BeaconStatement
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-17T12:46:02.920-07:00")

public class BeaconStatement implements CachedEntity  {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("subject")
  private BeaconStatementSubject subject = null;

  @JsonProperty("predicate")
  private BeaconStatementPredicate predicate = null;

  @JsonProperty("object")
  private BeaconStatementObject object = null;

  public BeaconStatement id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier for statement (can be used to retrieve associated evidence)
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier for statement (can be used to retrieve associated evidence)")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BeaconStatement subject(BeaconStatementSubject subject) {
    this.subject = subject;
    return this;
  }

   /**
   * Get subject
   * @return subject
  **/
  @ApiModelProperty(value = "")
  public BeaconStatementSubject getSubject() {
    return subject;
  }

  public void setSubject(BeaconStatementSubject subject) {
    this.subject = subject;
  }

  public BeaconStatement predicate(BeaconStatementPredicate predicate) {
    this.predicate = predicate;
    return this;
  }

   /**
   * Get predicate
   * @return predicate
  **/
  @ApiModelProperty(value = "")
  public BeaconStatementPredicate getPredicate() {
    return predicate;
  }

  public void setPredicate(BeaconStatementPredicate predicate) {
    this.predicate = predicate;
  }

  public BeaconStatement object(BeaconStatementObject object) {
    this.object = object;
    return this;
  }

   /**
   * Get object
   * @return object
  **/
  @ApiModelProperty(value = "")
  public BeaconStatementObject getObject() {
    return object;
  }

  public void setObject(BeaconStatementObject object) {
    this.object = object;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconStatement beaconStatement = (BeaconStatement) o;
    return Objects.equals(this.id, beaconStatement.id) &&
        Objects.equals(this.subject, beaconStatement.subject) &&
        Objects.equals(this.predicate, beaconStatement.predicate) &&
        Objects.equals(this.object, beaconStatement.object);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, subject, predicate, object);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconStatement {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
    sb.append("    predicate: ").append(toIndentedString(predicate)).append("\n");
    sb.append("    object: ").append(toIndentedString(object)).append("\n");
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

