package com.briteboxbackend.briterbox.repository;




import com.briteboxbackend.briterbox.entities.OurUsers;
import com.briteboxbackend.briterbox.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OurUserRepo extends JpaRepository<OurUsers, Long> {
    Optional<OurUsers> findByPhoneNumber(String phoneNumber);
    Optional<OurUsers> findByPhoneNumberAndRole(String phoneNumber, Role role);
    List<OurUsers> findByRole(Role role);
    boolean existsByRole(Role role);

    boolean existsByPhoneNumber(String phoneNumber);
}
