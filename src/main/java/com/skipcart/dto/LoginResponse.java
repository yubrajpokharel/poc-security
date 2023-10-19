package com.skipcart.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {
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
  private Result result;

  @JsonProperty("Errors")
  private Object errors;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Result {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("UserToken")
    private String userToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @JsonProperty("UserTokenExpires")
    private Date userTokenExpires;

    @JsonProperty("FullName")
    private String fullName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @JsonProperty("Expiry")
    private Date expiry;

    @JsonProperty("RefreshToken")
    private String refreshToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @JsonProperty("RefreshTokenExpires")
    private Date refreshTokenExpires;
  }
}
