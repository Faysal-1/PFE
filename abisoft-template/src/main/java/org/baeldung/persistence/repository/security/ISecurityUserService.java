package org.baeldung.persistence.repository.security;

public interface ISecurityUserService {

    String validatePasswordResetToken(long id, String token);

}
