package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * Predicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-18T13:42:46.892-07:00")

public class Predicate   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("definition")
  private String definition = null;

  public Predicate id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier of predicate resource 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier of predicate resource ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Predicate name(String name) {
    this.name = name;
    return this;
  }

   /**
   * human readable name of predicate relation 
   * @return name
  **/
  @ApiModelProperty(value = "human readable name of predicate relation ")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Predicate definition(String definition) {
    this.definition = definition;
    return this;
  }

   /**
   * human readable definition of predicate relation provided by this beacon 
   * @return definition
  **/
  @ApiModelProperty(value = "human readable definition of predicate relation provided by this beacon ")
  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Predicate predicate = (Predicate) o;
    return Objects.equals(this.id, predicate.id) &&
        Objects.equals(this.name, predicate.name) &&
        Objects.equals(this.definition, predicate.definition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, definition);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Predicate {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
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

