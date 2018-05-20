package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import bio.knowledge.server.impl.CachedEntity;
import io.swagger.annotations.ApiModelProperty;
/**
 * BeaconConcept
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-17T12:46:02.920-07:00")

public class BeaconConcept implements CachedEntity  {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("category")
  private String category = null;

  @JsonProperty("synonyms")
  private List<String> synonyms = new ArrayList<String>();

  @JsonProperty("definition")
  private String definition = null;

  public BeaconConcept id(String id) {
    this.id = id;
    return this;
  }

   /**
   * local object identifier for the concept in the specified knowledge source database 
   * @return id
  **/
  @ApiModelProperty(value = "local object identifier for the concept in the specified knowledge source database ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BeaconConcept name(String name) {
    this.name = name;
    return this;
  }

   /**
   * canonical human readable name of the concept 
   * @return name
  **/
  @ApiModelProperty(value = "canonical human readable name of the concept ")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BeaconConcept category(String category) {
    this.category = category;
    return this;
  }

   /**
   * concept semantic type 
   * @return category
  **/
  @ApiModelProperty(value = "concept semantic type ")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public BeaconConcept synonyms(List<String> synonyms) {
    this.synonyms = synonyms;
    return this;
  }

  public BeaconConcept addSynonymsItem(String synonymsItem) {
    this.synonyms.add(synonymsItem);
    return this;
  }

   /**
   * list of synonyms for concept 
   * @return synonyms
  **/
  @ApiModelProperty(value = "list of synonyms for concept ")
  public List<String> getSynonyms() {
    return synonyms;
  }

  public void setSynonyms(List<String> synonyms) {
    this.synonyms = synonyms;
  }

  public BeaconConcept definition(String definition) {
    this.definition = definition;
    return this;
  }

   /**
   * concept definition 
   * @return definition
  **/
  @ApiModelProperty(value = "concept definition ")
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
    BeaconConcept beaconConcept = (BeaconConcept) o;
    return Objects.equals(this.id, beaconConcept.id) &&
        Objects.equals(this.name, beaconConcept.name) &&
        Objects.equals(this.category, beaconConcept.category) &&
        Objects.equals(this.synonyms, beaconConcept.synonyms) &&
        Objects.equals(this.definition, beaconConcept.definition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, category, synonyms, definition);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconConcept {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    synonyms: ").append(toIndentedString(synonyms)).append("\n");
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

