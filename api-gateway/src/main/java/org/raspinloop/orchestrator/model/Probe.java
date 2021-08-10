package org.raspinloop.orchestrator.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Generated;

import java.math.BigDecimal;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Probe
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")

@Generated
public class Probe   {
  @JsonProperty("signalName")
  private String signalName = null;

  /**
   * Gets or Sets signalKind
   */
  public enum SignalKindEnum {
    BOOLEAN("boolean"),
    
    INTEGER("integer"),
    
    FLOAT("float");

    private String value;

    SignalKindEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static SignalKindEnum fromValue(String text) {
      for (SignalKindEnum b : SignalKindEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("signalKind")
  private SignalKindEnum signalKind = null;

  @JsonProperty("min")
  private BigDecimal min = null;

  @JsonProperty("max")
  private BigDecimal max = null;

  /**
   * Gets or Sets scale
   */
  public enum ScaleEnum {
    AUTO("auto"),
    
    DEFINED("defined");

    private String value;

    ScaleEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ScaleEnum fromValue(String text) {
      for (ScaleEnum b : ScaleEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("scale")
  private ScaleEnum scale = null;

  public Probe signalName(String signalName) {
    this.signalName = signalName;
    return this;
  }

  /**
   * Get signalName
   * @return signalName
   **/
  @Schema(example = "temperature 3", description = "")
  
    public String getSignalName() {
    return signalName;
  }

  public void setSignalName(String signalName) {
    this.signalName = signalName;
  }

  public Probe signalKind(SignalKindEnum signalKind) {
    this.signalKind = signalKind;
    return this;
  }

  /**
   * Get signalKind
   * @return signalKind
   **/
  @Schema(example = "float", description = "")
  
    public SignalKindEnum getSignalKind() {
    return signalKind;
  }

  public void setSignalKind(SignalKindEnum signalKind) {
    this.signalKind = signalKind;
  }

  public Probe min(BigDecimal min) {
    this.min = min;
    return this;
  }

  /**
   * Get min
   * @return min
   **/
  @Schema(description = "")
  
    @Valid
    public BigDecimal getMin() {
    return min;
  }

  public void setMin(BigDecimal min) {
    this.min = min;
  }

  public Probe max(BigDecimal max) {
    this.max = max;
    return this;
  }

  /**
   * Get max
   * @return max
   **/
  @Schema(example = "37.5", description = "")
  
    @Valid
    public BigDecimal getMax() {
    return max;
  }

  public void setMax(BigDecimal max) {
    this.max = max;
  }

  public Probe scale(ScaleEnum scale) {
    this.scale = scale;
    return this;
  }

  /**
   * Get scale
   * @return scale
   **/
  @Schema(example = "defined", description = "")
  
    public ScaleEnum getScale() {
    return scale;
  }

  public void setScale(ScaleEnum scale) {
    this.scale = scale;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Probe probe = (Probe) o;
    return Objects.equals(this.signalName, probe.signalName) &&
        Objects.equals(this.signalKind, probe.signalKind) &&
        Objects.equals(this.min, probe.min) &&
        Objects.equals(this.max, probe.max) &&
        Objects.equals(this.scale, probe.scale);
  }

  @Override
  public int hashCode() {
    return Objects.hash(signalName, signalKind, min, max, scale);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Probe {\n");
    
    sb.append("    signalName: ").append(toIndentedString(signalName)).append("\n");
    sb.append("    signalKind: ").append(toIndentedString(signalKind)).append("\n");
    sb.append("    min: ").append(toIndentedString(min)).append("\n");
    sb.append("    max: ").append(toIndentedString(max)).append("\n");
    sb.append("    scale: ").append(toIndentedString(scale)).append("\n");
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
