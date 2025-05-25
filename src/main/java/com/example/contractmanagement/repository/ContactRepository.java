package com.example.contractmanagement.repository;

import com.example.contractmanagement.model.Contact;
import com.example.contractmanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    boolean existsByUserAndEmail(User user, String email);

    boolean existsByUserAndPhoneNumber(User user, String phoneNumber);

    @Query("SELECT c FROM Contact c WHERE " +
           "(:firstName IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', CAST(:firstName AS string), '%'))) AND " +
           "(:lastName IS NULL OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', CAST(:lastName AS string), '%'))) AND " +
           "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', CAST(:email AS string), '%'))) AND " +
           "(c.user.id = :userId)")
    Page<Contact> searchByFieldsForUser(@Param("firstName") String firstName,
                                        @Param("lastName") String lastName,
                                        @Param("email") String email,
                                        @Param("userId") Long userId,
                                        Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE " +
           "(:firstName IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', CAST(:firstName AS string), '%'))) AND " +
           "(:lastName IS NULL OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', CAST(:lastName AS string), '%'))) AND " +
           "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', CAST(:email AS string), '%')))")
    Page<Contact> searchByFieldsForAdmin(@Param("firstName") String firstName,
                                         @Param("lastName") String lastName,
                                         @Param("email") String email,
                                         Pageable pageable);
}
