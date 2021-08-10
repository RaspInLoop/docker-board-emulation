package org.raspinloop.orchestrator.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Generated;

import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Connection
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")

@Generated
public class Connection   {
  /**
   * Gets or Sets startElement
   */
  public enum StartElementEnum {
    MODEL("model"),
    
    BOARD("board");

    private String value;

    StartElementEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StartElementEnum fromValue(String text) {
      for (StartElementEnum b : StartElementEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("startElement")
  private StartElementEnum startElement = null;

  @JsonProperty("startConnector")
  private String startConnector = null;

  /**
   * Gets or Sets endElement
   */
  public enum EndElementEnum {
    MODEL("model"),
    
    BOARD("board"),
    
    INSTUMENTS("instuments");

    private String value;

    EndElementEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static EndElementEnum fromValue(String text) {
      for (EndElementEnum b : EndElementEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("endElement")
  private EndElementEnum endElement = null;

  @JsonProperty("endConnector")
  private String endConnector = null;

  public Connection startElement(StartElementEnum startElement) {
    this.startElement = startElement;
    return this;
  }

  /**
   * Get startElement
   * @return startElement
   **/
  @Schema(example = "model", description = "")
  
    public StartElementEnum getStartElement() {
    return startElement;
  }

  public void setStartElement(StartElementEnum startElement) {
    this.startElement = startElement;
  }

  public Connection startConnector(String startConnector) {
    this.startConnector = startConnector;
    return this;
  }

  /**
   * Get startConnector
   * @return startConnector
   **/
  @Schema(example = "temp_ext", description = "")
  
    public String getStartConnector() {
    return startConnector;
  }

  public void setStartConnector(String startConnector) {
    this.startConnector = startConnector;
  }

  public Connection endElement(EndElementEnum endElement) {
    this.endElement = endElement;
    return this;
  }

  /**
   * Get endElement
   * @return endElement
   **/
  @Schema(example = "board", description = "")
  
    public EndElementEnum getEndElement() {
    return endElement;
  }

  public void setEndElement(EndElementEnum endElement) {
    this.endElement = endElement;
  }

  public Connection endConnector(String endConnector) {
    this.endConnector = endConnector;
    return this;
  }

  /**
   * Get endConnector
   * @return endConnector
   **/
  @Schema(example = "AN_01", description = "")
  
    public String getEndConnector() {
    return endConnector;
  }

  public void setEndConnector(String endConnector) {
    this.endConnector = endConnector;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Connection connection = (Connection) o;
    return Objects.equals(this.startElement, connection.startElement) &&
        Objects.equals(this.startConnector, connection.startConnector) &&
        Objects.equals(this.endElement, connection.endElement) &&
        Objects.equals(this.endConnector, connection.endConnector);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startElement, startConnector, endElement, endConnector);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Connection {\n");
    
    sb.append("    startElement: ").append(toIndentedString(startElement)).append("\n");
    sb.append("    startConnector: ").append(toIndentedString(startConnector)).append("\n");
    sb.append("    endElement: ").append(toIndentedString(endElement)).append("\n");
    sb.append("    endConnector: ").append(toIndentedString(endConnector)).append("\n");
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
