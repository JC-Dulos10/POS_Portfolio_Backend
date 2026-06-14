package com.posportfolio.repository;

import com.posportfolio.model.SaleItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItemEntity, Long> {
    List<SaleItemEntity> findBySaleId(Long saleId);
}
