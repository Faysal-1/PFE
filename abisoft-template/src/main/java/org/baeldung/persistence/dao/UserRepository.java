package org.baeldung.persistence.dao;

import java.util.Collection;
import java.util.List;

import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    @Override
    void delete(User user);
    List<User> findByRoles_Name(String roleName);
    List<User> findAllByRolesIn(Collection<Role> role);
    
}
