package org.raspinloop.orchestrator.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.raspinloop.orchestrator.model.Lines;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Generated;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * TestRun
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")

@Generated
public class TestRun   {
  @JsonProperty("stopTime")
  private Float stopTime = null;

  @JsonProperty("StepSize")
  private Float stepSize = null;

  @JsonProperty("currentStep")
  private Float currentStep = null;

  @JsonProperty("probes")
  @Valid
  private List<Lines> probes = null;

  public TestRun stopTime(Float stopTime) {
    this.stopTime = stopTime;
    return this;
  }

  /**
   * Get stopTime
   * @return stopTime
   **/
  @Schema(example = "100", description = "")
  
    public Float getStopTime() {
    return stopTime;
  }

  public void setStopTime(Float stopTime) {
    this.stopTime = stopTime;
  }

  public TestRun stepSize(Float stepSize) {
    this.stepSize = stepSize;
    return this;
  }

  /**
   * Get stepSize
   * @return stepSize
   **/
  @Schema(example = "0.1", description = "")
  
    public Float getStepSize() {
    return stepSize;
  }

  public void setStepSize(Float stepSize) {
    this.stepSize = stepSize;
  }

  public TestRun currentStep(Float currentStep) {
    this.currentStep = currentStep;
    return this;
  }

  /**
   * Get currentStep
   * @return currentStep
   **/
  @Schema(example = "7.3", description = "")
  
    public Float getCurrentStep() {
    return currentStep;
  }

  public void setCurrentStep(Float currentStep) {
    this.currentStep = currentStep;
  }

  public TestRun probes(List<Lines> probes) {
    this.probes = probes;
    return this;
  }

  public TestRun addProbesItem(Lines probesItem) {
    if (this.probes == null) {
      this.probes = new ArrayList<Lines>();
    }
    this.probes.add(probesItem);
    return this;
  }

  /**
   * Get probes
   * @return probes
   **/
  @Schema(description = "")
      @Valid
    public List<Lines> getProbes() {
    return probes;
  }

  public void setProbes(List<Lines> probes) {
    this.probes = probes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestRun testRun = (TestRun) o;
    return Objects.equals(this.stopTime, testRun.stopTime) &&
        Objects.equals(this.stepSize, testRun.stepSize) &&
        Objects.equals(this.currentStep, testRun.currentStep) &&
        Objects.equals(this.probes, testRun.probes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stopTime, stepSize, currentStep, probes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TestRun {\n");
    
    sb.append("    stopTime: ").append(toIndentedString(stopTime)).append("\n");
    sb.append("    stepSize: ").append(toIndentedString(stepSize)).append("\n");
    sb.append("    currentStep: ").append(toIndentedString(currentStep)).append("\n");
    sb.append("    probes: ").append(toIndentedString(probes)).append("\n");
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
