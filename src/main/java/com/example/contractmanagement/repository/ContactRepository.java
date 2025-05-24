package com.example.contractmanagement.repository;

import com.example.contractmanagement.model.Contact;
import com.example.contractmanagement.model.User;

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

	Page<Contact> findByUser(User user, Pageable pageable);

	List<Contact> findByFirstNameContainingIgnoreCaseAndUserId(String firstName, Long userId);

	List<Contact> findByLastNameContainingIgnoreCaseAndUserId(String lastName, Long userId);

	List<Contact> findByEmailContainingIgnoreCaseAndUserId(String email, Long userId);

}
