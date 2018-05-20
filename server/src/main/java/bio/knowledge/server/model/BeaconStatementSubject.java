package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * BeaconStatementSubject
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-17T12:46:02.920-07:00")

public class BeaconStatementSubject   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("category")
  private String category = null;

  public BeaconStatementSubject id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier of concept 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier of concept ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BeaconStatementSubject name(String name) {
    this.name = name;
    return this;
  }

   /**
   * human readable label of subject concept
   * @return name
  **/
  @ApiModelProperty(value = "human readable label of subject concept")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BeaconStatementSubject category(String category) {
    this.category = category;
    return this;
  }

   /**
   * a semantic group for the subject concept (specified as a code gene, pathway, disease, etc. - see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of categories) 
   * @return category
  **/
  @ApiModelProperty(value = "a semantic group for the subject concept (specified as a code gene, pathway, disease, etc. - see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of categories) ")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconStatementSubject beaconStatementSubject = (BeaconStatementSubject) o;
    return Objects.equals(this.id, beaconStatementSubject.id) &&
        Objects.equals(this.name, beaconStatementSubject.name) &&
        Objects.equals(this.category, beaconStatementSubject.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, category);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconStatementSubject {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
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

