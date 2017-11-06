package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * Annotation
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-11-05T07:12:50.191-08:00")

public class Annotation   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("label")
  private String label = null;

  @JsonProperty("type")
  private String type = null;

  @JsonProperty("date")
  private String date = null;

  public Annotation id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier to an associated external resources (e.g. PMID of a pubmed citation) 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier to an associated external resources (e.g. PMID of a pubmed citation) ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Annotation label(String label) {
    this.label = label;
    return this;
  }

   /**
   * canonical human readable and searchable label of the annotation (i.e. comment, matched sentence, etc.) 
   * @return label
  **/
  @ApiModelProperty(value = "canonical human readable and searchable label of the annotation (i.e. comment, matched sentence, etc.) ")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Annotation type(String type) {
    this.type = type;
    return this;
  }

   /**
   * Gene Ontology Evidence Code (http://www.geneontology.org/page/guide-go-evidence-codes) 
   * @return type
  **/
  @ApiModelProperty(value = "Gene Ontology Evidence Code (http://www.geneontology.org/page/guide-go-evidence-codes) ")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Annotation date(String date) {
    this.date = date;
    return this;
  }

   /**
   * publication date of annotation (generally of format 'yyyy-mm-dd') 
   * @return date
  **/
  @ApiModelProperty(value = "publication date of annotation (generally of format 'yyyy-mm-dd') ")
  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Annotation annotation = (Annotation) o;
    return Objects.equals(this.id, annotation.id) &&
        Objects.equals(this.label, annotation.label) &&
        Objects.equals(this.type, annotation.type) &&
        Objects.equals(this.date, annotation.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, label, type, date);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Annotation {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
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

