package org.raspinloop.orchestrator.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Generated;

import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * SimulationStartParam
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")

@Generated
public class SimulationStartParam   {
  @JsonProperty("stopTime")
  private Float stopTime = null;

  @JsonProperty("StepSize")
  private Float stepSize = null;

  public SimulationStartParam stopTime(Float stopTime) {
    this.stopTime = stopTime;
    return this;
  }

  /**
   * Get stopTime
   * @return stopTime
   **/
  @Schema(example = "100", required = true, description = "")
      @NotNull

    public Float getStopTime() {
    return stopTime;
  }

  public void setStopTime(Float stopTime) {
    this.stopTime = stopTime;
  }

  public SimulationStartParam stepSize(Float stepSize) {
    this.stepSize = stepSize;
    return this;
  }

  /**
   * Get stepSize
   * @return stepSize
   **/
  @Schema(example = "0.1", required = true, description = "")
      @NotNull

    public Float getStepSize() {
    return stepSize;
  }

  public void setStepSize(Float stepSize) {
    this.stepSize = stepSize;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimulationStartParam simulationStartParam = (SimulationStartParam) o;
    return Objects.equals(this.stopTime, simulationStartParam.stopTime) &&
        Objects.equals(this.stepSize, simulationStartParam.stepSize);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stopTime, stepSize);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SimulationStartParam {\n");
    
    sb.append("    stopTime: ").append(toIndentedString(stopTime)).append("\n");
    sb.append("    stepSize: ").append(toIndentedString(stepSize)).append("\n");
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
