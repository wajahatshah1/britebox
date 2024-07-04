package com.briteboxbackend.briterbox.repository;




import com.briteboxbackend.briterbox.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByTransactionId(String transactionId);
    List<Bill> findByDetail(Boolean detail);
    List<Bill> findByCleaning(Boolean cleaning);
    List<Bill> findByReady(Boolean ready);
    List<Bill> findByPickup(Boolean pickup);
    List<Bill> findByStatus(String status);
    List<Bill> findByDate(LocalDate date);
    List<Bill> findByPicked(boolean picked);

    List<Bill> findByStatusAndPhoneNumber(String status, String phoneNumber);

    List<Bill> findAllByStatus(String status);

    List<Bill> findByPhoneNumber(String phoneNumber);
}

