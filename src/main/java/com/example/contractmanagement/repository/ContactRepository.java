package com.example.contractmanagement.repository;

import com.example.contractmanagement.model.Contact;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByFirstNameContainingIgnoreCase(String firstName);
    List<Contact> findByLastNameContainingIgnoreCase(String lastName);
    List<Contact> findByEmailContainingIgnoreCase(String email);
    Page<Contact> findAll(Pageable pageable);
}
