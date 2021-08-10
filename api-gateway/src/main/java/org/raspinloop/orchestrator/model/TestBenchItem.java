package org.raspinloop.orchestrator.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.raspinloop.orchestrator.model.Simulation;
import org.raspinloop.orchestrator.model.SystemUnderTest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Generated;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * TestBenchItem
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")

@Generated
public class TestBenchItem   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("systemUnderTest")
  private SystemUnderTest systemUnderTest = null;

  @JsonProperty("simulation")
  private Simulation simulation = null;

  @JsonProperty("previousTestRuns")
  @Valid
  private List<Long> previousTestRuns = null;

  public TestBenchItem id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   **/
  @Schema(example = "d290f1ee-6c54-4b01-90e6-d701748f0851", accessMode = Schema.AccessMode.READ_ONLY, description = "")
  
    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public TestBenchItem name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   **/
  @Schema(example = "My simple project", required = true, description = "")
      @NotNull

    public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TestBenchItem systemUnderTest(SystemUnderTest systemUnderTest) {
    this.systemUnderTest = systemUnderTest;
    return this;
  }

  /**
   * Get systemUnderTest
   * @return systemUnderTest
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public SystemUnderTest getSystemUnderTest() {
    return systemUnderTest;
  }

  public void setSystemUnderTest(SystemUnderTest systemUnderTest) {
    this.systemUnderTest = systemUnderTest;
  }

  public TestBenchItem simulation(Simulation simulation) {
    this.simulation = simulation;
    return this;
  }

  /**
   * Get simulation
   * @return simulation
   **/
  @Schema(description = "")
  
    @Valid
    public Simulation getSimulation() {
    return simulation;
  }

  public void setSimulation(Simulation simulation) {
    this.simulation = simulation;
  }

  public TestBenchItem previousTestRuns(List<Long> previousTestRuns) {
    this.previousTestRuns = previousTestRuns;
    return this;
  }

  public TestBenchItem addPreviousTestRunsItem(Long previousTestRunsItem) {
    if (this.previousTestRuns == null) {
      this.previousTestRuns = new ArrayList<Long>();
    }
    this.previousTestRuns.add(previousTestRunsItem);
    return this;
  }

  /**
   * Get previousTestRuns
   * @return previousTestRuns
   **/
  @Schema(example = "[45875,665874,2222668,9854785]", accessMode = Schema.AccessMode.READ_ONLY, description = "")
  
    public List<Long> getPreviousTestRuns() {
    return previousTestRuns;
  }

  public void setPreviousTestRuns(List<Long> previousTestRuns) {
    this.previousTestRuns = previousTestRuns;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestBenchItem testBenchItem = (TestBenchItem) o;
    return Objects.equals(this.id, testBenchItem.id) &&
        Objects.equals(this.name, testBenchItem.name) &&
        Objects.equals(this.systemUnderTest, testBenchItem.systemUnderTest) &&
        Objects.equals(this.simulation, testBenchItem.simulation) &&
        Objects.equals(this.previousTestRuns, testBenchItem.previousTestRuns);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, systemUnderTest, simulation, previousTestRuns);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TestBenchItem {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    systemUnderTest: ").append(toIndentedString(systemUnderTest)).append("\n");
    sb.append("    simulation: ").append(toIndentedString(simulation)).append("\n");
    sb.append("    previousTestRuns: ").append(toIndentedString(previousTestRuns)).append("\n");
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
