package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * Summary
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-11-05T07:12:50.191-08:00")

public class Summary   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("idmap")
  private String idmap = null;

  @JsonProperty("frequency")
  private Integer frequency = null;

  public Summary id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the type  
   * @return id
  **/
  @ApiModelProperty(value = "the type  ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Summary idmap(String idmap) {
    this.idmap = idmap;
    return this;
  }

   /**
   * The URL to execute the exactmatches API call on the id
   * @return idmap
  **/
  @ApiModelProperty(value = "The URL to execute the exactmatches API call on the id")
  public String getIdmap() {
    return idmap;
  }

  public void setIdmap(String idmap) {
    this.idmap = idmap;
  }

  public Summary frequency(Integer frequency) {
    this.frequency = frequency;
    return this;
  }

   /**
   * the number of instances of the specified type 
   * @return frequency
  **/
  @ApiModelProperty(value = "the number of instances of the specified type ")
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
    Summary summary = (Summary) o;
    return Objects.equals(this.id, summary.id) &&
        Objects.equals(this.idmap, summary.idmap) &&
        Objects.equals(this.frequency, summary.frequency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, idmap, frequency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Summary {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    idmap: ").append(toIndentedString(idmap)).append("\n");
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

