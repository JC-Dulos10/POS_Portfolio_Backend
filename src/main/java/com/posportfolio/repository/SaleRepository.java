package com.posportfolio.repository;

import com.posportfolio.model.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<SaleEntity, Long> {
    Optional<SaleEntity> findByReceiptNumber(String receiptNumber);
    List<SaleEntity> findByUsername(String username);
    List<SaleEntity> findAllByOrderByCreatedAtDesc();
}
