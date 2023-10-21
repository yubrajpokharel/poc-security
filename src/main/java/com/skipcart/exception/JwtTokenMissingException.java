package com.skipcart.exception;

/**
 * @author Anish Panthi
 */
public class JwtTokenMissingException extends RuntimeException {
  public JwtTokenMissingException(String s) {
    super(s);
  }
}
