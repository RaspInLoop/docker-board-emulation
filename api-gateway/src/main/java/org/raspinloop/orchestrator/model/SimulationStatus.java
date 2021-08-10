package org.raspinloop.orchestrator.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.raspinloop.orchestrator.model.TestRun;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Generated;

import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * SimulationStatus
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")

@Generated
public class SimulationStatus   {
  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    RUNNING("running"),
    
    STOPPED("stopped");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String text) {
      for (StatusEnum b : StatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("status")
  private StatusEnum status = null;

  @JsonProperty("currentTestRun")
  private TestRun currentTestRun = null;

  public SimulationStatus status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   **/
  @Schema(description = "")
  
    public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public SimulationStatus currentTestRun(TestRun currentTestRun) {
    this.currentTestRun = currentTestRun;
    return this;
  }

  /**
   * Get currentTestRun
   * @return currentTestRun
   **/
  @Schema(description = "")
  
    @Valid
    public TestRun getCurrentTestRun() {
    return currentTestRun;
  }

  public void setCurrentTestRun(TestRun currentTestRun) {
    this.currentTestRun = currentTestRun;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimulationStatus simulationStatus = (SimulationStatus) o;
    return Objects.equals(this.status, simulationStatus.status) &&
        Objects.equals(this.currentTestRun, simulationStatus.currentTestRun);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, currentTestRun);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SimulationStatus {\n");
    
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    currentTestRun: ").append(toIndentedString(currentTestRun)).append("\n");
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
