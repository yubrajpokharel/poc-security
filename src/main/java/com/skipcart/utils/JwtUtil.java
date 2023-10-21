package com.skipcart.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.skipcart.dto.User;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Anish Panthi
 */
@Component
@Slf4j
public class JwtUtil {

  @Value("${app.secret-key}")
  private String SECRET_KEY;

  @Value("${app.token}")
  private String appToken;

  public String generateToken(User user, String scope) throws JOSEException {
    // Create a JWT Claims Set
    var claimsSet =
        new JWTClaimsSet.Builder()
            .subject(user.getEmail())
            .issuer("https://devapi.skipcart.com")
            .expirationTime(user.getUserTokenExpires()) // As per REST response
            .claim("appToken", appToken)
            .claim("userToken", user.getUserToken())
            .claim("userId", user.getId())
            .claim("scope", scope)
            .jwtID(UUID.randomUUID().toString()) // Unique JWT ID, if required.
            .build();

    var header = new JWSHeader.Builder(JWSAlgorithm.HS256).type(new JOSEObjectType("JWT")).build();
    var signedJWT = new SignedJWT(header, claimsSet);
    var signer = new MACSigner(SECRET_KEY.getBytes());
    signedJWT.sign(signer);
    return signedJWT.serialize();
  }

  public String validateToken(String token) throws JOSEException, ParseException {
    try {
      var signedJWT = SignedJWT.parse(token);
      var verifier = new MACVerifier(SECRET_KEY.getBytes());
      if (signedJWT.verify(verifier)) {
        var claimsSet = signedJWT.getJWTClaimsSet();
        var expirationTime = claimsSet.getExpirationTime();
        if (expirationTime.before(new Date())) {
          log.error("Token is expired");
          throw new RuntimeException("Token is expired");
        }
        log.info("Token is valid");
        return claimsSet.getStringClaim("scope");
      } else {
        log.error("Token is invalid");
        throw new RuntimeException("Token is invalid");
      }
    } catch (Exception e) {
      log.error("Error while validating token", e);
      throw new RuntimeException(e.getMessage());
    }
  }
}
