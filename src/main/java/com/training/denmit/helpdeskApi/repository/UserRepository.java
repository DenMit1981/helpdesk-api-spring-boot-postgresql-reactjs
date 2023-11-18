package com.training.denmit.helpdeskApi.repository;

import com.training.denmit.helpdeskApi.model.User;
import com.training.denmit.helpdeskApi.model.enums.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String login);

    List<User> findByRole(Role role);

    @Query("from User u where u.role = 'ROLE_MANAGER'")
    List<User> findAllManagers();

    @Query("from User u where u.role = 'ROLE_ENGINEER'")
    List<User> findAllEngineers();
}