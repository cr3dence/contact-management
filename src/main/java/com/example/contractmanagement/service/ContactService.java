package com.example.contractmanagement.service;

import com.example.contractmanagement.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ContactService {

    Contact createContact(Contact contact);

    Page<Contact> getAllContacts(Pageable pageable);

    Optional<Contact> getContactById(Long id);

    Contact updateContact(Long id, Contact contact);

    void deleteContact(Long id);

    List<Contact> searchByFirstName(String firstName);
    List<Contact> searchByLastName(String lastName);
    List<Contact> searchByEmail(String email);
}
