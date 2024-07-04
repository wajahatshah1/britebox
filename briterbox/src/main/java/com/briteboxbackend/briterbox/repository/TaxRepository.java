package com.briteboxbackend.briterbox.repository;




import com.briteboxbackend.briterbox.entities.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
}
