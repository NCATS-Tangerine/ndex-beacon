package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * BeaconConceptCategory
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-17T12:46:02.920-07:00")

public class BeaconConceptCategory   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("uri")
  private String uri = null;

  @JsonProperty("category")
  private String category = null;

  @JsonProperty("local_id")
  private String localId = null;

  @JsonProperty("local_uri")
  private String localUri = null;

  @JsonProperty("local_category")
  private String localCategory = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("frequency")
  private Integer frequency = null;

  public BeaconConceptCategory id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the CURIE of the concept category
   * @return id
  **/
  @ApiModelProperty(value = "the CURIE of the concept category")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BeaconConceptCategory uri(String uri) {
    this.uri = uri;
    return this;
  }

   /**
   * The category URI which should generally resolves to  the full semantic description of the category
   * @return uri
  **/
  @ApiModelProperty(value = "The category URI which should generally resolves to  the full semantic description of the category")
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public BeaconConceptCategory category(String category) {
    this.category = category;
    return this;
  }

   /**
   * human readable name (\"rdfs:label\")
   * @return category
  **/
  @ApiModelProperty(value = "human readable name (\"rdfs:label\")")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public BeaconConceptCategory localId(String localId) {
    this.localId = localId;
    return this;
  }

   /**
   * the locally designated CURIE of the concept category
   * @return localId
  **/
  @ApiModelProperty(value = "the locally designated CURIE of the concept category")
  public String getLocalId() {
    return localId;
  }

  public void setLocalId(String localId) {
    this.localId = localId;
  }

  public BeaconConceptCategory localUri(String localUri) {
    this.localUri = localUri;
    return this;
  }

   /**
   * This locally designated category URI should generally  resolve to the full semantic description of the category
   * @return localUri
  **/
  @ApiModelProperty(value = "This locally designated category URI should generally  resolve to the full semantic description of the category")
  public String getLocalUri() {
    return localUri;
  }

  public void setLocalUri(String localUri) {
    this.localUri = localUri;
  }

  public BeaconConceptCategory localCategory(String localCategory) {
    this.localCategory = localCategory;
    return this;
  }

   /**
   * human readable local semantic type name (\"rdfs:label\")
   * @return localCategory
  **/
  @ApiModelProperty(value = "human readable local semantic type name (\"rdfs:label\")")
  public String getLocalCategory() {
    return localCategory;
  }

  public void setLocalCategory(String localCategory) {
    this.localCategory = localCategory;
  }

  public BeaconConceptCategory description(String description) {
    this.description = description;
    return this;
  }

   /**
   * human readable name definition of the category
   * @return description
  **/
  @ApiModelProperty(value = "human readable name definition of the category")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BeaconConceptCategory frequency(Integer frequency) {
    this.frequency = frequency;
    return this;
  }

   /**
   * the number of concept entries of the specified type in the beacon knowledge base
   * @return frequency
  **/
  @ApiModelProperty(value = "the number of concept entries of the specified type in the beacon knowledge base")
  public Integer getFrequency() {
    return frequency;
  }

  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconConceptCategory beaconConceptCategory = (BeaconConceptCategory) o;
    return Objects.equals(this.id, beaconConceptCategory.id) &&
        Objects.equals(this.uri, beaconConceptCategory.uri) &&
        Objects.equals(this.category, beaconConceptCategory.category) &&
        Objects.equals(this.localId, beaconConceptCategory.localId) &&
        Objects.equals(this.localUri, beaconConceptCategory.localUri) &&
        Objects.equals(this.localCategory, beaconConceptCategory.localCategory) &&
        Objects.equals(this.description, beaconConceptCategory.description) &&
        Objects.equals(this.frequency, beaconConceptCategory.frequency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uri, category, localId, localUri, localCategory, description, frequency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconConceptCategory {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    localId: ").append(toIndentedString(localId)).append("\n");
    sb.append("    localUri: ").append(toIndentedString(localUri)).append("\n");
    sb.append("    localCategory: ").append(toIndentedString(localCategory)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    frequency: ").append(toIndentedString(frequency)).append("\n");
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

