package com.skipcart.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.skipcart.dto.User;
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
    JWTClaimsSet claimsSet =
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

    JWSHeader header =
        new JWSHeader.Builder(JWSAlgorithm.HS256).type(new JOSEObjectType("JWT")).build();
    SignedJWT signedJWT = new SignedJWT(header, claimsSet);
    JWSSigner signer = new MACSigner(SECRET_KEY.getBytes());
    signedJWT.sign(signer);
    return signedJWT.serialize();
  }
}
