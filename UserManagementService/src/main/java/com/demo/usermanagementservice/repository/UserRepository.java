package com.demo.usermanagementservice.repository;

import com.demo.usermanagementservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findUsersByArchivedFalse();

    List<User> findUsersByIdInAndArchivedFalse(List<Long> ids);

    Optional<User> findUserByIdAndArchivedFalse(Long id);

    User findUserByEmailEqualsIgnoreCaseAndArchivedFalse(String email);

}
