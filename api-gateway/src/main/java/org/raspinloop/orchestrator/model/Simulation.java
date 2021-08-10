package org.raspinloop.orchestrator.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.raspinloop.orchestrator.model.Connections;
import org.raspinloop.orchestrator.model.Instrument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Generated;

import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Simulation
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")

@Generated
public class Simulation   {
  @JsonProperty("fmuRef")
  private String fmuRef = null;

  @JsonProperty("instrument")
  private Instrument instrument = null;

  @JsonProperty("connections")
  private Connections connections = null;

  public Simulation fmuRef(String fmuRef) {
    this.fmuRef = fmuRef;
    return this;
  }

  /**
   * Get fmuRef
   * @return fmuRef
   **/
  @Schema(example = "42", description = "")
  
    public String getFmuRef() {
    return fmuRef;
  }

  public void setFmuRef(String fmuRef) {
    this.fmuRef = fmuRef;
  }

  public Simulation instrument(Instrument instrument) {
    this.instrument = instrument;
    return this;
  }

  /**
   * Get instrument
   * @return instrument
   **/
  @Schema(description = "")
  
    @Valid
    public Instrument getInstrument() {
    return instrument;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  public Simulation connections(Connections connections) {
    this.connections = connections;
    return this;
  }

  /**
   * Get connections
   * @return connections
   **/
  @Schema(description = "")
  
    @Valid
    public Connections getConnections() {
    return connections;
  }

  public void setConnections(Connections connections) {
    this.connections = connections;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Simulation simulation = (Simulation) o;
    return Objects.equals(this.fmuRef, simulation.fmuRef) &&
        Objects.equals(this.instrument, simulation.instrument) &&
        Objects.equals(this.connections, simulation.connections);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fmuRef, instrument, connections);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Simulation {\n");
    
    sb.append("    fmuRef: ").append(toIndentedString(fmuRef)).append("\n");
    sb.append("    instrument: ").append(toIndentedString(instrument)).append("\n");
    sb.append("    connections: ").append(toIndentedString(connections)).append("\n");
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
