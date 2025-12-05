package com.myfin.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.myfin.customer.entity.RecurringDeposit;

public interface RecurringDepositRepository extends JpaRepository<RecurringDeposit, Long> {
}

