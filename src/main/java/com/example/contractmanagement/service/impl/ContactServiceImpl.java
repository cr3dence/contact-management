package com.example.contractmanagement.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.contractmanagement.model.Contact;
import com.example.contractmanagement.model.User;
import com.example.contractmanagement.repository.ContactRepository;
import com.example.contractmanagement.repository.UserRepository;
import com.example.contractmanagement.service.ContactService;

@Service
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isAdmin(User user) {
        return user.getRole().name().equals("ROLE_ADMIN");
    }

    private void checkOwnershipOrAdmin(Contact contact, User user, String action) {
        if (!isAdmin(user) && !contact.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not authorized to " + action + " contact ID: " + contact.getId());
        }
    }

    @Override
    public Contact createContact(Contact contact) {
        contact.setUser(getCurrentUser());
        return contactRepository.save(contact);
    }

    @Override
    public Page<Contact> getAllContacts(Pageable pageable) {
        User user = getCurrentUser();
        Page<Contact> contacts = isAdmin(user)
                ? contactRepository.findAll(pageable)
                : contactRepository.findByUser(user, pageable);

        return contacts;
    }

    @Override
    public Optional<Contact> getContactById(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + id));
        User user = getCurrentUser();
        checkOwnershipOrAdmin(contact, user, "view");
        return Optional.of(contact);
    }

    @Override
    public Contact updateContact(Long id, Contact updatedContact) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + id));
        User user = getCurrentUser();
        checkOwnershipOrAdmin(contact, user, "update");

        contact.setFirstName(updatedContact.getFirstName());
        contact.setLastName(updatedContact.getLastName());
        contact.setEmail(updatedContact.getEmail());
        contact.setPhoneNumber(updatedContact.getPhoneNumber());

        return contactRepository.save(contact);
    }

    @Override
    public void deleteContact(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + id));
        User user = getCurrentUser();
        checkOwnershipOrAdmin(contact, user, "delete");
        contactRepository.deleteById(id);
    }

    @Override
    public List<Contact> searchByFirstName(String firstName) {
        User user = getCurrentUser();
        return isAdmin(user)
                ? contactRepository.findByFirstNameContainingIgnoreCase(firstName)
                : contactRepository.findByFirstNameContainingIgnoreCaseAndUserId(firstName, user.getId());
    }

    @Override
    public List<Contact> searchByLastName(String lastName) {
        User user = getCurrentUser();
        return isAdmin(user)
                ? contactRepository.findByLastNameContainingIgnoreCase(lastName)
                : contactRepository.findByLastNameContainingIgnoreCaseAndUserId(lastName, user.getId());
    }

    @Override
    public List<Contact> searchByEmail(String email) {
        User user = getCurrentUser();
        return isAdmin(user)
                ? contactRepository.findByEmailContainingIgnoreCase(email)
                : contactRepository.findByEmailContainingIgnoreCaseAndUserId(email, user.getId());
    }
}
