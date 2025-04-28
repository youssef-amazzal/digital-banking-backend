package com.example.digitalbankingbackend.repositories;

import com.example.digitalbankingbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer, Long> {

}