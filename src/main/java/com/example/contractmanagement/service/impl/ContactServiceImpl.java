package com.example.contractmanagement.service.impl;

import com.example.contractmanagement.model.Contact;
import com.example.contractmanagement.repository.ContactRepository;
import com.example.contractmanagement.service.ContactService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public Page<Contact> getAllContacts(Pageable pageable) {
        return contactRepository.findAll(pageable);
    }

    @Override
    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    @Override
    public Contact updateContact(Long id, Contact updatedContact) {
        return contactRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(updatedContact.getFirstName());
                    existing.setLastName(updatedContact.getLastName());
                    existing.setEmail(updatedContact.getEmail());
                    existing.setPhoneNumber(updatedContact.getPhoneNumber());
                    return contactRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + id));
    }

    @Override
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }

    @Override
    public List<Contact> searchByFirstName(String firstName) {
        return contactRepository.findByFirstNameContainingIgnoreCase(firstName);
    }

    @Override
    public List<Contact> searchByLastName(String lastName) {
        return contactRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    @Override
    public List<Contact> searchByEmail(String email) {
        return contactRepository.findByEmailContainingIgnoreCase(email);
    }
}
