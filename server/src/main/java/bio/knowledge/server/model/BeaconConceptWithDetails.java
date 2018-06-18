package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.BeaconConceptDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
/**
 * BeaconConceptWithDetails
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-12T19:09:25.899Z")

public class BeaconConceptWithDetails   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("uri")
  private String uri = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("symbol")
  private String symbol = null;

  @JsonProperty("category")
  private String category = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("synonyms")
  private List<String> synonyms = new ArrayList<String>();

  @JsonProperty("exact_matches")
  private List<String> exactMatches = new ArrayList<String>();

  @JsonProperty("details")
  private List<BeaconConceptDetail> details = new ArrayList<BeaconConceptDetail>();

  public BeaconConceptWithDetails id(String id) {
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

  public BeaconConceptWithDetails uri(String uri) {
    this.uri = uri;
    return this;
  }

   /**
   * (optional) equivalent to expansion of the CURIE 
   * @return uri
  **/
  @ApiModelProperty(value = "(optional) equivalent to expansion of the CURIE ")
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public BeaconConceptWithDetails name(String name) {
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

  public BeaconConceptWithDetails symbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

   /**
   * (optional) symbol, used for genomic data 
   * @return symbol
  **/
  @ApiModelProperty(value = "(optional) symbol, used for genomic data ")
  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public BeaconConceptWithDetails category(String category) {
    this.category = category;
    return this;
  }

   /**
   * concept semantic type 'category'. Should be specified from the [Biolink Model](https://biolink.github.io/biolink-model). 
   * @return category
  **/
  @ApiModelProperty(value = "concept semantic type 'category'. Should be specified from the [Biolink Model](https://biolink.github.io/biolink-model). ")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public BeaconConceptWithDetails description(String description) {
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

  public BeaconConceptWithDetails synonyms(List<String> synonyms) {
    this.synonyms = synonyms;
    return this;
  }

  public BeaconConceptWithDetails addSynonymsItem(String synonymsItem) {
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

  public BeaconConceptWithDetails exactMatches(List<String> exactMatches) {
    this.exactMatches = exactMatches;
    return this;
  }

  public BeaconConceptWithDetails addExactMatchesItem(String exactMatchesItem) {
    this.exactMatches.add(exactMatchesItem);
    return this;
  }

   /**
   * List of [CURIE](https://www.w3.org/TR/curie/)  identifiers of concepts thought to be exactly matching concepts, [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch). This is generally the same list returned by the /exact_matches endpoint (given the concept 'id' as input) 
   * @return exactMatches
  **/
  @ApiModelProperty(value = "List of [CURIE](https://www.w3.org/TR/curie/)  identifiers of concepts thought to be exactly matching concepts, [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch). This is generally the same list returned by the /exact_matches endpoint (given the concept 'id' as input) ")
  public List<String> getExactMatches() {
    return exactMatches;
  }

  public void setExactMatches(List<String> exactMatches) {
    this.exactMatches = exactMatches;
  }

  public BeaconConceptWithDetails details(List<BeaconConceptDetail> details) {
    this.details = details;
    return this;
  }

  public BeaconConceptWithDetails addDetailsItem(BeaconConceptDetail detailsItem) {
    this.details.add(detailsItem);
    return this;
  }

   /**
   * Get details
   * @return details
  **/
  @ApiModelProperty(value = "")
  public List<BeaconConceptDetail> getDetails() {
    return details;
  }

  public void setDetails(List<BeaconConceptDetail> details) {
    this.details = details;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconConceptWithDetails beaconConceptWithDetails = (BeaconConceptWithDetails) o;
    return Objects.equals(this.id, beaconConceptWithDetails.id) &&
        Objects.equals(this.uri, beaconConceptWithDetails.uri) &&
        Objects.equals(this.name, beaconConceptWithDetails.name) &&
        Objects.equals(this.symbol, beaconConceptWithDetails.symbol) &&
        Objects.equals(this.category, beaconConceptWithDetails.category) &&
        Objects.equals(this.description, beaconConceptWithDetails.description) &&
        Objects.equals(this.synonyms, beaconConceptWithDetails.synonyms) &&
        Objects.equals(this.exactMatches, beaconConceptWithDetails.exactMatches) &&
        Objects.equals(this.details, beaconConceptWithDetails.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uri, name, symbol, category, description, synonyms, exactMatches, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconConceptWithDetails {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    symbol: ").append(toIndentedString(symbol)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    synonyms: ").append(toIndentedString(synonyms)).append("\n");
    sb.append("    exactMatches: ").append(toIndentedString(exactMatches)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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

