package com.myfin.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.myfin.customer.entity.FixedDeposit;

public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {
}

