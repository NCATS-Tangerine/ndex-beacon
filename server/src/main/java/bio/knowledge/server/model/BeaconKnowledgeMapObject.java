package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * BeaconKnowledgeMapObject
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-12T19:09:25.899Z")

public class BeaconKnowledgeMapObject   {
  @JsonProperty("category")
  private String category = null;

  @JsonProperty("prefixes")
  private List<String> prefixes = new ArrayList<String>();

  public BeaconKnowledgeMapObject category(String category) {
    this.category = category;
    return this;
  }

   /**
   * the concept semantic type of a statement object. This  should be specified as Biolink concept semantic type names like gene, pathway, etc. (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of terms) 
   * @return category
  **/
  @ApiModelProperty(value = "the concept semantic type of a statement object. This  should be specified as Biolink concept semantic type names like gene, pathway, etc. (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of terms) ")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public BeaconKnowledgeMapObject prefixes(List<String> prefixes) {
    this.prefixes = prefixes;
    return this;
  }

  public BeaconKnowledgeMapObject addPrefixesItem(String prefixesItem) {
    this.prefixes.add(prefixesItem);
    return this;
  }

   /**
   * Get prefixes
   * @return prefixes
  **/
  @ApiModelProperty(value = "")
  public List<String> getPrefixes() {
    return prefixes;
  }

  public void setPrefixes(List<String> prefixes) {
    this.prefixes = prefixes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconKnowledgeMapObject beaconKnowledgeMapObject = (BeaconKnowledgeMapObject) o;
    return Objects.equals(this.category, beaconKnowledgeMapObject.category) &&
        Objects.equals(this.prefixes, beaconKnowledgeMapObject.prefixes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(category, prefixes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconKnowledgeMapObject {\n");
    
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    prefixes: ").append(toIndentedString(prefixes)).append("\n");
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

