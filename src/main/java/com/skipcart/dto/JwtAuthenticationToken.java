package com.skipcart.dto;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.security.auth.Subject;

@Getter
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String token;

    public JwtAuthenticationToken(String token) {
        super(null, null);
        this.token = token;
    }

    /**
     * Returns true if the specified subject is implied by this principal.
     *
     * @param subject the {@code Subject}
     * @return true if {@code subject} is non-null and is
     * implied by this principal, or false otherwise.
     * @implSpec The default implementation of this method returns true if
     * {@code subject} is non-null and contains at least one principal that
     * is equal to this principal.
     *
     * <p>Subclasses may override this with a different implementation, if
     * necessary.
     * @since 1.8
     */
    @Override
    public boolean implies(Subject subject) {
        return super.implies(subject);
    }
}
