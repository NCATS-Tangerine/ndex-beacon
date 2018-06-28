package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
/**
 * BeaconConcept
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-27T18:01:11.438Z")

public class BeaconConcept   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("categories")
  private List<String> categories = new ArrayList<String>();

  @JsonProperty("description")
  private String description = null;

  public BeaconConcept id(String id) {
    this.id = id;
    return this;
  }

   /**
   * local object CURIE for the concept in the specified knowledge source database 
   * @return id
  **/
  @ApiModelProperty(value = "local object CURIE for the concept in the specified knowledge source database ")
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

  public BeaconConcept categories(List<String> categories) {
    this.categories = categories;
    return this;
  }

  public BeaconConcept addCategoriesItem(String categoriesItem) {
    this.categories.add(categoriesItem);
    return this;
  }

   /**
   * concept semantic type 'category'. Should be specified from the [Biolink Model](https://biolink.github.io/biolink-model). 
   * @return categories
  **/
  @ApiModelProperty(value = "concept semantic type 'category'. Should be specified from the [Biolink Model](https://biolink.github.io/biolink-model). ")
  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public BeaconConcept description(String description) {
    this.description = description;
    return this;
  }

   /**
   * (optional) narrative concept definition 
   * @return description
  **/
  @ApiModelProperty(value = "(optional) narrative concept definition ")
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
    BeaconConcept beaconConcept = (BeaconConcept) o;
    return Objects.equals(this.id, beaconConcept.id) &&
        Objects.equals(this.name, beaconConcept.name) &&
        Objects.equals(this.categories, beaconConcept.categories) &&
        Objects.equals(this.description, beaconConcept.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, categories, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconConcept {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
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

