package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * BeaconPredicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-12T19:09:25.899Z")

public class BeaconPredicate   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("uri")
  private String uri = null;

  @JsonProperty("edge_label")
  private String edgeLabel = null;

  @JsonProperty("relation")
  private String relation = null;

  @JsonProperty("local_id")
  private String localId = null;

  @JsonProperty("local_uri")
  private String localUri = null;

  @JsonProperty("local_relation")
  private String localRelation = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("frequency")
  private Integer frequency = null;

  public BeaconPredicate id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier of predicate relation 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier of predicate relation ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BeaconPredicate uri(String uri) {
    this.uri = uri;
    return this;
  }

   /**
   * The predicate URI which should generally resolves to the  full semantic description of the predicate relation
   * @return uri
  **/
  @ApiModelProperty(value = "The predicate URI which should generally resolves to the  full semantic description of the predicate relation")
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public BeaconPredicate edgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
    return this;
  }

   /**
   * A predicate edge label which must be taken from the minimal predicate ('slot') list of the [Biolink Model](https://biolink.github.io/biolink-model). 
   * @return edgeLabel
  **/
  @ApiModelProperty(value = "A predicate edge label which must be taken from the minimal predicate ('slot') list of the [Biolink Model](https://biolink.github.io/biolink-model). ")
  public String getEdgeLabel() {
    return edgeLabel;
  }

  public void setEdgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
  }

  public BeaconPredicate relation(String relation) {
    this.relation = relation;
    return this;
  }

   /**
   * The predicate relation, with the preferred format being a CURIE where one exists, but strings/labels acceptable. This relation  may be equivalent to the edge_label (e.g. edge_label: has_phenotype, relation: RO:0002200), or a more specific relation in cases where the source provides more granularity  (e.g. edge_label: molecularly_interacts_with, relation: RO:0002447) 
   * @return relation
  **/
  @ApiModelProperty(value = "The predicate relation, with the preferred format being a CURIE where one exists, but strings/labels acceptable. This relation  may be equivalent to the edge_label (e.g. edge_label: has_phenotype, relation: RO:0002200), or a more specific relation in cases where the source provides more granularity  (e.g. edge_label: molecularly_interacts_with, relation: RO:0002447) ")
  public String getRelation() {
    return relation;
  }

  public void setRelation(String relation) {
    this.relation = relation;
  }

  public BeaconPredicate localId(String localId) {
    this.localId = localId;
    return this;
  }

   /**
   * CURIE-encoded identifier of the locally defined predicate relation. Such terms should be formally documented as mappings in the [Biolink Model](https://biolink.github.io/biolink-model) 
   * @return localId
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier of the locally defined predicate relation. Such terms should be formally documented as mappings in the [Biolink Model](https://biolink.github.io/biolink-model) ")
  public String getLocalId() {
    return localId;
  }

  public void setLocalId(String localId) {
    this.localId = localId;
  }

  public BeaconPredicate localUri(String localUri) {
    this.localUri = localUri;
    return this;
  }

   /**
   * The predicate URI which should generally resolves  to the local predicate relation
   * @return localUri
  **/
  @ApiModelProperty(value = "The predicate URI which should generally resolves  to the local predicate relation")
  public String getLocalUri() {
    return localUri;
  }

  public void setLocalUri(String localUri) {
    this.localUri = localUri;
  }

  public BeaconPredicate localRelation(String localRelation) {
    this.localRelation = localRelation;
    return this;
  }

   /**
   * human readable name of the locally defined predicate relation 
   * @return localRelation
  **/
  @ApiModelProperty(value = "human readable name of the locally defined predicate relation ")
  public String getLocalRelation() {
    return localRelation;
  }

  public void setLocalRelation(String localRelation) {
    this.localRelation = localRelation;
  }

  public BeaconPredicate description(String description) {
    this.description = description;
    return this;
  }

   /**
   * human readable definition of predicate relation  provided by this beacon 
   * @return description
  **/
  @ApiModelProperty(value = "human readable definition of predicate relation  provided by this beacon ")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BeaconPredicate frequency(Integer frequency) {
    this.frequency = frequency;
    return this;
  }

   /**
   * the number of statement entries using the specified predicate in the given beacon knowledge base
   * @return frequency
  **/
  @ApiModelProperty(value = "the number of statement entries using the specified predicate in the given beacon knowledge base")
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
    BeaconPredicate beaconPredicate = (BeaconPredicate) o;
    return Objects.equals(this.id, beaconPredicate.id) &&
        Objects.equals(this.uri, beaconPredicate.uri) &&
        Objects.equals(this.edgeLabel, beaconPredicate.edgeLabel) &&
        Objects.equals(this.relation, beaconPredicate.relation) &&
        Objects.equals(this.localId, beaconPredicate.localId) &&
        Objects.equals(this.localUri, beaconPredicate.localUri) &&
        Objects.equals(this.localRelation, beaconPredicate.localRelation) &&
        Objects.equals(this.description, beaconPredicate.description) &&
        Objects.equals(this.frequency, beaconPredicate.frequency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uri, edgeLabel, relation, localId, localUri, localRelation, description, frequency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconPredicate {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    edgeLabel: ").append(toIndentedString(edgeLabel)).append("\n");
    sb.append("    relation: ").append(toIndentedString(relation)).append("\n");
    sb.append("    localId: ").append(toIndentedString(localId)).append("\n");
    sb.append("    localUri: ").append(toIndentedString(localUri)).append("\n");
    sb.append("    localRelation: ").append(toIndentedString(localRelation)).append("\n");
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

