package com.mertalptekin.springbatchchunkoperationsdemo.repository;

import com.mertalptekin.springbatchchunkoperationsdemo.model.CustomerCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCreditRepository extends JpaRepository<CustomerCredit, Long> {
}
