package com.technoelevate.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.technoelevate.payment.pojo.Order;

public interface PaymentRepository extends JpaRepository<Order, Long>{

}
