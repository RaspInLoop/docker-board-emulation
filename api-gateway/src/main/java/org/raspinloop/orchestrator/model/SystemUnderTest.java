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
 * SystemUnderTest
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")

@Generated
public class SystemUnderTest   {
  @JsonProperty("boardRef")
  private String boardRef = null;

  @JsonProperty("publicKey")
  private String publicKey = null;

  @JsonProperty("access")
  private String access = null;

  @JsonProperty("id")
  private String id = null;

  public SystemUnderTest boardRef(String boardRef) {
    this.boardRef = boardRef;
    return this;
  }

  /**
   * Get boardRef
   * @return boardRef
   **/
  @Schema(example = "84", required = true, description = "")
      @NotNull

    public String getBoardRef() {
    return boardRef;
  }

  public void setBoardRef(String boardRef) {
    this.boardRef = boardRef;
  }

  public SystemUnderTest publicKey(String publicKey) {
    this.publicKey = publicKey;
    return this;
  }

  /**
   * Get publicKey
   * @return publicKey
   **/
  @Schema(example = "ssh-ed25519 AAC3+if5Sc4wJ/fr+pqXKr", required = true, description = "")
      @NotNull

    public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public SystemUnderTest access(String access) {
    this.access = access;
    return this;
  }

  /**
   * Get access
   * @return access
   **/
  @Schema(example = "ssh://em-587.ssh-access.default.svc.raspinloop.org", description = "")
  
    public String getAccess() {
    return access;
  }

  public void setAccess(String access) {
    this.access = access;
  }

  public SystemUnderTest id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   **/
  @Schema(example = "408-867-5309", description = "")
  
    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SystemUnderTest systemUnderTest = (SystemUnderTest) o;
    return Objects.equals(this.boardRef, systemUnderTest.boardRef) &&
        Objects.equals(this.publicKey, systemUnderTest.publicKey) &&
        Objects.equals(this.access, systemUnderTest.access) &&
        Objects.equals(this.id, systemUnderTest.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(boardRef, publicKey, access, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SystemUnderTest {\n");
    
    sb.append("    boardRef: ").append(toIndentedString(boardRef)).append("\n");
    sb.append("    publicKey: ").append(toIndentedString(publicKey)).append("\n");
    sb.append("    access: ").append(toIndentedString(access)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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
