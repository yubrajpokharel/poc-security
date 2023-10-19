package com.skipcart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionResponse {
  @JsonProperty("StatusCode")
  private int statusCode;

  @JsonProperty("Status")
  private boolean status;

  @JsonProperty("Message")
  private String message;

  @JsonProperty("JobId")
  private String jobId;

  @JsonProperty("KeyMessage")
  private String keyMessage;

  @JsonProperty("Result")
  private List<Result> result;

  @JsonProperty("Errors")
  private Object errors;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Result {

    @JsonProperty("permissionid")
    String permissionId;

    @JsonProperty("permissionname")
    String permissionName;

    @JsonProperty("module")
    String module;

    @JsonProperty("permissioncode")
    String permissionCode;

    @JsonProperty("RoleId")
    String roleId;

    @JsonProperty("UserId")
    String userId;
  }
}
