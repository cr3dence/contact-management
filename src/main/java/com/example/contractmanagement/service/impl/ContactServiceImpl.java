package com.example.contractmanagement.service.impl;

import com.example.contractmanagement.model.Contact;
import com.example.contractmanagement.model.User;
import com.example.contractmanagement.repository.ContactRepository;
import com.example.contractmanagement.repository.UserRepository;
import com.example.contractmanagement.service.ContactService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^\\d{10}$");

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

    private void validateContact(Contact contact) {
        if (contact.getFirstName() == null || contact.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }

        boolean hasEmail = contact.getEmail() != null && !contact.getEmail().trim().isEmpty();
        boolean hasPhone = contact.getPhoneNumber() != null && !contact.getPhoneNumber().trim().isEmpty();

        if (!hasEmail && !hasPhone) {
            throw new IllegalArgumentException("At least one of email or phone number must be provided");
        }

        if (hasEmail && !EMAIL_REGEX.matcher(contact.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (hasPhone && !PHONE_REGEX.matcher(contact.getPhoneNumber()).matches()) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits");
        }
    }

    @Override
    public Contact createContact(Contact contact) {
        validateContact(contact);
        contact.setUser(getCurrentUser());
        return contactRepository.save(contact);
    }

    @Override
    public Page<Contact> getAllContacts(Pageable pageable) {
        User user = getCurrentUser();
        return isAdmin(user)
                ? contactRepository.findAll(pageable)
                : contactRepository.findByUser(user, pageable);
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
        validateContact(updatedContact);

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
        if (!EMAIL_REGEX.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format for search");
        }

        User user = getCurrentUser();
        return isAdmin(user)
                ? contactRepository.findByEmailContainingIgnoreCase(email)
                : contactRepository.findByEmailContainingIgnoreCaseAndUserId(email, user.getId());
    }

    @Override
    public Page<Contact> searchByFields(String firstName, String lastName, String email, Pageable pageable) {
        if (firstName == null && lastName == null && email == null) {
            throw new IllegalArgumentException("At least one search parameter must be provided.");
        }

        if (email != null && !EMAIL_REGEX.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format for search.");
        }

        User user = getCurrentUser();
        if (isAdmin(user)) {
            return contactRepository.searchByFieldsForAdmin(firstName, lastName, email, pageable);
        } else {
            return contactRepository.searchByFieldsForUser(firstName, lastName, email, user.getId(), pageable);
        }
    }

}
