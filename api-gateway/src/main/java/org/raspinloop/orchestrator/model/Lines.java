package org.raspinloop.orchestrator.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Generated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Lines
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")

@Generated
public class Lines   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("x-scale")
  @Valid
  private List<BigDecimal> xScale = null;

  @JsonProperty("y-scale")
  @Valid
  private List<BigDecimal> yScale = null;

  @JsonProperty("points")
  @Valid
  private List<BigDecimal> points = null;

  public Lines name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   **/
  @Schema(example = "Temperature", description = "")
  
    public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Lines xScale(List<BigDecimal> xScale) {
    this.xScale = xScale;
    return this;
  }

  public Lines addXScaleItem(BigDecimal xScaleItem) {
    if (this.xScale == null) {
      this.xScale = new ArrayList<BigDecimal>();
    }
    this.xScale.add(xScaleItem);
    return this;
  }

  /**
   * Get xScale
   * @return xScale
   **/
  @Schema(example = "[0,10]", description = "")
      @Valid
  @Size(min=2,max=2)   public List<BigDecimal> getXScale() {
    return xScale;
  }

  public void setXScale(List<BigDecimal> xScale) {
    this.xScale = xScale;
  }

  public Lines yScale(List<BigDecimal> yScale) {
    this.yScale = yScale;
    return this;
  }

  public Lines addYScaleItem(BigDecimal yScaleItem) {
    if (this.yScale == null) {
      this.yScale = new ArrayList<BigDecimal>();
    }
    this.yScale.add(yScaleItem);
    return this;
  }

  /**
   * Get yScale
   * @return yScale
   **/
  @Schema(example = "[-100,100]", description = "")
      @Valid
  @Size(min=2,max=2)   public List<BigDecimal> getYScale() {
    return yScale;
  }

  public void setYScale(List<BigDecimal> yScale) {
    this.yScale = yScale;
  }

  public Lines points(List<BigDecimal> points) {
    this.points = points;
    return this;
  }

  public Lines addPointsItem(BigDecimal pointsItem) {
    if (this.points == null) {
      this.points = new ArrayList<BigDecimal>();
    }
    this.points.add(pointsItem);
    return this;
  }

  /**
   * Get points
   * @return points
   **/
  @Schema(description = "")
      @Valid
    public List<BigDecimal> getPoints() {
    return points;
  }

  public void setPoints(List<BigDecimal> points) {
    this.points = points;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Lines lines = (Lines) o;
    return Objects.equals(this.name, lines.name) &&
        Objects.equals(this.xScale, lines.xScale) &&
        Objects.equals(this.yScale, lines.yScale) &&
        Objects.equals(this.points, lines.points);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, xScale, yScale, points);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Lines {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    xScale: ").append(toIndentedString(xScale)).append("\n");
    sb.append("    yScale: ").append(toIndentedString(yScale)).append("\n");
    sb.append("    points: ").append(toIndentedString(points)).append("\n");
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
