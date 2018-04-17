package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * BeaconConceptType
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-17T14:24:56.897-07:00")

public class BeaconConceptType   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("iri")
  private String iri = null;

  @JsonProperty("label")
  private String label = null;

  @JsonProperty("frequency")
  private Integer frequency = null;

  public BeaconConceptType id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the CURIE of the type
   * @return id
  **/
  @ApiModelProperty(value = "the CURIE of the type")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BeaconConceptType iri(String iri) {
    this.iri = iri;
    return this;
  }

   /**
   * The full IRI, generally resolve the full semantic description of the type
   * @return iri
  **/
  @ApiModelProperty(value = "The full IRI, generally resolve the full semantic description of the type")
  public String getIri() {
    return iri;
  }

  public void setIri(String iri) {
    this.iri = iri;
  }

  public BeaconConceptType label(String label) {
    this.label = label;
    return this;
  }

   /**
   * human readable name (\"rdfs:label\")
   * @return label
  **/
  @ApiModelProperty(value = "human readable name (\"rdfs:label\")")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public BeaconConceptType frequency(Integer frequency) {
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
    BeaconConceptType beaconConceptType = (BeaconConceptType) o;
    return Objects.equals(this.id, beaconConceptType.id) &&
        Objects.equals(this.iri, beaconConceptType.iri) &&
        Objects.equals(this.label, beaconConceptType.label) &&
        Objects.equals(this.frequency, beaconConceptType.frequency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, iri, label, frequency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconConceptType {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    iri: ").append(toIndentedString(iri)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
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

