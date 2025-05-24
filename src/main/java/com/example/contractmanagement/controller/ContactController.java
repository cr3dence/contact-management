package com.example.contractmanagement.controller;

import com.example.contractmanagement.dto.ContactResponseDTO;
import com.example.contractmanagement.model.Contact;
import com.example.contractmanagement.service.ContactService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ContactResponseDTO> createContact(@RequestBody Contact contact) {
        Contact created = contactService.createContact(contact);
        return ResponseEntity.ok(new ContactResponseDTO(created));
    }

    @GetMapping
    public ResponseEntity<Page<ContactResponseDTO>> getAllContacts(Pageable pageable) {
        Page<Contact> contacts = contactService.getAllContacts(pageable);
        Page<ContactResponseDTO> dtoPage = contacts.map(ContactResponseDTO::new);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> getContactById(@PathVariable Long id) {
        return contactService.getContactById(id)
                .map(contact -> ResponseEntity.ok(new ContactResponseDTO(contact)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> updateContact(@PathVariable Long id, @RequestBody Contact updatedContact) {
        try {
            Contact updated = contactService.updateContact(id, updatedContact);
            return ResponseEntity.ok(new ContactResponseDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ContactResponseDTO>> searchContacts(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email) {

        List<Contact> contacts;

        if (firstName != null) {
            contacts = contactService.searchByFirstName(firstName);
        } else if (lastName != null) {
            contacts = contactService.searchByLastName(lastName);
        } else if (email != null) {
            contacts = contactService.searchByEmail(email);
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<ContactResponseDTO> dtos = contacts.stream()
                .map(ContactResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
